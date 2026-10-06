package br.com.back_end.simasp.previsao.service;

import br.com.back_end.simasp.exception.RecursoNaoEncontradoException;
import br.com.back_end.simasp.leitura.entity.Leitura;
import br.com.back_end.simasp.leitura.repository.LeituraRepository;
import br.com.back_end.simasp.previsao.client.PrevisaoIaClient;
import br.com.back_end.simasp.previsao.client.dto.ItemLoteIa;
import br.com.back_end.simasp.previsao.client.dto.PrevisaoIaRequest;
import br.com.back_end.simasp.previsao.client.dto.PrevisaoIaResponse;
import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import br.com.back_end.simasp.previsao.dto.PrevisaoResponse;
import br.com.back_end.simasp.previsao.entity.Previsao;
import br.com.back_end.simasp.previsao.mapper.PrevisaoMapper;
import br.com.back_end.simasp.previsao.repository.PrevisaoRepository;
import br.com.back_end.simasp.sensor.entity.Sensor;
import br.com.back_end.simasp.sensor.enums.StatusSensor;
import br.com.back_end.simasp.sensor.repository.SensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orquestra a previsão: junta sensor + última leitura, chama o serviço de IA e guarda o
 * resultado.
 *
 * <p>Duas decisões importantes:</p>
 * <ul>
 *   <li><b>A chuva é do serviço de IA.</b> O backend manda só posição e leitura do sensor. Assim
 *       a chuva usada ao vivo vem da mesma fonte e do mesmo código do treino;</li>
 *   <li><b>Sem resposta, nada é inventado.</b> Se o serviço não responde, não gravamos uma
 *       previsão "chutada": a última previsão fica no mapa marcada como DESATUALIZADA. Nível de
 *       risco sem modelo seria um número sem significado para o usuário.</li>
 * </ul>
 */
@Service
public class PrevisaoService {

    private static final Logger log = LoggerFactory.getLogger(PrevisaoService.class);

    private final PrevisaoIaClient cliente;
    private final PrevisaoRepository repository;
    private final SensorRepository sensorRepository;
    private final LeituraRepository leituraRepository;
    private final PrevisaoMapper mapper;
    private final PrevisaoProperties propriedades;
    private final DemonstracaoService demonstracao;

    public PrevisaoService(PrevisaoIaClient cliente, PrevisaoRepository repository,
                           SensorRepository sensorRepository, LeituraRepository leituraRepository,
                           PrevisaoMapper mapper, PrevisaoProperties propriedades,
                           DemonstracaoService demonstracao) {
        this.cliente = cliente;
        this.repository = repository;
        this.sensorRepository = sensorRepository;
        this.leituraRepository = leituraRepository;
        this.mapper = mapper;
        this.propriedades = propriedades;
        this.demonstracao = demonstracao;
    }

    /** Recalcula a previsão de um sensor agora (chamado ao chegar leitura nova, por exemplo). */
    @Transactional
    public Optional<PrevisaoResponse> atualizarSensor(Long sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(
                () -> new RecursoNaoEncontradoException("O sensor com ID " + sensorId + " não foi encontrado."));
        Leitura leitura = leituraRepository.findFirstBySensorIdOrderByDataLeituraDesc(sensorId)
                .filter(this::leituraRecente)
                .orElse(null);

        return cliente.prever(montarRequisicao(sensor, leitura))
                .map(resposta -> salvar(resposta, sensor))
                .map(mapper::paraResposta);
    }

    /**
     * Recalcula todos os sensores ativos numa chamada de lote por bloco.
     *
     * @return quantas previsões foram gravadas
     */
    @Transactional
    public int atualizarTodos() {
        List<Sensor> sensores = sensorRepository.findAll().stream()
                .filter(s -> s.getStatus() == StatusSensor.ATIVO)
                .toList();
        if (sensores.isEmpty() || !cliente.disponivel()) {
            return 0;
        }

        Map<Long, Leitura> leituras = leituraRepository.buscarUltimaDeCadaSensor().stream()
                .filter(this::leituraRecente)
                .collect(Collectors.toMap(l -> l.getSensor().getId(), Function.identity(), (a, b) -> a));
        Map<Long, Sensor> porId = sensores.stream().collect(Collectors.toMap(Sensor::getId, Function.identity()));

        int gravadas = 0;
        for (int inicio = 0; inicio < sensores.size(); inicio += propriedades.tamanhoLote()) {
            List<Sensor> bloco = sensores.subList(inicio, Math.min(inicio + propriedades.tamanhoLote(), sensores.size()));
            List<PrevisaoIaRequest> requisicoes = bloco.stream()
                    .map(sensor -> montarRequisicao(sensor, leituras.get(sensor.getId())))
                    .toList();

            for (ItemLoteIa item : cliente.preverLote(requisicoes)) {
                if (!item.sucesso() || item.previsao() == null) {
                    log.warn("Previsão falhou para o sensor {}: {} ({})", item.sensorId(), item.erro(), item.codigoErro());
                    continue;
                }
                Sensor sensor = porId.get(item.sensorId());
                if (sensor == null) {
                    log.warn("O serviço de IA devolveu um sensorId desconhecido: {}", item.sensorId());
                    continue;
                }
                salvar(item.previsao(), sensor);
                gravadas++;
            }
        }
        log.info("Previsões atualizadas: {} de {} sensores ativos", gravadas, sensores.size());
        return gravadas;
    }

    /** Última previsão de um sensor (ou SEM_PREVISAO quando ele nunca recebeu uma). */
    @Transactional(readOnly = true)
    public PrevisaoResponse ultimaDoSensor(Long sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(
                () -> new RecursoNaoEncontradoException("O sensor com ID " + sensorId + " não foi encontrado."));
        return repository.findFirstBySensorIdOrderByGeradaEmDesc(sensorId)
                .map(mapper::paraResposta)
                .orElseGet(() -> mapper.semPrevisao(sensor));
    }

    /** Mapa: um item por sensor, sempre — com previsão válida, desatualizada ou sem previsão. */
    @Transactional(readOnly = true)
    public List<PrevisaoResponse> mapa() {
        Map<Long, Previsao> ultimas = repository.buscarUltimaDeCadaSensor().stream()
                .filter(p -> p.getSensor() != null)
                .collect(Collectors.toMap(p -> p.getSensor().getId(), Function.identity(), (a, b) -> a));

        return sensorRepository.findAll().stream()
                .map(sensor -> {
                    Previsao previsao = ultimas.get(sensor.getId());
                    return previsao == null ? mapper.semPrevisao(sensor) : mapper.paraResposta(previsao);
                })
                .toList();
    }

    private Previsao salvar(PrevisaoIaResponse resposta, Sensor sensor) {
        return repository.save(mapper.paraEntidade(resposta, sensor, propriedades.validade()));
    }

    /**
     * Monta o pedido ao serviço de IA.
     *
     * <p>No fluxo normal vão só a posição e a última leitura. Com o modo de demonstração ligado
     * para este sensor, entram a série de chuva do cenário e, se informadas, as leituras
     * simuladas de água e lixo (que têm prioridade sobre a leitura real).</p>
     */
    private PrevisaoIaRequest montarRequisicao(Sensor sensor, Leitura leitura) {
        Double nivelAgua = leitura == null ? null : leitura.getNivelAgua();
        Double porcentagemLixo = leitura == null ? null : leitura.getPorcentagemDesperdicio();
        Double chuvaMm = leitura == null ? null : leitura.getChuvaMM();
        List<Double> chuvaHoraria = null;

        Optional<DemonstracaoService.Estado> cenario = demonstracao.paraSensor(sensor.getId());
        if (cenario.isPresent()) {
            DemonstracaoService.Estado simulado = cenario.get();
            chuvaHoraria = simulado.cenario().serie();
            chuvaMm = null;  // a chuva real da leitura não pode se misturar com a simulada
            if (simulado.nivelAgua() != null) {
                nivelAgua = simulado.nivelAgua();
            }
            if (simulado.porcentagemLixo() != null) {
                porcentagemLixo = simulado.porcentagemLixo();
            }
        }

        return new PrevisaoIaRequest(
                sensor.getId(),
                paraDouble(sensor.getLatitude()),
                paraDouble(sensor.getLongitude()),
                OffsetDateTime.now(),
                nivelAgua,
                porcentagemLixo,
                chuvaMm,
                chuvaHoraria);
    }

    /** Leitura velha demais não vale como "estado atual" do sensor. */
    private boolean leituraRecente(Leitura leitura) {
        return leitura.getDataLeitura() != null
                && leitura.getDataLeitura().isAfter(LocalDateTime.now().minus(propriedades.idadeMaximaLeitura()));
    }

    private double paraDouble(BigDecimal valor) {
        return valor == null ? 0.0 : valor.doubleValue();
    }
}

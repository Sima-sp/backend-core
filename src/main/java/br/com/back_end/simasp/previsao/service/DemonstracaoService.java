package br.com.back_end.simasp.previsao.service;

import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import br.com.back_end.simasp.previsao.enums.CenarioChuvaEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Modo de demonstração: troca a chuva real por um cenário simulado.
 *
 * <p><b>Por que existe.</b> Num dia sem chuva todas as previsões ficam em BAIXO — o que está
 * certo, mas não deixa mostrar o sistema funcionando. Com um cenário ligado, o backend manda a
 * série de chuva junto com cada pedido ao serviço de IA, e o modelo responde como se estivesse
 * chovendo. O modelo, os limiares e o ajuste pelo sensor são os de verdade; só a chuva é
 * inventada.</p>
 *
 * <p><b>Cuidados.</b></p>
 * <ul>
 *   <li>Só liga com {@code ia.demonstracao-habilitada=true}. O padrão é desligado, para ninguém
 *       simular chuva num ambiente de verdade por engano;</li>
 *   <li>desliga sozinho depois de um tempo ({@code ia.demonstracao-duracao}), caso alguém
 *       esqueça ligado;</li>
 *   <li>toda previsão feita assim sai marcada ({@code simulada = true} na resposta e
 *       {@code TX_FONTE_CHUVA = 'INFORMADA'} no banco), então o app pode avisar na tela e o
 *       histórico pode ser filtrado.</li>
 * </ul>
 *
 * <p>O estado fica em memória: reiniciar o backend encerra a demonstração.</p>
 */
@Service
public class DemonstracaoService {

    private static final Logger log = LoggerFactory.getLogger(DemonstracaoService.class);

    private final PrevisaoProperties propriedades;
    private final AtomicReference<Estado> estado = new AtomicReference<>();

    public DemonstracaoService(PrevisaoProperties propriedades) {
        this.propriedades = propriedades;
    }

    /** Cenário em vigor e a quem ele se aplica. */
    public record Estado(
            CenarioChuvaEnum cenario,
            Set<Long> sensorIds,
            Double nivelAgua,
            Double porcentagemLixo,
            Instant expiraEm
    ) {
        /** Sem lista de sensores, o cenário vale para todos. */
        public boolean valePara(Long sensorId) {
            return sensorIds.isEmpty() || sensorIds.contains(sensorId);
        }
    }

    /** True quando o ambiente permite ligar a demonstração. */
    public boolean habilitada() {
        return propriedades.demonstracaoHabilitada();
    }

    /**
     * Liga um cenário.
     *
     * @param duracao nula = a duração padrão das propriedades
     * @throws IllegalStateException quando a demonstração não está habilitada neste ambiente
     */
    public Estado iniciar(CenarioChuvaEnum cenario, Set<Long> sensorIds, Double nivelAgua,
                          Double porcentagemLixo, Duration duracao) {
        if (!habilitada()) {
            throw new IllegalStateException("Modo de demonstração desabilitado (ia.demonstracao-habilitada=false).");
        }
        Duration validade = duracao == null ? propriedades.demonstracaoDuracao() : duracao;
        Estado novo = new Estado(
                cenario,
                sensorIds == null ? Set.of() : Set.copyOf(sensorIds),
                nivelAgua,
                porcentagemLixo,
                Instant.now().plus(validade));
        estado.set(novo);
        log.warn("MODO DE DEMONSTRAÇÃO LIGADO: cenário {} por {} — as previsões são simuladas.", cenario, validade);
        return novo;
    }

    /** Desliga a demonstração; a próxima previsão volta a usar a chuva real. */
    public void encerrar() {
        if (estado.getAndSet(null) != null) {
            log.warn("Modo de demonstração encerrado — previsões voltam a usar a chuva real.");
        }
    }

    /** Cenário em vigor, se houver. Um cenário vencido é descartado aqui. */
    public Optional<Estado> ativa() {
        Estado atual = estado.get();
        if (atual == null) {
            return Optional.empty();
        }
        if (!atual.expiraEm().isAfter(Instant.now())) {
            if (estado.compareAndSet(atual, null)) {
                log.warn("Modo de demonstração expirou — previsões voltam a usar a chuva real.");
            }
            return Optional.empty();
        }
        return Optional.of(atual);
    }

    /** Cenário que vale para um sensor específico, se houver. */
    public Optional<Estado> paraSensor(Long sensorId) {
        return ativa().filter(atual -> atual.valePara(sensorId));
    }
}

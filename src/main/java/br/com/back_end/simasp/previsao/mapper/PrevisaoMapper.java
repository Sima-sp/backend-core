package br.com.back_end.simasp.previsao.mapper;

import br.com.back_end.simasp.leitura.enums.NivelRiscoEnum;
import br.com.back_end.simasp.previsao.client.dto.PrevisaoIaResponse;
import br.com.back_end.simasp.previsao.dto.PrevisaoResponse;
import br.com.back_end.simasp.previsao.entity.Previsao;
import br.com.back_end.simasp.previsao.enums.OrigemPrevisaoEnum;
import br.com.back_end.simasp.previsao.enums.StatusPrevisaoEnum;
import br.com.back_end.simasp.sensor.entity.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * Conversões entre a resposta do serviço de IA, a entidade e o DTO do app.
 *
 * <p>Escrito à mão (e não com MapStruct) porque tem conversão de fuso, texto para enum e
 * junção de lista em uma coluna — regras que ficam mais claras explícitas.</p>
 */
@Component
public class PrevisaoMapper {

    private static final Logger log = LoggerFactory.getLogger(PrevisaoMapper.class);
    private static final ZoneId FUSO_LOCAL = ZoneId.of("America/Sao_Paulo");
    private static final String SEPARADOR_MOTIVOS = ";";

    /** Resposta do serviço de IA → entidade pronta para salvar. */
    public Previsao paraEntidade(PrevisaoIaResponse resposta, Sensor sensor, Duration validadePadrao) {
        Previsao previsao = new Previsao();
        previsao.setSensor(sensor);
        previsao.setProbabilidadeAlagamento(resposta.probabilidadeAlagamento());
        previsao.setNivelRisco(paraNivel(resposta.nivelRisco()));
        previsao.setNivelRiscoModelo(paraNivel(resposta.nivelRiscoModelo()));
        previsao.setJanelaHoras(resposta.janelaHoras() == null ? 3 : resposta.janelaHoras());

        LocalDateTime geradaEm = paraLocal(resposta.geradaEm());
        previsao.setGeradaEm(geradaEm == null ? LocalDateTime.now() : geradaEm);
        LocalDateTime validaAte = paraLocal(resposta.validaAte());
        previsao.setValidaAte(validaAte == null ? previsao.getGeradaEm().plus(validadePadrao) : validaAte);
        previsao.setHoraReferencia(paraLocal(resposta.horaReferencia()));

        previsao.setOrigem(paraOrigem(resposta.origem()));
        previsao.setModeloVersao(resposta.modeloVersao());
        previsao.setChuvaRecente3hMm(resposta.chuvaRecente3hMm());
        previsao.setChuvaPrevista3hMm(resposta.chuvaPrevista3hMm());
        previsao.setFonteChuva(resposta.fonteChuva());
        previsao.setMedicaoTransbordando(Boolean.TRUE.equals(resposta.medicaoTransbordando()));
        previsao.setAjusteSensorAplicado(Boolean.TRUE.equals(resposta.ajusteSensorAplicado()));
        previsao.setSemLeituraSensor(Boolean.TRUE.equals(resposta.semLeituraSensor()));
        previsao.setMotivosAjuste(juntarMotivos(resposta.motivosAjuste()));
        previsao.setPontoId(resposta.pontoId());
        previsao.setDistanciaPontoM(resposta.distanciaPontoM());
        return previsao;
    }

    /** Entidade → DTO do app, com o {@code status} calculado na hora da consulta. */
    public PrevisaoResponse paraResposta(Previsao previsao) {
        Sensor sensor = previsao.getSensor();
        return new PrevisaoResponse(
                sensor == null ? null : sensor.getId(),
                sensor == null ? null : paraDouble(sensor.getLatitude()),
                sensor == null ? null : paraDouble(sensor.getLongitude()),
                sensor == null ? null : sensor.getVizinhanca(),
                statusDe(previsao),
                previsao.getProbabilidadeAlagamento(),
                previsao.getNivelRisco(),
                previsao.getNivelRiscoModelo(),
                previsao.getJanelaHoras(),
                previsao.getGeradaEm(),
                previsao.getValidaAte(),
                previsao.getHoraReferencia(),
                previsao.getMedicaoTransbordando(),
                previsao.getAjusteSensorAplicado(),
                previsao.getSemLeituraSensor(),
                separarMotivos(previsao.getMotivosAjuste()),
                previsao.getChuvaRecente3hMm(),
                previsao.getChuvaPrevista3hMm(),
                previsao.getOrigem(),
                previsao.getModeloVersao());
    }

    /** Sensor sem nenhuma previsão: o mapa mostra o ponto assim mesmo. */
    public PrevisaoResponse semPrevisao(Sensor sensor) {
        return new PrevisaoResponse(
                sensor.getId(), paraDouble(sensor.getLatitude()), paraDouble(sensor.getLongitude()),
                sensor.getVizinhanca(), StatusPrevisaoEnum.SEM_PREVISAO,
                null, null, null, null, null, null, null,
                false, false, true, List.of(), null, null, null, null);
    }

    public StatusPrevisaoEnum statusDe(Previsao previsao) {
        if (previsao.getValidaAte() == null || previsao.getValidaAte().isBefore(LocalDateTime.now())) {
            return StatusPrevisaoEnum.DESATUALIZADA;
        }
        return StatusPrevisaoEnum.VALIDA;
    }

    /** "BAIXO"/"MEDIO"/"ALTO"/"CRITICO" (sem acento, como o enum do backend). */
    public NivelRiscoEnum paraNivel(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return NivelRiscoEnum.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException erro) {
            log.warn("Nível de risco desconhecido vindo do serviço de IA: {}", texto);
            return null;
        }
    }

    private OrigemPrevisaoEnum paraOrigem(String texto) {
        if (texto == null) {
            return OrigemPrevisaoEnum.REGRAS;
        }
        try {
            return OrigemPrevisaoEnum.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException erro) {
            log.warn("Origem desconhecida vinda do serviço de IA: {}", texto);
            return OrigemPrevisaoEnum.REGRAS;
        }
    }

    private LocalDateTime paraLocal(OffsetDateTime instante) {
        return instante == null ? null : instante.atZoneSameInstant(FUSO_LOCAL).toLocalDateTime();
    }

    private Double paraDouble(BigDecimal valor) {
        return valor == null ? null : valor.doubleValue();
    }

    private String juntarMotivos(List<String> motivos) {
        if (motivos == null || motivos.isEmpty()) {
            return null;
        }
        String texto = String.join(SEPARADOR_MOTIVOS, motivos);
        return texto.length() > 500 ? texto.substring(0, 500) : texto;
    }

    private List<String> separarMotivos(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return Arrays.stream(texto.split(SEPARADOR_MOTIVOS)).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}

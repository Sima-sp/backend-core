package br.com.back_end.simasp.previsao.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Resposta de {@code /predict/regioes}: as regiões vêm da mais crítica para a menos.
 *
 * <p>{@code fonteChuva = "INFORMADA"} indica chuva simulada (modo de demonstração); o app deve
 * avisar na tela.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegioesIaResponse(
        OffsetDateTime geradaEm,
        OffsetDateTime validaAte,
        OffsetDateTime horaReferencia,
        Integer janelaHoras,
        String origem,
        String modeloVersao,
        Integer minPontosRegiao,
        String fonteChuva,
        List<RegiaoIaResponse> regioes,
        List<String> avisos
) {
}

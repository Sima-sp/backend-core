package br.com.back_end.simasp.previsao.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.util.List;

/** Resposta de {@code GET /predict/regioes}: as regiões vêm da mais crítica para a menos. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegioesIaResponse(
        OffsetDateTime geradaEm,
        OffsetDateTime validaAte,
        OffsetDateTime horaReferencia,
        Integer janelaHoras,
        String origem,
        String modeloVersao,
        Integer minPontosRegiao,
        List<RegiaoIaResponse> regioes,
        List<String> avisos
) {
}

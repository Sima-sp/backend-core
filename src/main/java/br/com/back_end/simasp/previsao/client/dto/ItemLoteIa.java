package br.com.back_end.simasp.previsao.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Um item de {@code POST /predict/batch}: a lista devolvida mantém a ordem da entrada. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemLoteIa(
        Long sensorId,
        boolean sucesso,
        PrevisaoIaResponse previsao,
        String erro,
        String codigoErro
) {
}

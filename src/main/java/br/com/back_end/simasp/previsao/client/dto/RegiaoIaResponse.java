package br.com.back_end.simasp.previsao.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Risco de uma subprefeitura, vindo de {@code GET /predict/regioes}.
 *
 * <p>{@code cobertura = "SEM_COBERTURA"} significa que a região tem poucos pontos monitorados
 * para falar por ela inteira: o nível vem nulo, e isso <b>não</b> é "sem risco".</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegiaoIaResponse(
        String regiaoId,
        String regiao,
        String zona,
        String nivelRisco,
        Double probabilidadeMaxima,
        String pontoId,
        String logradouro,
        Integer pontosMonitorados,
        String cobertura
) {
}

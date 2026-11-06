package br.com.back_end.simasp.previsao.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Resposta de {@code POST /predict}.
 *
 * <p>{@code probabilidadeAlagamento} é {@code Double} de propósito: vem nula quando o serviço
 * está em modo REGRAS. Campos desconhecidos são ignorados, então uma versão nova do serviço de
 * IA não quebra o backend.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PrevisaoIaResponse(
        Long sensorId,
        Double probabilidadeAlagamento,
        String nivelRisco,
        String nivelRiscoModelo,
        Integer janelaHoras,
        OffsetDateTime geradaEm,
        OffsetDateTime validaAte,
        OffsetDateTime horaReferencia,
        Boolean medicaoTransbordando,
        Boolean ajusteSensorAplicado,
        List<String> motivosAjuste,
        Boolean semLeituraSensor,
        String fonteChuva,
        Double chuvaRecente3hMm,
        Double chuvaPrevista3hMm,
        String origem,
        String modeloVersao,
        String pontoId,
        Double distanciaPontoM,
        List<String> avisos
) {
}

package br.com.back_end.simasp.previsao.dto;

import br.com.back_end.simasp.leitura.enums.NivelRiscoEnum;
import br.com.back_end.simasp.previsao.enums.OrigemPrevisaoEnum;
import br.com.back_end.simasp.previsao.enums.StatusPrevisaoEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Previsão como o app e o site recebem.
 *
 * <p>{@code status} evita o ponto sumir do mapa quando a previsão envelhece: o app mostra o
 * sensor com o aviso "desatualizada" em vez de nada. Quando
 * {@code medicaoTransbordando = true}, o app deve trocar a porcentagem por "Transbordando
 * agora (medido)" — a probabilidade continua sendo a do modelo (R5).</p>
 */
public record PrevisaoResponse(
        Long sensorId,
        Double latitude,
        Double longitude,
        String vizinhanca,
        StatusPrevisaoEnum status,
        Double probabilidadeAlagamento,
        NivelRiscoEnum nivelRisco,
        NivelRiscoEnum nivelRiscoModelo,
        Integer janelaHoras,
        LocalDateTime geradaEm,
        LocalDateTime validaAte,
        LocalDateTime horaReferencia,
        Boolean medicaoTransbordando,
        Boolean ajusteSensorAplicado,
        Boolean semLeituraSensor,
        List<String> motivosAjuste,
        Double chuvaRecente3hMm,
        Double chuvaPrevista3hMm,
        OrigemPrevisaoEnum origem,
        String modeloVersao
) {
}

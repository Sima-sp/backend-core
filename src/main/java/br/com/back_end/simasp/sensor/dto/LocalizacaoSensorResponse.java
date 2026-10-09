package br.com.back_end.simasp.sensor.dto;

import java.math.BigDecimal;

public record LocalizacaoSensorResponse(
        BigDecimal latitude,
        BigDecimal longitude,
        String vizinhanca
) {
}

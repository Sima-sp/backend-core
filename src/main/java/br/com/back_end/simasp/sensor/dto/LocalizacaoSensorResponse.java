package br.com.back_end.simasp.sensor.dto;

public record LocalizacaoSensorResponse(
        Double latitude,
        Double longitude,
        String vizinhanca
) {
}

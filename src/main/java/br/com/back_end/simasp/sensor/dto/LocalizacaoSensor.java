package br.com.back_end.simasp.sensor.dto;

public record LocalizacaoSensor(
        Double latitude,
        Double longitude,
        String vizinhanca
) {
}

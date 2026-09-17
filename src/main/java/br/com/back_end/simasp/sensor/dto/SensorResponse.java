package br.com.back_end.simasp.sensor.dto;

import br.com.back_end.simasp.sensor.enums.StatusSensor;

import java.time.LocalDateTime;

public record SensorResponse(
        Double latitude,
        Double longitude,
        String vizinhanca,
        StatusSensor status,
        LocalDateTime dataInstalacao
) {
}

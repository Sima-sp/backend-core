package br.com.back_end.simasp.sensor.dto;

import br.com.back_end.simasp.sensor.enums.StatusSensor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SensorResponse(
        BigDecimal latitude,
        BigDecimal longitude,
        String vizinhanca,
        StatusSensor status,
        LocalDateTime dataInstalacao
) {
}

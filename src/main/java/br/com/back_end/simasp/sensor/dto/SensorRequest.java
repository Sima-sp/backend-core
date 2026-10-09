package br.com.back_end.simasp.sensor.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SensorRequest(

        @NotNull(message = "A latitude deve ser inserida.")
        @Digits(integer = 3, fraction = 6, message = "Latitude deve possuir até 3 dígitos inteiros e 6 decimais.")
        @DecimalMin(value = "-90.000000", message = "Latitude não pode ser menor que -90.")
        @DecimalMax(value = "90.000000", message = "Latitude não pode ser maior que -90.")
        BigDecimal latitude,

        @NotNull(message = "A longitude deve ser inserida.")
        @Digits(integer = 3, fraction = 6, message = "Longitude deve possuir até 3 dígitos inteiros e 6 decimais.")
        @DecimalMin(value = "-180.000000", message = "Longitude não pode ser menor que -180.")
        @DecimalMax(value = "180.000000", message = "Longitude não pode ser maior que 180.")
        BigDecimal longitude,

        @NotNull(message = "A vizinhança deve ser inserida.")
        @Size(max = 120, message = "O número de caracteres de vizinhança não pode passar de 120.")
        String vizinhanca,

        @NotNull(message = "A data deve ser inserida.")
        @PastOrPresent(message = "A data de instalação deve estar na data presente ou passada.")
        LocalDateTime dataInstalacao



) {
}

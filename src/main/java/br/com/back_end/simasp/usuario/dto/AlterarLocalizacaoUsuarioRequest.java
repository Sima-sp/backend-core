package br.com.back_end.simasp.usuario.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AlterarLocalizacaoUsuarioRequest(
        @NotNull(message = "A latitude deve ser inserida.")
        @Digits(integer = 3, fraction = 6, message = "Latitude deve possuir até 3 dígitos inteiros e 6 decimais.")
        @DecimalMin(value = "-90.000000", message = "Latitude não pode ser menor que -90.")
        @DecimalMax(value = "90.000000", message = "Latitude não pode ser maior que -90.")
        BigDecimal latitude,

        @NotNull(message = "A longitude deve ser inserida.")
        @Digits(integer = 3, fraction = 6, message = "Longitude deve possuir até 3 dígitos inteiros e 6 decimais.")
        @DecimalMin(value = "-180.000000", message = "Longitude não pode ser menor que -180.")
        @DecimalMax(value = "180.000000", message = "Longitude não pode ser maior que 180.")
        BigDecimal longitude
        ) {
}

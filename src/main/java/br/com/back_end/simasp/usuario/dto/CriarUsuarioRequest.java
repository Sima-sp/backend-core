package br.com.back_end.simasp.usuario.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CriarUsuarioRequest(
        @NotBlank(message = "O e-mail deve ser inserido")
        @Email(message = "O e-mail deve ser válido.")
        String email,

        @NotBlank(message = "telefone deve ser inserido")
        @Pattern(
                regexp = "\\d{10,11}",
                message = "O telefone deve possuir 10 ou 11 números."
        )
        String telefone,

        @NotBlank
        @Size(min = 8, max = 72, message = "A senha deve ter entre 9 a 72 caracteres.")
        String senhaHash,

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

        @NotBlank(message = "A permissão de alerta deve ser inserida.")
        @Size(max = 3, message = "Tamanho da permissão de alerta excedida.")
        Boolean permissaoAlerta
) {
}

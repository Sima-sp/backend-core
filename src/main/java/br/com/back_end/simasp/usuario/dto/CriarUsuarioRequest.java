package br.com.back_end.simasp.usuario.dto;

import br.com.back_end.simasp.usuario.enums.SimNaoEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarUsuarioRequest(
        @NotBlank(message = "O e-mail deve ser inserido")
        @Email(message = "O e-mail deve ser válido.")
        String email,

        @NotBlank
        @Size(min = 8, max = 72, message = "A senha deve ter entre 9 a 72 caracteres.")
        String senhaHash,

        @NotBlank(message = "A latitude deve ser inserida.")
        Double latitude,

        @NotBlank(message = "A latitude deve ser inserida.")
        Double longitude,

        @NotBlank(message = "A permissão de localização deve ser inserida.")
        @Size(max = 3, message = "Tamanho da permissão de localização excedida.")
        SimNaoEnum permissaoLocalizacao,

        @NotBlank(message = "A permissão de alerta deve ser inserida.")
        @Size(max = 3, message = "Tamanho da permissão de alerta excedida.")
        SimNaoEnum permissaoAlerta
) {
}

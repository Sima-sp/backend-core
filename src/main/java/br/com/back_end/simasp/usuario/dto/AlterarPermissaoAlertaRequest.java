package br.com.back_end.simasp.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarPermissaoAlertaRequest(
        @NotBlank(message = "A permissão de alerta deve ser inserida.")
        @Size(max = 3, message = "Tamanho da permissão de alerta excedida.")
        Boolean permissaoAlerta
) {
}

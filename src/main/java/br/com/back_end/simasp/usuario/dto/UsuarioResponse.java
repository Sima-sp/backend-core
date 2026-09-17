package br.com.back_end.simasp.usuario.dto;

import br.com.back_end.simasp.usuario.enums.SimNaoEnum;
import br.com.back_end.simasp.usuario.enums.TipoUsuarioEnum;

public record UsuarioResponse(
        String email,
        Double latitude,
        Double longitude,
        SimNaoEnum permissaoLocalizacao,
        SimNaoEnum permissaoAlerta,
        TipoUsuarioEnum tipoUsuario
) {
}

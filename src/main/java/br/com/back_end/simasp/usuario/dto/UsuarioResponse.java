package br.com.back_end.simasp.usuario.dto;

import br.com.back_end.simasp.usuario.enums.TipoUsuarioEnum;

import java.math.BigDecimal;

public record UsuarioResponse(
        String email,
        String telefone,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean permissaoAlerta,
        TipoUsuarioEnum tipoUsuario,
        Boolean ativo
) {
}

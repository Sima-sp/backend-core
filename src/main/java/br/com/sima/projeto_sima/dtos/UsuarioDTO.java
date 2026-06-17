package br.com.sima.projeto_sima.dtos;

import br.com.sima.projeto_sima.enums.SimNaoEnum;

public record UsuarioDTO(
        Integer id,
        Double latitude,
        Double longitude,
        SimNaoEnum permissaoLocalizacao,
        SimNaoEnum permissaoAlerta,
        SimNaoEnum governamental,
        String email
) {
}

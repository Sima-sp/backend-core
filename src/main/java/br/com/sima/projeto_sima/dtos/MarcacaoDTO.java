package br.com.sima.projeto_sima.dtos;

import br.com.sima.projeto_sima.entitys.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public record MarcacaoDTO(
        Integer id,
        Double latitude,
        Double longitude,
        LocalDateTime dataMarcacao,
        List<Usuario> usuarios
) {
}

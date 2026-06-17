package br.com.sima.projeto_sima.mappers;

import br.com.sima.projeto_sima.dtos.UsuarioDTO;
import br.com.sima.projeto_sima.entitys.Usuario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    List<UsuarioDTO> retornaTodosUsuariosDTO(List<Usuario> entities);

}

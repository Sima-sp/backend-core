package br.com.sima.projeto_sima.mappers;

import br.com.sima.projeto_sima.dtos.MarcacaoDTO;
import br.com.sima.projeto_sima.entitys.Marcacao;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarcacaoMapper {

    List<MarcacaoDTO> retornaMarcacoesDTOS(List<Marcacao> entities);
}

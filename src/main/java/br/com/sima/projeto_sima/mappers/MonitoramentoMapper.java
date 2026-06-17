package br.com.sima.projeto_sima.mappers;

import br.com.sima.projeto_sima.dtos.MonitoramentoDTO;
import br.com.sima.projeto_sima.dtos.MonitoramentoResiduoDTO;
import br.com.sima.projeto_sima.entitys.Monitoramento;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MonitoramentoMapper {

    List<MonitoramentoDTO> retornarTodosMonitoramentoDTO(List<Monitoramento> entities);

    List<MonitoramentoResiduoDTO> retornaMonitoramentoResiduoDTO(List<Monitoramento> entities);

}

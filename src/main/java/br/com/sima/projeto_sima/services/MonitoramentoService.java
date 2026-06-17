package br.com.sima.projeto_sima.services;

import br.com.sima.projeto_sima.dtos.MonitoramentoDTO;
import br.com.sima.projeto_sima.dtos.MonitoramentoResiduoDTO;
import br.com.sima.projeto_sima.mappers.MonitoramentoMapper;
import br.com.sima.projeto_sima.repositorys.MonitoramentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class MonitoramentoService {

    @Autowired
    private MonitoramentoRepository monitoramentoRepository;

    @Autowired
    private MonitoramentoMapper monitoramentoMapper;

    public List<MonitoramentoDTO> findAll()
    {
        var monitoramentos = monitoramentoMapper.retornarTodosMonitoramentoDTO(monitoramentoRepository.findAll());
        return monitoramentos;
    }

    public List<MonitoramentoResiduoDTO> buscarMonitoramentoPorEstadoBueiro(String estadoBueiro)
    {
        var monitoramentos = monitoramentoMapper.retornaMonitoramentoResiduoDTO(monitoramentoRepository.buscarMonitoramentoPorEstadoBueiro(estadoBueiro));
        return monitoramentos;
    }

    public List<MonitoramentoDTO> buscarMonitoramentoProximo(Double latitude, Double longitude, Integer raio)
    {
        var monitoramentos = monitoramentoMapper.retornarTodosMonitoramentoDTO(monitoramentoRepository.buscarMonitoramentoProximo(latitude, longitude, raio));
        return monitoramentos;
    }


}

package br.com.sima.projeto_sima.controllers;


import br.com.sima.projeto_sima.dtos.MonitoramentoDTO;
import br.com.sima.projeto_sima.dtos.MonitoramentoResiduoDTO;
import br.com.sima.projeto_sima.services.MonitoramentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/monitoramentos")

public class MonitoramentoController {

    @Autowired
    private MonitoramentoService monitoramentoService;

    @GetMapping
    public List<MonitoramentoDTO> listarMonitoramento()
    {
        return monitoramentoService.findAll();
    }

    @GetMapping("/{estadoBueiro}")
    public List<MonitoramentoResiduoDTO> listarMonitoramentoPorEstadoBueiro(@PathVariable String estadoBueiro)
    {
        return monitoramentoService.buscarMonitoramentoPorEstadoBueiro(estadoBueiro);
    }

    @GetMapping("proximos/{latitude}/{longitude}/{raio}")
    public List<MonitoramentoDTO> listarMonitoramentoProximo(@PathVariable Double latitude, @PathVariable Double longitude, @PathVariable Integer raio)
    {
        return monitoramentoService.buscarMonitoramentoProximo(latitude, longitude, raio);
    }
}

package br.com.back_end.simasp.previsao.controller;

import br.com.back_end.simasp.previsao.dto.PrevisaoResponse;
import br.com.back_end.simasp.previsao.service.PrevisaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Previsões para o mapa do app e do site. */
@RestController
@RequestMapping("/previsoes")
public class PrevisaoController {

    private final PrevisaoService service;

    public PrevisaoController(PrevisaoService service) {
        this.service = service;
    }

    /** Um item por sensor, com status VALIDA, DESATUALIZADA ou SEM_PREVISAO. */
    @GetMapping
    public ResponseEntity<List<PrevisaoResponse>> mapa() {
        return ResponseEntity.ok(service.mapa());
    }

    /** Última previsão de um sensor. */
    @GetMapping("/sensores/{sensorId}")
    public ResponseEntity<PrevisaoResponse> doSensor(@PathVariable Long sensorId) {
        return ResponseEntity.ok(service.ultimaDoSensor(sensorId));
    }

    /** Recalcula um sensor na hora (usado na demo e ao chegar leitura nova). */
    @PostMapping("/sensores/{sensorId}/atualizar")
    public ResponseEntity<PrevisaoResponse> atualizar(@PathVariable Long sensorId) {
        return service.atualizarSensor(sensorId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(503).build());
    }
}

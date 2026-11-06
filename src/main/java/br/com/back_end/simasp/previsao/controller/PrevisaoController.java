package br.com.back_end.simasp.previsao.controller;

import br.com.back_end.simasp.previsao.client.dto.RegioesIaResponse;
import br.com.back_end.simasp.previsao.dto.PrevisaoResponse;
import br.com.back_end.simasp.previsao.service.PrevisaoService;
import br.com.back_end.simasp.previsao.service.RegiaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Previsões para o mapa do app e do site. */
@RestController
@RequestMapping("/previsoes")
public class PrevisaoController {

    private final PrevisaoService service;
    private final RegiaoService regiaoService;

    public PrevisaoController(PrevisaoService service, RegiaoService regiaoService) {
        this.service = service;
        this.regiaoService = regiaoService;
    }

    /** Um item por sensor, com status VALIDA, DESATUALIZADA ou SEM_PREVISAO. */
    @GetMapping
    public ResponseEntity<List<PrevisaoResponse>> mapa() {
        return ResponseEntity.ok(service.mapa());
    }

    /**
     * Risco por subprefeitura, para o aviso de região ("risco elevado na Vila Maria").
     *
     * <p>Regiões com poucos pontos monitorados vêm com {@code cobertura = "SEM_COBERTURA"} e
     * sem nível — a tela deve dizer "sem cobertura", nunca "sem risco". Responde 503 quando o
     * serviço de IA não está disponível; nesse caso o app só não mostra a faixa.</p>
     */
    @GetMapping("/regioes")
    public ResponseEntity<RegioesIaResponse> regioes() {
        return regiaoService.regioes()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(503).build());
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

package br.com.back_end.simasp.previsao.controller;

import br.com.back_end.simasp.previsao.dto.DemonstracaoRequest;
import br.com.back_end.simasp.previsao.dto.DemonstracaoResponse;
import br.com.back_end.simasp.previsao.service.DemonstracaoService;
import br.com.back_end.simasp.previsao.service.PrevisaoService;
import br.com.back_end.simasp.previsao.service.RegiaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

/**
 * Liga e desliga o modo de demonstração (chuva simulada).
 *
 * <p>Ligar ou desligar já recalcula as previsões na hora, para o mapa mudar sem esperar o
 * agendador. Só funciona com {@code ia.demonstracao-habilitada=true}; senão responde 403.</p>
 */
@RestController
@RequestMapping("/admin/demonstracao")
public class DemonstracaoController {

    private static final ZoneId FUSO_LOCAL = ZoneId.of("America/Sao_Paulo");

    private final DemonstracaoService demonstracao;
    private final PrevisaoService previsaoService;
    private final RegiaoService regiaoService;

    public DemonstracaoController(DemonstracaoService demonstracao, PrevisaoService previsaoService,
                                  RegiaoService regiaoService) {
        this.demonstracao = demonstracao;
        this.previsaoService = previsaoService;
        this.regiaoService = regiaoService;
    }

    /** Diz se há um cenário valendo e até quando. */
    @GetMapping
    public ResponseEntity<DemonstracaoResponse> situacao() {
        return ResponseEntity.ok(montar(null));
    }

    /** Liga um cenário e recalcula as previsões dos sensores ativos. */
    @PostMapping("/iniciar")
    public ResponseEntity<DemonstracaoResponse> iniciar(@Valid @RequestBody DemonstracaoRequest pedido) {
        if (!demonstracao.habilitada()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Modo de demonstração desabilitado neste ambiente (ia.demonstracao-habilitada=false).");
        }
        Duration duracao = pedido.minutos() == null ? null : Duration.ofMinutes(pedido.minutos());
        demonstracao.iniciar(pedido.cenario(), pedido.sensorIds(), pedido.nivelAgua(),
                pedido.porcentagemLixo(), duracao);

        return ResponseEntity.ok(montar(recalcular()));
    }

    /** Desliga a demonstração e recalcula com a chuva real. */
    @PostMapping("/encerrar")
    public ResponseEntity<DemonstracaoResponse> encerrar() {
        demonstracao.encerrar();
        return ResponseEntity.ok(montar(recalcular()));
    }

    private int recalcular() {
        regiaoService.limparCache();
        return previsaoService.atualizarTodos();
    }

    private DemonstracaoResponse montar(Integer previsoesGravadas) {
        Optional<DemonstracaoService.Estado> ativa = demonstracao.ativa();
        if (ativa.isEmpty()) {
            return new DemonstracaoResponse(demonstracao.habilitada(), false, null, null, Set.of(),
                    null, null, null, previsoesGravadas);
        }
        DemonstracaoService.Estado estado = ativa.get();
        return new DemonstracaoResponse(
                demonstracao.habilitada(),
                true,
                estado.cenario(),
                estado.cenario().descricao(),
                estado.sensorIds(),
                estado.nivelAgua(),
                estado.porcentagemLixo(),
                LocalDateTime.ofInstant(estado.expiraEm(), FUSO_LOCAL),
                previsoesGravadas);
    }
}

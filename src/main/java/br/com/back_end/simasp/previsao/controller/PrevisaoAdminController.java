package br.com.back_end.simasp.previsao.controller;

import br.com.back_end.simasp.previsao.client.PrevisaoIaClient;
import br.com.back_end.simasp.previsao.service.PrevisaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Operação da integração com a IA: forçar atualização e conferir o serviço. */
@RestController
@RequestMapping("/admin/previsoes")
public class PrevisaoAdminController {

    private final PrevisaoService service;
    private final PrevisaoIaClient cliente;

    public PrevisaoAdminController(PrevisaoService service, PrevisaoIaClient cliente) {
        this.service = service;
        this.cliente = cliente;
    }

    /** Recalcula todos os sensores ativos agora, sem esperar o agendador. */
    @PostMapping("/atualizar")
    public ResponseEntity<Map<String, Object>> atualizarTodos() {
        return ResponseEntity.ok(Map.of("previsoesGravadas", service.atualizarTodos()));
    }

    /** Estado do serviço de IA: se está respondendo e qual versão do modelo está carregada. */
    @PostMapping("/checar")
    public ResponseEntity<Map<String, Object>> checar() {
        String versao = cliente.versaoModelo().orElse(null);
        return ResponseEntity.ok(Map.of(
                "disponivel", cliente.disponivel(),
                "respondeu", versao != null,
                "modeloVersao", versao == null ? "" : versao));
    }
}

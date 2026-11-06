package br.com.back_end.simasp.previsao.controller;

import br.com.back_end.simasp.previsao.client.PrevisaoIaClient;
import br.com.back_end.simasp.previsao.service.PrevisaoService;
import br.com.back_end.simasp.previsao.service.RegiaoService;
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
    private final RegiaoService regiaoService;

    public PrevisaoAdminController(PrevisaoService service, PrevisaoIaClient cliente,
                                   RegiaoService regiaoService) {
        this.service = service;
        this.cliente = cliente;
        this.regiaoService = regiaoService;
    }

    /** Recalcula todos os sensores ativos agora, sem esperar o agendador. */
    @PostMapping("/atualizar")
    public ResponseEntity<Map<String, Object>> atualizarTodos() {
        regiaoService.limparCache();
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

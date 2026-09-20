package br.com.back_end.simasp.previsao.client;

import br.com.back_end.simasp.previsao.client.dto.ItemLoteIa;
import br.com.back_end.simasp.previsao.client.dto.PrevisaoIaRequest;
import br.com.back_end.simasp.previsao.client.dto.PrevisaoIaResponse;
import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Cliente HTTP do serviço de IA, com timeout curto e desligamento temporário.
 *
 * <p>Regras de convivência com o app:</p>
 * <ul>
 *   <li>a previsão <b>nunca</b> pode travar uma requisição do usuário: o timeout é curto e
 *       qualquer erro vira {@link Optional#empty()}, não exceção;</li>
 *   <li>depois de {@code ia.falhas-para-abrir} falhas seguidas, o cliente para de tentar por
 *       {@code ia.pausa-apos-falhas} (disjuntor simples). Assim, com o serviço fora do ar, o
 *       backend não paga o timeout a cada chamada;</li>
 *   <li>quem decide o que fazer sem previsão é o serviço, não o cliente.</li>
 * </ul>
 */
@Component
public class PrevisaoIaClient {

    private static final Logger log = LoggerFactory.getLogger(PrevisaoIaClient.class);

    private final PrevisaoProperties propriedades;
    private final RestClient rest;
    private final AtomicInteger falhasSeguidas = new AtomicInteger();
    private final AtomicReference<Instant> desligadoAte = new AtomicReference<>(Instant.EPOCH);

    public PrevisaoIaClient(PrevisaoProperties propriedades,
                            @Qualifier("restClientIa") RestClient restClientIa) {
        this.propriedades = propriedades;
        this.rest = restClientIa;
    }

    /** True quando vale a pena tentar: integração ligada e disjuntor fechado. */
    public boolean disponivel() {
        return propriedades.habilitada() && Instant.now().isAfter(desligadoAte.get());
    }

    /** Uma previsão. Devolve vazio em qualquer erro (timeout, 5xx, serviço desligado). */
    public Optional<PrevisaoIaResponse> prever(PrevisaoIaRequest requisicao) {
        if (!disponivel()) {
            return Optional.empty();
        }
        try {
            PrevisaoIaResponse resposta = rest.post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requisicao)
                    .retrieve()
                    .body(PrevisaoIaResponse.class);
            registrarSucesso();
            return Optional.ofNullable(resposta);
        } catch (Exception erro) {
            registrarFalha("/predict", erro);
            return Optional.empty();
        }
    }

    /**
     * Previsão em lote, usada pelo agendador. A lista devolvida mantém a ordem da entrada;
     * em caso de erro devolve lista vazia.
     */
    public List<ItemLoteIa> preverLote(List<PrevisaoIaRequest> requisicoes) {
        if (!disponivel() || requisicoes.isEmpty()) {
            return List.of();
        }
        try {
            List<ItemLoteIa> resposta = rest.post()
                    .uri("/predict/batch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requisicoes)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ItemLoteIa>>() {
                    });
            registrarSucesso();
            return resposta == null ? List.of() : resposta;
        } catch (Exception erro) {
            registrarFalha("/predict/batch", erro);
            return List.of();
        }
    }

    /** Estado do serviço de IA, para o Actuator e para a tela de administração. */
    public Optional<String> versaoModelo() {
        if (!propriedades.habilitada()) {
            return Optional.empty();
        }
        try {
            SaudeIa saude = rest.get().uri("/health").retrieve().body(SaudeIa.class);
            return saude == null ? Optional.empty() : Optional.ofNullable(saude.modeloVersao());
        } catch (Exception erro) {
            log.debug("Serviço de IA não respondeu ao /health: {}", erro.getMessage());
            return Optional.empty();
        }
    }

    private void registrarSucesso() {
        falhasSeguidas.set(0);
    }

    private void registrarFalha(String rota, Exception erro) {
        int falhas = falhasSeguidas.incrementAndGet();
        log.warn("Serviço de IA falhou em {} ({} falha(s) seguida(s)): {}", rota, falhas, erro.toString());
        if (falhas >= propriedades.falhasParaAbrir()) {
            desligadoAte.set(Instant.now().plus(propriedades.pausaAposFalhas()));
            falhasSeguidas.set(0);
            log.warn("Serviço de IA desligado por {} — o backend segue sem previsão.",
                    propriedades.pausaAposFalhas());
        }
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private record SaudeIa(String status, String modeloVersao, String origem) {
    }
}

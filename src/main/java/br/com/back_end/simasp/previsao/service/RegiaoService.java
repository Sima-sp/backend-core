package br.com.back_end.simasp.previsao.service;

import br.com.back_end.simasp.previsao.client.PrevisaoIaClient;
import br.com.back_end.simasp.previsao.client.dto.RegioesIaResponse;
import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Aviso de risco por subprefeitura, repassado do serviço de IA.
 *
 * <p>Não vai para o banco: a mesma informação pode ser reconstruída do histórico por ponto na
 * {@code TBL_PREVISAO}, e uma tabela a mais só criaria dado duplicado. O que existe aqui é um
 * cache curto em memória, porque a tela inicial do app pede isso a cada abertura e a chuva do
 * Open-Meteo só muda de hora em hora.</p>
 */
@Service
public class RegiaoService {

    private final PrevisaoIaClient cliente;
    private final PrevisaoProperties propriedades;
    private final AtomicReference<Cache> cache = new AtomicReference<>();

    public RegiaoService(PrevisaoIaClient cliente, PrevisaoProperties propriedades) {
        this.cliente = cliente;
        this.propriedades = propriedades;
    }

    /** Regiões em risco, do cache quando ainda válido. Vazio quando a IA não responde. */
    public Optional<RegioesIaResponse> regioes() {
        Cache atual = cache.get();
        if (atual != null && atual.validoAte().isAfter(Instant.now())) {
            return Optional.of(atual.resposta());
        }
        Optional<RegioesIaResponse> resposta = cliente.regioes();
        resposta.ifPresent(r -> cache.set(new Cache(r, Instant.now().plus(propriedades.cacheRegioes()))));
        return resposta;
    }

    /** Descarta o cache (usado ao forçar atualização pela administração). */
    public void limparCache() {
        cache.set(null);
    }

    private record Cache(RegioesIaResponse resposta, Instant validoAte) {
    }
}

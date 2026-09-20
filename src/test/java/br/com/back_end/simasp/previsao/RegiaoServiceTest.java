package br.com.back_end.simasp.previsao;

import br.com.back_end.simasp.previsao.client.PrevisaoIaClient;
import br.com.back_end.simasp.previsao.client.dto.RegiaoIaResponse;
import br.com.back_end.simasp.previsao.client.dto.RegioesIaResponse;
import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import br.com.back_end.simasp.previsao.service.RegiaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class RegiaoServiceTest {

    private static PrevisaoProperties propriedades(Duration cache) {
        return new PrevisaoProperties(true, "http://ia:5000", Duration.ofSeconds(2),
                Duration.ofMinutes(30), Duration.ofMinutes(30), cache, 100, 3, Duration.ofMinutes(1));
    }

    /** Cliente falso que conta quantas vezes o serviço de IA foi realmente chamado. */
    private static class ClienteFalso extends PrevisaoIaClient {
        final AtomicInteger chamadas = new AtomicInteger();

        ClienteFalso() {
            super(propriedades(Duration.ofMinutes(10)), org.springframework.web.client.RestClient.create());
        }

        @Override
        public Optional<RegioesIaResponse> regioes() {
            chamadas.incrementAndGet();
            return Optional.of(new RegioesIaResponse(
                    OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(30), OffsetDateTime.now(),
                    3, "MODELO", "v1", 3,
                    List.of(new RegiaoIaResponse("MOOCA", "Mooca", "LESTE", "MEDIO", 0.01,
                            "P0007", "R DO ORATORIO", 12, "COBERTA")),
                    List.of()));
        }
    }

    @Test
    @DisplayName("a segunda chamada vem do cache, sem bater no serviço de IA")
    void usaCache() {
        ClienteFalso cliente = new ClienteFalso();
        RegiaoService servico = new RegiaoService(cliente, propriedades(Duration.ofMinutes(10)));

        assertThat(servico.regioes()).isPresent();
        assertThat(servico.regioes()).isPresent();

        assertThat(cliente.chamadas.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("limpar o cache faz a próxima chamada ir ao serviço de novo")
    void limpaCache() {
        ClienteFalso cliente = new ClienteFalso();
        RegiaoService servico = new RegiaoService(cliente, propriedades(Duration.ofMinutes(10)));

        servico.regioes();
        servico.limparCache();
        servico.regioes();

        assertThat(cliente.chamadas.get()).isEqualTo(2);
    }
}

package br.com.back_end.simasp.previsao;

import br.com.back_end.simasp.previsao.client.PrevisaoIaClient;
import br.com.back_end.simasp.previsao.client.dto.RegiaoIaResponse;
import br.com.back_end.simasp.previsao.client.dto.RegioesIaResponse;
import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import br.com.back_end.simasp.previsao.enums.CenarioChuvaEnum;
import br.com.back_end.simasp.previsao.service.DemonstracaoService;
import br.com.back_end.simasp.previsao.service.RegiaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RegiaoServiceTest {

    private static PrevisaoProperties propriedades(boolean demonstracaoHabilitada) {
        return new PrevisaoProperties(true, "http://ia:5000", Duration.ofSeconds(2),
                Duration.ofMinutes(30), Duration.ofMinutes(30), Duration.ofMinutes(10), 100, 3,
                Duration.ofMinutes(1), demonstracaoHabilitada, Duration.ofMinutes(30));
    }

    /** Cliente falso que guarda com qual chuva o serviço de IA foi chamado em cada vez. */
    private static class ClienteFalso extends PrevisaoIaClient {
        final List<List<Double>> chamadas = new ArrayList<>();

        ClienteFalso() {
            super(propriedades(false), RestClient.create());
        }

        @Override
        public Optional<RegioesIaResponse> regioes(List<Double> chuvaHoraria) {
            chamadas.add(chuvaHoraria);
            return Optional.of(new RegioesIaResponse(
                    OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(30), OffsetDateTime.now(),
                    3, "MODELO", "v1", 3, chuvaHoraria == null ? "OPEN_METEO" : "INFORMADA",
                    List.of(new RegiaoIaResponse("MOOCA", "Mooca", "LESTE", "MEDIO", 0.01,
                            "P0007", "R DO ORATORIO", 12, "COBERTA")),
                    List.of()));
        }
    }

    @Test
    @DisplayName("a segunda chamada vem do cache, sem bater no serviço de IA")
    void usaCache() {
        ClienteFalso cliente = new ClienteFalso();
        PrevisaoProperties props = propriedades(false);
        RegiaoService servico = new RegiaoService(cliente, props, new DemonstracaoService(props));

        assertThat(servico.regioes()).isPresent();
        assertThat(servico.regioes()).isPresent();

        assertThat(cliente.chamadas).hasSize(1);
        assertThat(cliente.chamadas.get(0)).isNull();  // sem demonstração: chuva real
    }

    @Test
    @DisplayName("limpar o cache faz a próxima chamada ir ao serviço de novo")
    void limpaCache() {
        ClienteFalso cliente = new ClienteFalso();
        PrevisaoProperties props = propriedades(false);
        RegiaoService servico = new RegiaoService(cliente, props, new DemonstracaoService(props));

        servico.regioes();
        servico.limparCache();
        servico.regioes();

        assertThat(cliente.chamadas).hasSize(2);
    }

    @Test
    @DisplayName("demonstração: manda a série do cenário e não reaproveita o cache da chuva real")
    void demonstracaoNaoUsaCacheDaChuvaReal() {
        ClienteFalso cliente = new ClienteFalso();
        PrevisaoProperties props = propriedades(true);
        DemonstracaoService demonstracao = new DemonstracaoService(props);
        RegiaoService servico = new RegiaoService(cliente, props, demonstracao);

        servico.regioes();                                              // chuva real, entra no cache
        demonstracao.iniciar(CenarioChuvaEnum.CHUVA_FORTE, null, null, null, null);
        Optional<RegioesIaResponse> simulada = servico.regioes();       // cenário: não pode vir do cache
        demonstracao.encerrar();
        Optional<RegioesIaResponse> real = servico.regioes();           // voltou: também não pode

        assertThat(cliente.chamadas).hasSize(3);
        assertThat(cliente.chamadas.get(1)).isEqualTo(CenarioChuvaEnum.CHUVA_FORTE.serie());
        assertThat(simulada.orElseThrow().fonteChuva()).isEqualTo("INFORMADA");
        assertThat(real.orElseThrow().fonteChuva()).isEqualTo("OPEN_METEO");
    }
}

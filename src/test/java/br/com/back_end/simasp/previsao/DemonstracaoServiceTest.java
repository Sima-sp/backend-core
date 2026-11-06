package br.com.back_end.simasp.previsao;

import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import br.com.back_end.simasp.previsao.enums.CenarioChuvaEnum;
import br.com.back_end.simasp.previsao.service.DemonstracaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemonstracaoServiceTest {

    private static DemonstracaoService servico(boolean habilitada) {
        return new DemonstracaoService(new PrevisaoProperties(true, "http://ia:5000",
                Duration.ofSeconds(2), Duration.ofMinutes(30), Duration.ofMinutes(30),
                Duration.ofMinutes(10), 100, 3, Duration.ofMinutes(1), habilitada, Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("desabilitada por padrão: não deixa simular chuva num ambiente de verdade")
    void desabilitadaNaoLiga() {
        DemonstracaoService servico = servico(false);

        assertThatThrownBy(() -> servico.iniciar(CenarioChuvaEnum.CHUVA_FORTE, null, null, null, null))
                .isInstanceOf(IllegalStateException.class);
        assertThat(servico.ativa()).isEmpty();
    }

    @Test
    @DisplayName("sem lista de sensores, o cenário vale para todos")
    void valeParaTodos() {
        DemonstracaoService servico = servico(true);
        servico.iniciar(CenarioChuvaEnum.CHUVA_FORTE, null, null, null, null);

        assertThat(servico.paraSensor(1L)).isPresent();
        assertThat(servico.paraSensor(99L)).isPresent();
    }

    @Test
    @DisplayName("com lista, só os sensores escolhidos recebem a chuva simulada")
    void valeSoParaOsEscolhidos() {
        DemonstracaoService servico = servico(true);
        servico.iniciar(CenarioChuvaEnum.CHUVA_MODERADA, Set.of(1L, 2L), 105.0, null, null);

        assertThat(servico.paraSensor(1L)).isPresent();
        assertThat(servico.paraSensor(1L).orElseThrow().nivelAgua()).isEqualTo(105.0);
        assertThat(servico.paraSensor(3L)).isEmpty();
    }

    @Test
    @DisplayName("desliga sozinha quando o tempo acaba")
    void expira() {
        DemonstracaoService servico = servico(true);
        servico.iniciar(CenarioChuvaEnum.CHUVA_FORTE, null, null, null, Duration.ofMillis(-1));

        assertThat(servico.ativa()).isEmpty();
        assertThat(servico.paraSensor(1L)).isEmpty();
    }

    @Test
    @DisplayName("encerrar volta para a chuva real")
    void encerra() {
        DemonstracaoService servico = servico(true);
        servico.iniciar(CenarioChuvaEnum.CHUVA_FORTE, null, null, null, null);
        servico.encerrar();

        assertThat(servico.ativa()).isEmpty();
    }

    @Test
    @DisplayName("cada cenário tem 72 horas, e a chuva forte termina na hora atual")
    void seriesDosCenarios() {
        for (CenarioChuvaEnum cenario : CenarioChuvaEnum.values()) {
            assertThat(cenario.serie()).hasSize(CenarioChuvaEnum.horas());
        }
        assertThat(CenarioChuvaEnum.SECO.serie()).containsOnly(0.0);
        assertThat(CenarioChuvaEnum.CHUVA_FORTE.serie().get(71)).isEqualTo(20.0);
        assertThat(CenarioChuvaEnum.CHUVA_FORTE.serie().stream().mapToDouble(Double::doubleValue).sum())
                .isEqualTo(68.5);
    }
}

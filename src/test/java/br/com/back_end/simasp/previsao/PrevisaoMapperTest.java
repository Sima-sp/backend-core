package br.com.back_end.simasp.previsao;

import br.com.back_end.simasp.leitura.enums.NivelRiscoEnum;
import br.com.back_end.simasp.previsao.client.dto.PrevisaoIaResponse;
import br.com.back_end.simasp.previsao.entity.Previsao;
import br.com.back_end.simasp.previsao.enums.OrigemPrevisaoEnum;
import br.com.back_end.simasp.previsao.enums.StatusPrevisaoEnum;
import br.com.back_end.simasp.previsao.mapper.PrevisaoMapper;
import br.com.back_end.simasp.sensor.entity.Sensor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PrevisaoMapperTest {

    private final PrevisaoMapper mapper = new PrevisaoMapper();

    @Test
    @DisplayName("converte a resposta do serviço em entidade, com data no fuso de São Paulo")
    void converteResposta() {
        PrevisaoIaResponse resposta = new PrevisaoIaResponse(
                7L, 0.031, "ALTO", "MEDIO", 3,
                OffsetDateTime.of(2026, 9, 19, 13, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 19, 13, 30, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 9, 19, 12, 0, 0, 0, ZoneOffset.UTC),
                false, true, List.of("nivelAgua 85%", "lixo 70% com chuva"), false,
                "OPEN_METEO", 12.4, 3.1, "MODELO", "v1", "P0042", 87.5, List.of());

        Previsao previsao = mapper.paraEntidade(resposta, sensor(), Duration.ofMinutes(30));

        assertThat(previsao.getNivelRisco()).isEqualTo(NivelRiscoEnum.ALTO);
        assertThat(previsao.getNivelRiscoModelo()).isEqualTo(NivelRiscoEnum.MEDIO);
        assertThat(previsao.getOrigem()).isEqualTo(OrigemPrevisaoEnum.MODELO);
        assertThat(previsao.getGeradaEm()).isEqualTo(LocalDateTime.of(2026, 9, 19, 10, 0));
        assertThat(previsao.getValidaAte()).isEqualTo(LocalDateTime.of(2026, 9, 19, 10, 30));
        assertThat(previsao.getMotivosAjuste()).isEqualTo("nivelAgua 85%;lixo 70% com chuva");
        assertThat(mapper.paraResposta(previsao).motivosAjuste()).hasSize(2);
        assertThat(mapper.paraResposta(previsao).simulada()).isFalse();  // chuva real do Open-Meteo
    }

    @Test
    @DisplayName("previsão com chuva de cenário sai marcada como simulada")
    void marcaSimulada() {
        Previsao previsao = new Previsao();
        previsao.setSensor(sensor());
        previsao.setValidaAte(LocalDateTime.now().plusMinutes(30));
        previsao.setFonteChuva("INFORMADA");

        assertThat(mapper.paraResposta(previsao).simulada()).isTrue();
        // chuva real da leitura, usada quando o Open-Meteo cai: não é simulação
        assertThat(mapper.simulada("INFORMADA_LEGADO")).isFalse();
        assertThat(mapper.simulada(null)).isFalse();
    }

    @Test
    @DisplayName("sem validaAte, usa a validade configurada")
    void validadePadrao() {
        PrevisaoIaResponse resposta = new PrevisaoIaResponse(
                7L, null, "BAIXO", "BAIXO", null, null, null, null,
                null, null, null, null, "CACHE", null, null, "REGRAS", "regras-v0", null, null, List.of());

        Previsao previsao = mapper.paraEntidade(resposta, sensor(), Duration.ofMinutes(30));

        assertThat(previsao.getProbabilidadeAlagamento()).isNull();
        assertThat(previsao.getJanelaHoras()).isEqualTo(3);
        assertThat(previsao.getValidaAte()).isEqualTo(previsao.getGeradaEm().plusMinutes(30));
        assertThat(mapper.statusDe(previsao)).isEqualTo(StatusPrevisaoEnum.VALIDA);
    }

    @Test
    @DisplayName("previsão vencida aparece como DESATUALIZADA, e o sensor não some do mapa")
    void vencida() {
        Previsao previsao = new Previsao();
        previsao.setSensor(sensor());
        previsao.setValidaAte(LocalDateTime.now().minusHours(2));

        assertThat(mapper.statusDe(previsao)).isEqualTo(StatusPrevisaoEnum.DESATUALIZADA);
        assertThat(mapper.semPrevisao(sensor()).status()).isEqualTo(StatusPrevisaoEnum.SEM_PREVISAO);
    }

    @Test
    @DisplayName("nível desconhecido não quebra: vira nulo e fica registrado no log")
    void nivelDesconhecido() {
        assertThat(mapper.paraNivel("MÉDIO?")).isNull();
        assertThat(mapper.paraNivel("critico")).isEqualTo(NivelRiscoEnum.CRITICO);
    }

    private Sensor sensor() {
        Sensor sensor = new Sensor();
        sensor.setId(7L);
        sensor.setLatitude(new BigDecimal("-23.515200"));
        sensor.setLongitude(new BigDecimal("-46.584100"));
        sensor.setVizinhanca("Santana");
        return sensor;
    }
}

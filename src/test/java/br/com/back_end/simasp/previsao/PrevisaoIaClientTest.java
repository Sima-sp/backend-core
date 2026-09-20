package br.com.back_end.simasp.previsao;

import br.com.back_end.simasp.previsao.client.PrevisaoIaClient;
import br.com.back_end.simasp.previsao.client.dto.PrevisaoIaRequest;
import br.com.back_end.simasp.previsao.client.dto.PrevisaoIaResponse;
import br.com.back_end.simasp.previsao.config.PrevisaoProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PrevisaoIaClientTest {

    private static final String BASE = "http://ia:5000";

    private MockRestServiceServer servidor;
    private PrevisaoIaClient cliente;

    @BeforeEach
    void preparar() {
        PrevisaoProperties propriedades = new PrevisaoProperties(
                true, BASE, Duration.ofSeconds(2), Duration.ofMinutes(30), Duration.ofMinutes(30),
                100, 2, Duration.ofMinutes(1));
        RestClient.Builder construtor = RestClient.builder().baseUrl(BASE);
        servidor = MockRestServiceServer.bindTo(construtor).build();
        cliente = new PrevisaoIaClient(propriedades, construtor.build());
    }

    @Test
    @DisplayName("lê a resposta do serviço e aceita probabilidade nula (modo regras)")
    void leResposta() {
        servidor.expect(requestTo(BASE + "/predict"))
                .andExpect(jsonPath("$.sensorId").value(7))
                .andRespond(withSuccess("""
                        {"sensorId":7,"probabilidadeAlagamento":null,"nivelRisco":"ALTO",
                         "nivelRiscoModelo":"MEDIO","janelaHoras":3,
                         "geradaEm":"2026-09-19T10:00:00-03:00","validaAte":"2026-09-19T10:30:00-03:00",
                         "horaReferencia":"2026-09-19T09:00:00-03:00","medicaoTransbordando":false,
                         "ajusteSensorAplicado":true,"motivosAjuste":["nivelAgua 85%"],
                         "semLeituraSensor":false,"fonteChuva":"OPEN_METEO","origem":"REGRAS",
                         "modeloVersao":"regras-v0","campoQueAindaNaoExiste":123}
                        """, MediaType.APPLICATION_JSON));

        Optional<PrevisaoIaResponse> resposta = cliente.prever(requisicao());

        assertThat(resposta).isPresent();
        assertThat(resposta.get().probabilidadeAlagamento()).isNull();
        assertThat(resposta.get().nivelRisco()).isEqualTo("ALTO");
        assertThat(resposta.get().motivosAjuste()).containsExactly("nivelAgua 85%");
        servidor.verify();
    }

    @Test
    @DisplayName("erro do serviço não vira exceção: devolve vazio")
    void erroNaoPropaga() {
        servidor.expect(requestTo(BASE + "/predict")).andRespond(withServerError());

        assertThat(cliente.prever(requisicao())).isEmpty();
        servidor.verify();
    }

    @Test
    @DisplayName("depois das falhas seguidas o cliente para de tentar")
    void disjuntorAbre() {
        servidor.expect(requestTo(BASE + "/predict")).andRespond(withServerError());
        servidor.expect(requestTo(BASE + "/predict")).andRespond(withServerError());

        cliente.prever(requisicao());
        cliente.prever(requisicao());

        assertThat(cliente.disponivel()).isFalse();
        // A terceira chamada nem sai: o servidor não espera mais nenhuma requisição.
        assertThat(cliente.prever(requisicao())).isEmpty();
        servidor.verify();
    }

    @Test
    @DisplayName("lote devolve um item por sensor, na ordem da entrada")
    void lote() {
        servidor.expect(requestTo(BASE + "/predict/batch"))
                .andRespond(withSuccess("""
                        [{"sensorId":1,"sucesso":true,"previsao":{"sensorId":1,
                          "probabilidadeAlagamento":0.02,"nivelRisco":"MEDIO","origem":"MODELO",
                          "modeloVersao":"v1"}},
                         {"sensorId":2,"sucesso":false,"erro":"fora de São Paulo","codigoErro":"FORA_DA_AREA"}]
                        """, MediaType.APPLICATION_JSON));

        var itens = cliente.preverLote(List.of(requisicao(), requisicao()));

        assertThat(itens).hasSize(2);
        assertThat(itens.get(0).previsao().probabilidadeAlagamento()).isEqualTo(0.02);
        assertThat(itens.get(1).sucesso()).isFalse();
        assertThat(itens.get(1).codigoErro()).isEqualTo("FORA_DA_AREA");
        servidor.verify();
    }

    @Test
    @DisplayName("integração desligada não chama o serviço")
    void desligada() {
        PrevisaoProperties desligada = new PrevisaoProperties(
                false, BASE, Duration.ofSeconds(2), Duration.ofMinutes(30), Duration.ofMinutes(30),
                100, 3, Duration.ofMinutes(1));
        RestClient.Builder construtor = RestClient.builder().baseUrl(BASE);
        MockRestServiceServer vazio = MockRestServiceServer.bindTo(construtor).build();
        PrevisaoIaClient semIa = new PrevisaoIaClient(desligada, construtor.build());

        assertThat(semIa.disponivel()).isFalse();
        assertThat(semIa.prever(requisicao())).isEmpty();
        vazio.verify();
    }

    private PrevisaoIaRequest requisicao() {
        return new PrevisaoIaRequest(7L, -23.5152, -46.5841, OffsetDateTime.now(), 85.0, 20.0, null);
    }
}

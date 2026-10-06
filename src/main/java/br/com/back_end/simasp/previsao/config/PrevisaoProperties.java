package br.com.back_end.simasp.previsao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuração da integração com o serviço de IA (prefixo {@code ia} no .env / properties).
 *
 * @param habilitada      liga ou desliga a chamada ao serviço; desligada, o backend usa só o
 *                        ajuste local pela leitura do sensor
 * @param url             base do serviço, ex.: {@code http://ml-service:5000}
 * @param timeout         tempo máximo de espera por resposta (o app não pode travar por causa da IA)
 * @param validade        por quanto tempo a previsão conta como VALIDA no mapa (usada só quando
 *                        o serviço não informa {@code validaAte})
 * @param idadeMaximaLeitura leitura mais velha que isso não é enviada: o sensor conta como sem leitura
 * @param cacheRegioes    por quanto tempo o aviso por região fica em cache no backend
 * @param tamanhoLote     itens por chamada de {@code /predict/batch} (o serviço aceita até 500)
 * @param falhasParaAbrir falhas seguidas que desligam o serviço temporariamente
 * @param pausaAposFalhas quanto tempo ficar sem tentar depois de abrir
 * @param demonstracaoHabilitada permite ligar o modo de demonstração (chuva simulada); o padrão
 *                        é desligado, para não simular chuva num ambiente de verdade por engano
 * @param demonstracaoDuracao depois desse tempo a demonstração desliga sozinha
 */
@ConfigurationProperties(prefix = "ia")
public record PrevisaoProperties(
        boolean habilitada,
        String url,
        Duration timeout,
        Duration validade,
        Duration idadeMaximaLeitura,
        Duration cacheRegioes,
        int tamanhoLote,
        int falhasParaAbrir,
        Duration pausaAposFalhas,
        boolean demonstracaoHabilitada,
        Duration demonstracaoDuracao
) {

    public PrevisaoProperties {
        if (url == null || url.isBlank()) {
            url = "http://localhost:5000";
        }
        if (timeout == null) {
            timeout = Duration.ofSeconds(2);
        }
        if (validade == null) {
            validade = Duration.ofMinutes(30);
        }
        if (idadeMaximaLeitura == null) {
            idadeMaximaLeitura = Duration.ofMinutes(30);
        }
        if (cacheRegioes == null) {
            cacheRegioes = Duration.ofMinutes(10);
        }
        if (tamanhoLote <= 0 || tamanhoLote > 500) {
            tamanhoLote = 100;
        }
        if (falhasParaAbrir <= 0) {
            falhasParaAbrir = 3;
        }
        if (pausaAposFalhas == null) {
            pausaAposFalhas = Duration.ofMinutes(1);
        }
        if (demonstracaoDuracao == null) {
            demonstracaoDuracao = Duration.ofMinutes(30);
        }
    }
}

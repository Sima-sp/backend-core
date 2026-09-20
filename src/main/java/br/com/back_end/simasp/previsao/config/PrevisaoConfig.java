package br.com.back_end.simasp.previsao.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

/**
 * Registra as propriedades {@code ia.*}, o cliente HTTP e o agendador.
 *
 * <p>Fica aqui, e não na classe principal, para o módulo de previsão ser autocontido: apagar a
 * pasta {@code previsao} remove a integração inteira sem deixar sobra no resto do backend.</p>
 */
@Configuration
@EnableConfigurationProperties(PrevisaoProperties.class)
@EnableScheduling
public class PrevisaoConfig {

    /**
     * Cliente HTTP do serviço de IA, com timeout curto nos dois lados (conexão e leitura).
     * Sem timeout, uma requisição do app poderia ficar pendurada esperando a previsão.
     */
    @Bean("restClientIa")
    public RestClient restClientIa(PrevisaoProperties propriedades, RestClient.Builder construtor) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(propriedades.timeout());
        fabrica.setReadTimeout(propriedades.timeout());

        return construtor.baseUrl(propriedades.url()).requestFactory(fabrica).build();
    }
}

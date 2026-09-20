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
     *
     * <p>O construtor vem de {@code RestClient.builder()}, e não de um {@code RestClient.Builder}
     * injetado: no Spring Boot 4 esse bean só existe com o módulo de cliente HTTP, que este
     * projeto não usa. Assim o módulo de previsão não depende dessa autoconfiguração.</p>
     */
    @Bean("restClientIa")
    public RestClient restClientIa(PrevisaoProperties propriedades) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(propriedades.timeout());
        fabrica.setReadTimeout(propriedades.timeout());

        return RestClient.builder()
                .baseUrl(propriedades.url())
                .requestFactory(fabrica)
                .build();
    }
}

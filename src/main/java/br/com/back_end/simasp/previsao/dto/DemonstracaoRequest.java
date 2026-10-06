package br.com.back_end.simasp.previsao.dto;

import br.com.back_end.simasp.previsao.enums.CenarioChuvaEnum;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * Pedido para ligar o modo de demonstração.
 *
 * @param cenario         chuva simulada (obrigatório)
 * @param sensorIds       sensores afetados; nulo ou vazio = todos os ativos
 * @param nivelAgua       opcional: leitura simulada de água (%). 100 ou mais = transbordando
 * @param porcentagemLixo opcional: leitura simulada de lixo (%)
 * @param minutos         opcional: duração; depois disso o modo desliga sozinho
 */
public record DemonstracaoRequest(

        @NotNull(message = "O cenário deve ser informado.")
        CenarioChuvaEnum cenario,

        Set<Long> sensorIds,

        @DecimalMin(value = "0", message = "nivelAgua não pode ser negativo.")
        @DecimalMax(value = "200", message = "nivelAgua não pode passar de 200.")
        Double nivelAgua,

        @DecimalMin(value = "0", message = "porcentagemLixo não pode ser negativa.")
        @DecimalMax(value = "100", message = "porcentagemLixo não pode passar de 100.")
        Double porcentagemLixo,

        @Min(value = 1, message = "A duração mínima é de 1 minuto.")
        @Max(value = 180, message = "A duração máxima é de 180 minutos.")
        Integer minutos
) {
}

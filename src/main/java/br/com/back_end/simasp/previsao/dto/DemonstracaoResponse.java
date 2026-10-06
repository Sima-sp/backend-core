package br.com.back_end.simasp.previsao.dto;

import br.com.back_end.simasp.previsao.enums.CenarioChuvaEnum;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Situação do modo de demonstração.
 *
 * @param habilitado        se o modo pode ser ligado neste ambiente ({@code ia.demonstracao-habilitada})
 * @param ativo             se há um cenário valendo agora
 * @param cenario           cenário em vigor (nulo quando inativo)
 * @param descricao         o que o cenário simula
 * @param sensorIds         sensores afetados; vazio = todos os ativos
 * @param nivelAgua         leitura simulada de água, quando informada
 * @param porcentagemLixo   leitura simulada de lixo, quando informada
 * @param expiraEm          quando o modo desliga sozinho
 * @param previsoesGravadas previsões recalculadas por esta chamada (nulo na consulta)
 */
public record DemonstracaoResponse(
        boolean habilitado,
        boolean ativo,
        CenarioChuvaEnum cenario,
        String descricao,
        Set<Long> sensorIds,
        Double nivelAgua,
        Double porcentagemLixo,
        LocalDateTime expiraEm,
        Integer previsoesGravadas
) {
}

package br.com.sima.projeto_sima.dtos;

import br.com.sima.projeto_sima.enums.EstadoBueiroEnum;

public record MonitoramentoResiduoDTO(
        Integer id,
        Double latitude,
        Double longitude,
        EstadoBueiroEnum estadoBueiro
) {
}

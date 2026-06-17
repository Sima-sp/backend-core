package br.com.sima.projeto_sima.dtos;

import br.com.sima.projeto_sima.entitys.Usuario;
import br.com.sima.projeto_sima.enums.EstadoBueiroEnum;
import br.com.sima.projeto_sima.enums.NivelAguaEnum;

import java.util.List;

public record MonitoramentoDTO(
Integer id,
Double latitude,
Double longitude,
EstadoBueiroEnum estadoBueiro,
NivelAguaEnum nivelAgua
) {
}

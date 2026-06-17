package br.com.sima.projeto_sima.entitys;


import br.com.sima.projeto_sima.enums.EstadoBueiroEnum;
import br.com.sima.projeto_sima.enums.NivelAguaEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "TBL_MONITORAMENTO")

public class Monitoramento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MONITORAMENTO")
    private Integer id;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")

    private Double longitude;

    @Column(name = "ESTADO_BUEIRO")
    @Enumerated(EnumType.STRING)
    private EstadoBueiroEnum estadoBueiro;

    @Column(name = "NIVEL_AGUA")
    @Enumerated(EnumType.STRING)
    private NivelAguaEnum nivelAgua;

}

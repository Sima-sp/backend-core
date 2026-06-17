package br.com.sima.projeto_sima.entitys;

import br.com.sima.projeto_sima.enums.SimNaoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "TBL_USUARIO")
@Getter
@Setter

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Integer id;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name = "PERMISSAO_LOCALIZACAO")
    @Enumerated(EnumType.STRING)
    private SimNaoEnum permissaoLocalizacao;

    @Column(name = "PERMISSAO_ALERTA")
    @Enumerated(EnumType.STRING)
    private SimNaoEnum permissaoAlerta;

    @Column(name = "GOVERNAMENTAL")
    @Enumerated(EnumType.STRING)
    private SimNaoEnum governamental;

    @Column(name = "EMAIL")
    private String email;

}

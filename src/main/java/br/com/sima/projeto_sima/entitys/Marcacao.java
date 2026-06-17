package br.com.sima.projeto_sima.entitys;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "TBL_MARCACAO")

public class Marcacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MARCACAO")
    private Integer id;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name = "DATA_MARCACAO")
    private LocalDateTime dataMarcacao;

    @ManyToMany
    @JoinTable(name = "TBL_AREA_ALCANCE_ASSOCIATIVA", joinColumns = @JoinColumn(name = "ID_USUARIO"), inverseJoinColumns = @JoinColumn(name = "ID_MARCACAO"))
    private List<Usuario> usuarios;


}

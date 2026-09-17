package br.com.back_end.simasp.leitura.entity;


import br.com.back_end.simasp.leitura.enums.NivelRiscoEnum;
import br.com.back_end.simasp.sensor.entity.Sensor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "TBL_LEITURA")
@Entity
@Getter
@Setter
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LEITURA")
    private Long id;

    @JoinColumn(name = "ID_SENSOR")
    @ManyToOne
    private Sensor sensor;

    @Column(name = "NR_NIVEL_AGUA")
    private Double nivelAgua;

    @Column(name = "NR_PORCENTAGEM_DESPERDICIO")
    private Double porcentagemDesperdicio;

    @Column(name = "NR_CHUVA_MM")
    private Double chuvaMM;

    @Column(name = "DT_DATA_LEITURA")
    private LocalDateTime dataLeitura = LocalDateTime.now();

    @Column(name = "TX_NIVEL_RISCO")
    @Enumerated(EnumType.STRING)
    private NivelRiscoEnum nivelRisco;

    @Column(name = "NR_PROBABILIDADE_ALAGAMENTO")
    private Double probabilidadeAlagamento;

    @Column(name = "TX_PREVISAO_NIVEL_RISCO")
    private String previsaoNivelRisco;
}

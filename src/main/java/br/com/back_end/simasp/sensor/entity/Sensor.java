package br.com.back_end.simasp.sensor.entity;

import br.com.back_end.simasp.sensor.enums.StatusSensor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "TBL_SENSOR")
@Entity
@Getter
@Setter

public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "ID_SENSOR")
    private Long id;

    @Column (name = "NR_LATITUDE")
    private BigDecimal latitude;

    @Column (name = "NR_LONGITUDE")
    private BigDecimal longitude;

    @Column (name = "TX_VIZINHANCA")
    private String vizinhanca;

    @Column (name = "TX_STATUS_SENSOR")
    @Enumerated(EnumType.STRING)
    private StatusSensor status = StatusSensor.ATIVO;

    @Column (name = "DT_DATA_INSTALACAO")
    private LocalDateTime dataInstalacao = LocalDateTime.now();
}



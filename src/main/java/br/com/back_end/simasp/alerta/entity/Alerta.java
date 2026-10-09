package br.com.back_end.simasp.alerta.entity;

import br.com.back_end.simasp.alerta.enums.StatusAlertaEnum;
import br.com.back_end.simasp.leitura.entity.Leitura;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "TBL_ALERTA")
@Entity
@Getter
@Setter

public class Alerta {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "ID_ALERTA")
    private Long id;

    @JoinColumn(name = "ID_LEITURA")
    @ManyToOne
    private Leitura leitura;

    @Column(name = "TX_MENSAGEM")
    private String mensagem;

    @Column(name = "TX_STATUS")
    @Enumerated(EnumType.STRING)
    private StatusAlertaEnum status;

    @Column(name = "DT_ALERTA")
    private LocalDateTime dataAlerta = LocalDateTime.now();

}

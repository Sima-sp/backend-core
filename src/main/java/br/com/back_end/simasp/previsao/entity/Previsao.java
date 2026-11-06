package br.com.back_end.simasp.previsao.entity;

import br.com.back_end.simasp.leitura.enums.NivelRiscoEnum;
import br.com.back_end.simasp.previsao.enums.OrigemPrevisaoEnum;
import br.com.back_end.simasp.sensor.entity.Sensor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Previsão de alagamento gerada pelo serviço de IA para um sensor.
 *
 * <p>Fica separada de {@code Leitura} porque a previsão existe mesmo sem leitura nova: o
 * agendador recalcula de tempo em tempo usando só a chuva. Guardar o histórico também permite,
 * depois, comparar o que foi previsto com o que aconteceu.</p>
 */
@Table(name = "TBL_PREVISAO")
@Entity
@Getter
@Setter
public class Previsao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PREVISAO")
    private Long id;

    @JoinColumn(name = "ID_SENSOR")
    @ManyToOne(fetch = FetchType.LAZY)
    private Sensor sensor;

    /** 0 a 1, calibrada. Nula quando a origem não é MODELO (não há probabilidade honesta). */
    @Column(name = "NR_PROBABILIDADE_ALAGAMENTO")
    private Double probabilidadeAlagamento;

    /** Nível final mostrado ao usuário (já com o ajuste pela leitura do sensor). */
    @Column(name = "TX_NIVEL_RISCO")
    @Enumerated(EnumType.STRING)
    private NivelRiscoEnum nivelRisco;

    /** Nível só pela probabilidade, antes do ajuste do sensor. */
    @Column(name = "TX_NIVEL_RISCO_MODELO")
    @Enumerated(EnumType.STRING)
    private NivelRiscoEnum nivelRiscoModelo;

    @Column(name = "NR_JANELA_HORAS")
    private Integer janelaHoras = 3;

    @Column(name = "DT_GERADA_EM")
    private LocalDateTime geradaEm = LocalDateTime.now();

    /** Depois disso a previsão conta como DESATUALIZADA no mapa. */
    @Column(name = "DT_VALIDA_ATE")
    private LocalDateTime validaAte;

    /** Hora cheia usada nas features de chuva. */
    @Column(name = "DT_HORA_REFERENCIA")
    private LocalDateTime horaReferencia;

    @Column(name = "TX_ORIGEM")
    @Enumerated(EnumType.STRING)
    private OrigemPrevisaoEnum origem;

    @Column(name = "TX_MODELO_VERSAO")
    private String modeloVersao;

    @Column(name = "NR_CHUVA_RECENTE_3H_MM")
    private Double chuvaRecente3hMm;

    @Column(name = "NR_CHUVA_PREVISTA_3H_MM")
    private Double chuvaPrevista3hMm;

    @Column(name = "TX_FONTE_CHUVA")
    private String fonteChuva;

    /** True quando o sensor mediu transbordamento: o app troca a porcentagem pelo aviso. */
    @Column(name = "FL_MEDICAO_TRANSBORDANDO")
    private Boolean medicaoTransbordando = false;

    @Column(name = "FL_AJUSTE_SENSOR_APLICADO")
    private Boolean ajusteSensorAplicado = false;

    @Column(name = "FL_SEM_LEITURA_SENSOR")
    private Boolean semLeituraSensor = false;

    /** Motivos do ajuste, separados por ";" (lista curta vinda do serviço de IA). */
    @Column(name = "TX_MOTIVOS_AJUSTE")
    private String motivosAjuste;

    @Column(name = "TX_PONTO_ID")
    private String pontoId;

    @Column(name = "NR_DISTANCIA_PONTO_M")
    private Double distanciaPontoM;
}

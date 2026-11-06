package br.com.back_end.simasp.previsao.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cenários de chuva do modo de demonstração.
 *
 * <p>Cada cenário é uma série de 72 horas (mm por hora) que termina na hora atual. O serviço de
 * IA usa essa série no lugar da chuva real do Open-Meteo. São os mesmos cenários de
 * {@code scripts/demo_cenarios.py} do ml-service, para a demonstração pelo app e a pelo terminal
 * darem o mesmo resultado.</p>
 */
public enum CenarioChuvaEnum {

    /** Três dias sem chuva: tudo deve ficar em BAIXO. */
    SECO("Sem chuva nas últimas 72 horas"),

    /** Chuva moderada nas últimas horas (10,5 mm em 5 h). */
    CHUVA_MODERADA("Chuva moderada: 10,5 mm nas últimas 5 horas", 0.0, 0.5, 1.0, 3.0, 4.0, 2.0),

    /** Pancada forte (68,5 mm em 6 h, 60 mm nas últimas 3 h). */
    CHUVA_FORTE("Pancada forte: 68,5 mm nas últimas 6 horas", 0.5, 2.0, 6.0, 15.0, 25.0, 20.0);

    private static final int HORAS = 72;

    private final String descricao;
    private final List<Double> serie;

    CenarioChuvaEnum(String descricao, double... ultimasHoras) {
        this.descricao = descricao;
        List<Double> valores = new ArrayList<>(Collections.nCopies(HORAS - ultimasHoras.length, 0.0));
        for (double valor : ultimasHoras) {
            valores.add(valor);
        }
        this.serie = Collections.unmodifiableList(valores);
    }

    public String descricao() {
        return descricao;
    }

    /** 72 valores horários em mm; o último é a hora de referência (a mais recente). */
    public List<Double> serie() {
        return serie;
    }

    public static int horas() {
        return HORAS;
    }
}

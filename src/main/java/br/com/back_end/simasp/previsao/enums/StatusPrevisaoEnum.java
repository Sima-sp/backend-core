package br.com.back_end.simasp.previsao.enums;

/** Situação da última previsão de um sensor, para o mapa não esconder o ponto. */
public enum StatusPrevisaoEnum {
    /** Dentro da validade. */
    VALIDA,
    /** Existe previsão, mas já passou de ``validaAte``. */
    DESATUALIZADA,
    /** O sensor nunca recebeu previsão. */
    SEM_PREVISAO
}

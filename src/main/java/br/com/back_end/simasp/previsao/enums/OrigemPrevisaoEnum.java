package br.com.back_end.simasp.previsao.enums;

/** De onde veio a previsão: do modelo treinado ou do baseline de regras do serviço de IA. */
public enum OrigemPrevisaoEnum {
    /** Modelo treinado + calibração (tem probabilidade). */
    MODELO,
    /** Serviço sem modelo carregado: regras fixas, sem probabilidade. */
    REGRAS,
    /** O serviço de IA não respondeu: nível estimado localmente pela leitura do sensor. */
    INDISPONIVEL
}

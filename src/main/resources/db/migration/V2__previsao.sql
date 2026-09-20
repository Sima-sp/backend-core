-- Previsões do serviço de IA.
--
-- Tabela separada da TBL_LEITURA porque a previsão existe mesmo sem leitura nova: o agendador
-- recalcula de tempo em tempo usando só a chuva. Guardar o histórico permite comparar depois o
-- que foi previsto com o que aconteceu.

CREATE TABLE TBL_PREVISAO
(
    ID_PREVISAO                     BIGINT          NOT NULL    PRIMARY KEY AUTO_INCREMENT,
    ID_SENSOR                       BIGINT          NOT NULL,
    NR_PROBABILIDADE_ALAGAMENTO     DOUBLE           NULL,
    TX_NIVEL_RISCO                  VARCHAR(20)     NOT NULL,
    TX_NIVEL_RISCO_MODELO           VARCHAR(20)     NULL,
    NR_JANELA_HORAS                 INT             NOT NULL    DEFAULT 3,
    DT_GERADA_EM                    DATETIME(2)     NOT NULL    DEFAULT CURRENT_TIMESTAMP(2),
    DT_VALIDA_ATE                   DATETIME(2)     NULL,
    DT_HORA_REFERENCIA              DATETIME(2)     NULL,
    TX_ORIGEM                       VARCHAR(20)     NOT NULL,
    TX_MODELO_VERSAO                VARCHAR(40)     NULL,
    NR_CHUVA_RECENTE_3H_MM          DOUBLE           NULL,
    NR_CHUVA_PREVISTA_3H_MM         DOUBLE           NULL,
    TX_FONTE_CHUVA                  VARCHAR(30)     NULL,
    FL_MEDICAO_TRANSBORDANDO        BOOLEAN         NOT NULL    DEFAULT FALSE,
    FL_AJUSTE_SENSOR_APLICADO       BOOLEAN         NOT NULL    DEFAULT FALSE,
    FL_SEM_LEITURA_SENSOR           BOOLEAN         NOT NULL    DEFAULT FALSE,
    TX_MOTIVOS_AJUSTE               VARCHAR(500)    NULL,
    TX_PONTO_ID                     VARCHAR(20)     NULL,
    NR_DISTANCIA_PONTO_M            DOUBLE           NULL,

    CONSTRAINT  SEN_FK_PRE      FOREIGN KEY (ID_SENSOR) REFERENCES TBL_SENSOR (ID_SENSOR),
    CONSTRAINT  CHK_PRE_RISCO   CHECK (TX_NIVEL_RISCO IN ('BAIXO', 'MEDIO', 'ALTO', 'CRITICO')),
    CONSTRAINT  CHK_PRE_MODELO  CHECK (TX_NIVEL_RISCO_MODELO IN ('BAIXO', 'MEDIO', 'ALTO', 'CRITICO')),
    CONSTRAINT  CHK_PRE_ORIGEM  CHECK (TX_ORIGEM IN ('MODELO', 'REGRAS', 'INDISPONIVEL')),
    CONSTRAINT  CHK_PRE_PROB    CHECK (NR_PROBABILIDADE_ALAGAMENTO IS NULL
                                       OR NR_PROBABILIDADE_ALAGAMENTO BETWEEN 0 AND 1)
);

-- O mapa e a tela do sensor sempre pedem "a última previsão deste sensor".
CREATE INDEX IDX_PRE_SENSOR_DATA ON TBL_PREVISAO (ID_SENSOR, DT_GERADA_EM DESC);

-- DOUBLE, e não FLOAT: as colunas são mapeadas para Double no Java e o spring.jpa.hibernate.
-- ddl-auto=validate compara os tipos na subida. (Se a validação reclamar das colunas FLOAT da
-- TBL_LEITURA, a correção é a mesma: MODIFY ... DOUBLE.)

-- Sem modelo carregado, o serviço de IA responde em modo regras e NÃO devolve probabilidade
-- (probabilidade inventada enganaria o usuário). A coluna da leitura precisa aceitar nulo.
ALTER TABLE TBL_LEITURA MODIFY NR_PROBABILIDADE_ALAGAMENTO FLOAT NULL;
ALTER TABLE TBL_LEITURA MODIFY TX_PREVISAO_NIVEL_RISCO VARCHAR(20) NULL;

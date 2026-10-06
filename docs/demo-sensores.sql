-- Sensores de demonstração sobre pontos monitorados da Zona Norte.
--
-- POR QUE: o modelo conhece 137 pontos com histórico de alagamento. Um sensor a mais de 500 m de
-- qualquer um deles é previsto sem o histórico do ponto, e o risco fica baixo mesmo com chuva
-- forte. Os "Locais de teste" do script de exemplo estão quase todos nessa situação (só o 06 fica
-- perto de um ponto), então a previsão quase não se mexe na demonstração.
--
-- O QUE FAZ: move os 8 sensores de exemplo para pontos reais da Zona Norte, com o nome do lugar.
-- Não cria nem apaga nada, e não é migration: rode à mão, só no banco de demonstração.
--
--   mysql -u root -p simasp_db < docs/demo-sensores.sql
--
-- Com o cenário CHUVA_FORTE do modo de demonstração, os sensores 1 a 3 costumam ir para ALTO,
-- o 4 fica entre MEDIO e ALTO e o 5 e o 6 em MEDIO. O 7 e o 8 seguem INATIVO e em MANUTENCAO.

UPDATE TBL_SENSOR SET NR_LATITUDE = -23.518728, NR_LONGITUDE = -46.630333,
       TX_VIZINHANCA = 'Marginal Tietê — Santana / Tucuruvi'             WHERE ID_SENSOR = 1;
UPDATE TBL_SENSOR SET NR_LATITUDE = -23.503717, NR_LONGITUDE = -46.685085,
       TX_VIZINHANCA = 'Av. Antônio Munhoz Bonilha — Casa Verde / Limão' WHERE ID_SENSOR = 2;
UPDATE TBL_SENSOR SET NR_LATITUDE = -23.527245, NR_LONGITUDE = -46.596878,
       TX_VIZINHANCA = 'Marginal Tietê — Vila Maria / Vila Guilherme'    WHERE ID_SENSOR = 3;
UPDATE TBL_SENSOR SET NR_LATITUDE = -23.512827, NR_LONGITUDE = -46.625122,
       TX_VIZINHANCA = 'Av. Cruzeiro do Sul — Santana / Tucuruvi'        WHERE ID_SENSOR = 4;
UPDATE TBL_SENSOR SET NR_LATITUDE = -23.508715, NR_LONGITUDE = -46.600140,
       TX_VIZINHANCA = 'R. Chico Pontes — Vila Maria / Vila Guilherme'   WHERE ID_SENSOR = 5;
UPDATE TBL_SENSOR SET NR_LATITUDE = -23.504080, NR_LONGITUDE = -46.624783,
       TX_VIZINHANCA = 'R. Darzan — Santana / Tucuruvi'                  WHERE ID_SENSOR = 6;
UPDATE TBL_SENSOR SET NR_LATITUDE = -23.472675, NR_LONGITUDE = -46.571480,
       TX_VIZINHANCA = 'Av. Edu Chaves — Jaçanã / Tremembé'              WHERE ID_SENSOR = 7;
UPDATE TBL_SENSOR SET NR_LATITUDE = -23.503626, NR_LONGITUDE = -46.617527,
       TX_VIZINHANCA = 'Av. Gal. Ataliba Leonel — Santana / Tucuruvi'    WHERE ID_SENSOR = 8;

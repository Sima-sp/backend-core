# Integração com o serviço de IA (ml-service)

> Módulo `br.com.back_end.simasp.previsao`. O serviço de IA vive em outro repositório
> (`Sima-sp/ml-service`) e responde em HTTP. Aqui fica só o cliente, o agendador e o
> armazenamento das previsões.

## Como funciona

```
sensor + última leitura ──► PrevisaoService ──► PrevisaoIaClient ──► POST /predict
                                   │                                  (ml-service:5000)
                                   └──► TBL_PREVISAO ──► GET /previsoes (mapa do app)
```

1. O **agendador** (`PrevisaoAgendador`) roda a cada 10 min e recalcula todos os sensores
   ATIVOS numa chamada de lote (`POST /predict/batch`). Dez minutos bastam porque a chuva do
   Open-Meteo só muda de hora em hora.
2. Quando chega leitura nova, dá para recalcular na hora com
   `POST /previsoes/sensores/{id}/atualizar`.
3. O resultado vira uma linha em `TBL_PREVISAO`. O mapa lê a **última previsão de cada sensor**.

## Decisões que valem lembrar

- **A chuva é do serviço de IA.** O backend manda só posição e leitura do sensor. Se o backend
  mandasse a chuva, ela poderia vir de outra fonte ou ser calculada de outro jeito, e o modelo
  receberia números diferentes dos que viu no treino.
- **Probabilidade pode ser nula.** Sem modelo carregado, o serviço responde em modo regras e
  **não** devolve probabilidade. Por isso `NR_PROBABILIDADE_ALAGAMENTO` aceita nulo e o DTO usa
  `Double`, não `double`.
- **Tabela separada da leitura.** A previsão existe mesmo sem leitura nova, e guardar o
  histórico permite comparar depois o previsto com o ocorrido.
- **Sem resposta, nada é inventado.** Falhou a chamada? A última previsão continua no mapa
  marcada como `DESATUALIZADA`. O ponto não some e nenhum nível é chutado.
- **A previsão nunca trava o app.** Timeout de 2 s e disjuntor: depois de 3 falhas seguidas, o
  cliente para de tentar por 1 min.
- **Nível vem sem acento** (`BAIXO`, `MEDIO`, `ALTO`, `CRITICO`), igual ao `NivelRiscoEnum`.

## Endpoints

| Método | Rota | Para quê |
|---|---|---|
| GET | `/previsoes` | Mapa: um item por sensor, com `status` VALIDA / DESATUALIZADA / SEM_PREVISAO |
| GET | `/previsoes/sensores/{id}` | Última previsão de um sensor |
| POST | `/previsoes/sensores/{id}/atualizar` | Recalcula na hora (503 se o serviço de IA não responder) |
| GET | `/previsoes/regioes` | Risco por subprefeitura, para o aviso de região do app |
| GET | `/admin/demonstracao` | Diz se há cenário de chuva simulada valendo |
| POST | `/admin/demonstracao/iniciar` | Liga um cenário e recalcula as previsões |
| POST | `/admin/demonstracao/encerrar` | Desliga e recalcula com a chuva real |
| POST | `/admin/previsoes/atualizar` | Recalcula todos os sensores ativos |
| POST | `/admin/previsoes/checar` | Diz se o serviço responde e qual versão do modelo está carregada |

Campos da resposta que o app usa: `nivelRisco` (principal), `probabilidadeAlagamento`
(secundário, pode ser nulo), `medicaoTransbordando` (troca a porcentagem por "Transbordando
agora (medido)"), `semLeituraSensor` e `status`.

## Configuração (`.env` ou variáveis de ambiente)

| Propriedade | Padrão | O que é |
|---|---|---|
| `ia.habilitada` | `true` | Desligada, o backend não chama o serviço e o agendador não sobe |
| `ia.url` | `http://localhost:5000` | Base do ml-service (no Docker: `http://ml-service:5000`) |
| `ia.timeout` | `2s` | Conexão e leitura |
| `ia.validade` | `30m` | Usada só quando a resposta não traz `validaAte` |
| `ia.idade-maxima-leitura` | `30m` | Leitura mais velha que isso não é enviada |
| `ia.cache-regioes` | `10m` | Cache do aviso por região no backend |
| `ia.demonstracao-habilitada` | `false` | Permite ligar a chuva simulada (`IA_DEMONSTRACAO=true` no `.env`) |
| `ia.demonstracao-duracao` | `30m` | Depois disso a demonstração desliga sozinha |
| `ia.tamanho-lote` | `100` | Itens por chamada de lote (o serviço aceita até 500) |
| `ia.falhas-para-abrir` | `3` | Falhas seguidas que desligam o serviço temporariamente |
| `ia.pausa-apos-falhas` | `1m` | Quanto tempo ficar sem tentar |
| `ia.intervalo` | `PT10M` | Intervalo do agendador |

## Testar na mão

```bash
# 1. sobe o serviço de IA (no repositório ml-service)
docker compose up -d ml-service
curl http://localhost:5000/health

# 2. sobe o backend e confere a ligação
curl -X POST http://localhost:8080/admin/previsoes/checar
curl -X POST http://localhost:8080/admin/previsoes/atualizar
curl http://localhost:8080/previsoes | jq
```

Sem o serviço de IA no ar, o esperado é: `checar` devolvendo `respondeu: false`, `atualizar`
devolvendo `previsoesGravadas: 0` e o mapa com os sensores em `SEM_PREVISAO`. O backend segue
funcionando.

## Aviso por região

`GET /previsoes/regioes` devolve o risco por **subprefeitura**: o maior risco entre os pontos
monitorados de cada uma. O motivo está no ADR 0005 do ml-service — por ponto, o alerta ALTO
acerta 1,6 %; por subprefeitura, 8 %. É a escala em que a chuva de 9 km do modelo realmente
enxerga.

Dois cuidados na tela:

- `cobertura = "SEM_COBERTURA"` significa que a região tem menos de 3 pontos monitorados. O
  nível vem nulo, e a tela deve dizer "sem cobertura" — **nunca** "sem risco".
- O aviso de região **não substitui** o nível por ponto: o mapa e o desvio de rota continuam
  saindo de `/previsoes`.

O resultado fica 10 minutos em cache no backend, porque a chuva do Open-Meteo só muda de hora em
hora. Nada disso vai para o banco: o histórico por ponto na `TBL_PREVISAO` já permite
reconstruir a região depois.

## Modo de demonstração

Num dia sem chuva todas as previsões ficam em BAIXO. Está certo, mas não deixa mostrar o sistema
funcionando. O modo de demonstração troca a **chuva** por um cenário simulado; o modelo, os
limiares e o ajuste pelo sensor continuam sendo os de verdade.

```bash
# 1. no .env do backend, e reinicie
IA_DEMONSTRACAO=true

# 2. chuva forte em todos os sensores, por 20 minutos
curl -X POST http://localhost:8080/admin/demonstracao/iniciar \
     -H 'Content-Type: application/json' \
     -d '{"cenario": "CHUVA_FORTE", "minutos": 20}'

# 3. bueiro transbordando só no sensor 1 (medição, não previsão)
curl -X POST http://localhost:8080/admin/demonstracao/iniciar \
     -H 'Content-Type: application/json' \
     -d '{"cenario": "CHUVA_FORTE", "sensorIds": [1], "nivelAgua": 105}'

# 4. volta para a chuva real
curl -X POST http://localhost:8080/admin/demonstracao/encerrar
```

| Cenário | Chuva simulada | O que costuma aparecer |
|---|---|---|
| `SECO` | nada em 72 h | tudo em BAIXO |
| `CHUVA_MODERADA` | 10,5 mm nas últimas 5 h | parte dos pontos em MEDIO e alguns em ALTO |
| `CHUVA_FORTE` | 68,5 mm nas últimas 6 h | maioria em MEDIO ou ALTO |

Como funciona e o que cuidar:

- **Só liga com `ia.demonstracao-habilitada=true`.** O padrão é desligado; sem isso o
  `iniciar` responde 403. Não deixe ligado num ambiente de verdade.
- **Desliga sozinho** depois de `ia.demonstracao-duracao` (ou dos `minutos` pedidos), caso
  alguém esqueça. Reiniciar o backend também encerra, porque o estado fica em memória.
- **Toda previsão simulada sai marcada:** `simulada: true` em `/previsoes` e
  `fonteChuva: "INFORMADA"` em `/previsoes/regioes`. O app deve mostrar um aviso de simulação
  enquanto isso for verdade.
- **O histórico não se mistura:** as linhas simuladas ficam na `TBL_PREVISAO` com
  `TX_FONTE_CHUVA = 'INFORMADA'`, então dá para filtrá-las em qualquer análise.
- **O nível depende do dia e da hora.** O modelo usa hora, dia da semana e mês, então a mesma
  chuva forte dá mais pontos em ALTO numa tarde de dia útil do que num domingo à noite. Ensaie
  no mesmo horário da apresentação.
- **Os sensores precisam estar sobre pontos monitorados.** A mais de 500 m de um dos 137 pontos
  do modelo, o risco quase não sobe nem com chuva forte, porque falta o histórico do lugar. Os
  "Locais de teste" do script de exemplo estão quase todos assim. O `docs/demo-sensores.sql`
  move os oito para pontos reais da Zona Norte; rode antes de apresentar.
- `sensorIds` limita o cenário a alguns sensores; o aviso por região, quando há cenário ligado,
  aplica a chuva a todos os pontos da cidade.
- `nivelAgua` e `porcentagemLixo` simulam a leitura do sensor. Com `nivelAgua` de 100 ou mais,
  o nível vira CRITICO com `medicaoTransbordando: true`.

## Ainda em aberto

- **Alerta automático.** `TBL_ALERTA` exige `ID_LEITURA`, então hoje só dá para gerar alerta
  quando existe leitura. Para alertar por previsão (o caso "vai alagar em 3 h"), o alerta
  precisa poder apontar para uma previsão. Decisão do grupo.
- **WebSocket.** O projeto já tem o starter; falta publicar as mudanças de nível para o app não
  ficar consultando de tempos em tempos.
- **Alerta automático por região.** O aviso já existe em `/previsoes/regioes`, mas ninguém é
  notificado por ele ainda — depende da decisão sobre alerta a partir de previsão.

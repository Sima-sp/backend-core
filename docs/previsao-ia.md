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

## Ainda em aberto

- **Alerta automático.** `TBL_ALERTA` exige `ID_LEITURA`, então hoje só dá para gerar alerta
  quando existe leitura. Para alertar por previsão (o caso "vai alagar em 3 h"), o alerta
  precisa poder apontar para uma previsão. Decisão do grupo.
- **WebSocket.** O projeto já tem o starter; falta publicar as mudanças de nível para o app não
  ficar consultando de tempos em tempos.
- **Alerta por região.** O modelo acerta bem mais por zona do que por ponto. Seria um endpoint
  novo ("risco elevado na Zona Norte nas próximas 3 h"). Ver R18 no ml-service.

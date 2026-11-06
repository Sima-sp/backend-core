# SIMA-SP · backend

API do SIMA-SP (Sistema Inteligente de Monitoramento de Alagamentos em São Paulo), TCC.
Recebe os sensores, guarda leituras e previsões e serve o app e o site.

- Java 21, Spring Boot, Spring Data JPA, Spring Security
- MySQL 8, com o esquema versionado pelo Flyway (`src/main/resources/db/migration`)
- Serviço de IA à parte: [`Sima-sp/ml-service`](https://github.com/Sima-sp/ml-service)

## Rodar

1. Um MySQL 8 com um banco vazio. Com Docker:

   ```
   docker run -d --name sima-mysql -p 3306:3306 \
     -e MYSQL_DATABASE=simasp_db -e MYSQL_USER=simasp_user \
     -e MYSQL_PASSWORD=simasp_password -e MYSQL_ROOT_PASSWORD=root mysql:8.4
   ```

2. Copie `.env.example` para `.env` e preencha. O `JWT_SECRET` precisa estar em Base64 e ter pelo
   menos 32 bytes: `openssl rand -base64 48`. O `.env` não vai para o git.
3. `./mvnw spring-boot:run` (porta 8080).

## Testes

`./mvnw verify`. O teste de contexto precisa do MySQL do passo 1 e das variáveis do `.env`.

## Como o grupo trabalha

- Ninguém envia direto para a `main`: cada mudança vai numa branch (`feat/...`, `fix/...`) e entra
  por pull request.
- O PR só pode ser juntado com o CI passando (job `ci`: build e testes com MySQL de teste).
- O merge é sempre *squash* (um commit por PR) e a branch é apagada depois.
- Mudou uma entidade ou tabela? Crie uma migração nova (`V<n>__descricao.sql`). Uma migração que já
  está na `main` nunca é editada.
- O Dependabot abre, às segundas, um PR com as atualizações menores de dependências.

# Raízes do Nordeste — API Back-end

> **Modelo para completar e conferir pessoalmente antes da entrega.** Registre apenas funcionalidades e testes que você efetivamente validou.

## Finalidade
[Explique, com suas palavras, o problema do estudo de caso e o fluxo de Back-end escolhido.]

## Tecnologias
- Java 21 (compilacao com `--release 21`)
- Spring Boot 4.1.1
- Maven Wrapper
- Spring Data JPA / Hibernate
- MySQL 8
- Spring Security com JWT e BCrypt
- Swagger / OpenAPI

## Pré-requisitos
- JDK 21 ou superior compatível com o projeto
- MySQL 8 com um banco de dados criado
- PowerShell (Windows) ou terminal equivalente
- Cliente HTTP (Postman ou Swagger)

## Banco de dados
- Nome usado localmente: `raizes_do_nordeste`.
- Criar o banco no MySQL antes de iniciar a API.
- O projeto utiliza `spring.jpa.hibernate.ddl-auto=update` no ambiente local.
- [Documente como preparar dados iniciais e se existe script SQL específico.]

## Configuração de ambiente
Copie as **chaves** de `.env.example`, mas configure os **valores privados** nas variáveis de ambiente do sistema ou no ambiente de execução do IntelliJ. O Spring Boot **não lê o arquivo `.env` automaticamente**.

Variáveis usadas no `application.properties`:
- `DB_URL` — URL de conexão JDBC.
- `DB_USERNAME` — usuário MySQL.
- `DB_PASSWORD` — senha MySQL.
- `APP_JWT_SECRET` — segredo JWT de ao menos 32 bytes UTF-8.
- `APP_ADMIN_EMAIL` e `APP_ADMIN_PASSWORD` — bootstrap opcional, apenas no primeiro uso, se necessário.

Não publique credenciais, tokens ou dados reais dos usuários.

## Executar localmente
Dentro da pasta que contém `pom.xml`:

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

Swagger local: `http://localhost:8080/swagger-ui.html`

## Endpoints
- `POST /auth/login`
- `/usuarios`
- `/unidades`
- `GET /produtos` e `GET /produtos?unidadeId={id}`
- `/estoque`
- `/pedidos`
- `/pagamentos`
- `/fidelidade`
- `/promocoes`

[Confronte métodos, rotas e códigos de resposta com o Swagger atual antes de publicar.]

## Perfis de acesso
[Preencha a matriz final de CLIENTE, ATENDENTE, COZINHA, GERENTE e ADMIN conforme o `SecurityConfig` realmente publicado.]

## Fluxo principal
[Descreva a sequência de criação de pedido, estoque, pagamento simulado e atualização de status, com um exemplo reproduzível.]

## Testes
- [Importar a coleção Postman/Insomnia `.json` para o repositório.]
- [Informar pasta da coleção, ordem das requisições, configuração do ambiente e obtenção do token.]
- [Registrar os cenários positivos/negativos e evidências validadas.]

## Modelo de dados e diagramas
[Indique onde estão o DER atualizado e os diagramas de casos de uso.]

## Observações e limites
[Esclareça o que foi implementado, o que é simulado e o que ficou fora do escopo.]

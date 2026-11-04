# Raízes do Nordeste — API REST Back-end

API desenvolvida para organizar operações de uma rede de alimentação, com cadastro de usuários, unidades, produtos, controle de estoque por unidade, pedidos, pagamentos simulados, fidelidade e promoções. Este repositório contém o **back-end**, sem interface web ou aplicativo cliente.

## Tecnologias e arquitetura

| Componente | Tecnologia presente no projeto |
|---|---|
| Linguagem | Java 21 como versão definida no `pom.xml` (o projeto também foi iniciado localmente com JDK 25) |
| Framework | Spring Boot 4.1.1 / Spring Web MVC |
| Persistência | Spring Data JPA, Hibernate e MySQL Connector/J |
| Banco de dados | MySQL 8 |
| Autenticação | Spring Security, JWT com JJWT 0.12.6 e BCrypt |
| Validação | Jakarta Bean Validation |
| Documentação interativa | Springdoc OpenAPI 3.1.1 / Swagger UI |
| Gerenciamento de dependências | Maven Wrapper (`mvnw` e `mvnw.cmd`) |

O código está organizado no pacote `br.com.balbino.raizes`:

```text
src/main/java/br/com/balbino/raizes/
├── api/           # Controllers, objetos de requisição/resposta e tratamento de erros
├── application/   # Serviços e regras de negócio
├── config/        # Segurança, filtro JWT, bootstrap e OpenAPI
├── domain/        # Entidades JPA
└── repository/    # Interfaces de acesso ao banco de dados
```

## Funcionalidades implementadas

- **Autenticação:** login com e-mail e senha; token JWT com validade de **1 hora**; senha dos usuários armazenada por hash BCrypt.
- **Usuários:** cadastro, consulta e exclusão, com perfis `ADMIN`, `GERENTE`, `ATENDENTE`, `COZINHA` e `CLIENTE`.
- **Unidades:** cadastro e consulta, com situação ativa/inativa.
- **Produtos:** cadastro e listagem geral; consulta do cardápio disponível por unidade com `GET /produtos?unidadeId=...`.
- **Estoque:** cadastro/atualização do saldo de um produto em determinada unidade e consulta dos registros.
- **Pedidos:** criação com itens, reserva de estoque, filtro por canal, consulta, cancelamento e progressão de status.
- **Pagamentos:** processamento **simulado** como `APROVADO` ou `RECUSADO`, associado a um pedido.
- **Fidelidade:** adesão, consulta, alteração de consentimento, crédito e resgate de pontos.
- **Promoções:** cadastro, consulta, listagem das promoções vigentes e desativação.

## Pré-requisitos

1. **JDK 21 ou superior** compatível com as dependências; a versão do Java configurada no `pom.xml` é 21.
2. **MySQL 8** em execução e uma conta com permissão de acesso ao banco.
3. Terminal PowerShell no Windows, ou terminal equivalente; o IntelliJ IDEA é opcional.
4. Postman ou Swagger UI para enviar requisições HTTP.

Confira o Java instalado com `java -version`.

## Preparar o banco de dados

No MySQL Workbench, conecte-se ao servidor e execute:

```sql
CREATE DATABASE IF NOT EXISTS raizes_do_nordeste
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

A conexão padrão configurada no projeto é `jdbc:mysql://localhost:3306/raizes_do_nordeste`.

O arquivo `src/main/resources/application.properties` utiliza `spring.jpa.hibernate.ddl-auto=update`: no ambiente local, o Hibernate cria ou atualiza a estrutura das entidades quando a aplicação inicia, desde que o banco exista e o usuário possua as permissões necessárias. **Esse mecanismo não substitui migrações versionadas para produção.** O ZIP analisado não inclui um script SQL independente de criação das tabelas nem uma carga de dados iniciais.

As entidades mapeadas são `Usuario`, `Unidade`, `Produto`, `Estoque`, `Pedido`, `ItemPedido`, `Pagamento`, `Fidelidade` e `Promocao`. Não existe entidade `PRODUTO_UNIDADE` no código: a associação entre produto e unidade é feita por `Estoque`, com restrição de unicidade para o par (`produto_id`, `unidade_id`).

## Configuração das variáveis de ambiente

O arquivo `.env.example` documenta as variáveis, mas **o Spring Boot deste projeto não carrega `.env` automaticamente**. Configure os valores no ambiente de execução do IntelliJ ou no terminal em que a aplicação será iniciada.

| Variável | Obrigatória? | Utilização |
|---|---|---|
| `DB_URL` | Não | URL JDBC; se ausente, utiliza `jdbc:mysql://localhost:3306/raizes_do_nordeste`. |
| `DB_USERNAME` | Sim | Usuário de conexão com o MySQL. |
| `DB_PASSWORD` | Sim | Senha de conexão com o MySQL. |
| `APP_JWT_SECRET` | Sim | Chave usada para assinar e validar JWT; deve possuir no mínimo **32 bytes UTF-8**. |
| `APP_ADMIN_EMAIL` | Não | E-mail do administrador inicial, somente no bootstrap. |
| `APP_ADMIN_PASSWORD` | Não | Senha do administrador inicial, com **12 caracteres ou mais**. |

### Configurar pelo IntelliJ IDEA

1. Abra o projeto na pasta que contém `pom.xml`.
2. Acesse a configuração de execução da classe `RaizesDoNordesteBackendApplication` em **Run → Edit Configurations...** (o caminho pode variar conforme a interface).
3. Em **Environment variables**, configure `DB_USERNAME`, `DB_PASSWORD` e `APP_JWT_SECRET`. Configure `DB_URL` somente se sua conexão não usar o endereço padrão.
4. Salve com **Apply** e **OK** e execute a aplicação pelo botão **Run**.

**Importante:** variáveis configuradas apenas na execução do IntelliJ não são automaticamente compartilhadas com um PowerShell aberto fora dessa execução. Se utilizar `mvnw.cmd spring-boot:run` no terminal, forneça as variáveis também ao processo desse terminal.

Para gerar uma chave JWT aleatória no PowerShell, sem usar uma chave fixa no repositório:

```powershell
$bytes = New-Object byte[] 48
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
[Convert]::ToBase64String($bytes)
$rng.Dispose()
```

Guarde a chave em local privado e use o resultado em `APP_JWT_SECRET`. **Não publique credenciais, chaves JWT, tokens ou arquivos locais de configuração da IDE.**

### Primeiro administrador

O cadastro de usuários exige autorização de `ADMIN` ou `GERENTE`. Em um banco novo, o primeiro administrador pode ser criado automaticamente pelo `AdminBootstrap`, informando **as duas variáveis** `APP_ADMIN_EMAIL` e `APP_ADMIN_PASSWORD` antes da primeira execução.

- A senha precisa ter pelo menos 12 caracteres e será armazenada com BCrypt.
- Se já existir um usuário com o e-mail informado, o bootstrap lança um erro de inicialização.
- Se já existir um usuário com perfil `ADMIN` e o e-mail informado ainda não estiver cadastrado, o bootstrap não cria outra conta.
- Depois de criar o administrador, **remova as duas variáveis opcionais da configuração de execução** e reinicie a aplicação normalmente.

## Executar a aplicação

Na pasta que contém `pom.xml`, pelo PowerShell:

```powershell
# Confere a compilação
.\mvnw.cmd clean compile

# Inicia o serviço, desde que as variáveis de ambiente estejam disponíveis
.\mvnw.cmd spring-boot:run
```

Também é possível iniciar diretamente a classe `RaizesDoNordesteBackendApplication` pelo IntelliJ, com as variáveis configuradas no campo **Environment variables**.

A aplicação usa a porta **8080** na configuração padrão do Spring Boot:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Documento OpenAPI (JSON):** http://localhost:8080/v3/api-docs
- **URL-base das requisições:** `http://localhost:8080`

## Autenticação JWT

O endpoint `POST /auth/login` é público. Envie o e-mail e a senha de um usuário ativo previamente cadastrado:

```json
{
  "email": "admin@exemplo.com",
  "senha": "senha-do-usuario-cadastrado"
}
```

*O endereço e a senha acima são apenas um exemplo de estrutura da requisição; utilize credenciais criadas no seu banco.*

Quando a autenticação é válida, a resposta possui os campos `token`, `tipo` (`Bearer`), `usuarioId` e `perfil`. Para acessar as demais rotas, envie:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

No Swagger, clique em **Authorize** e informe o token conforme solicitado pela interface. No Postman, utilize **Authorization → Bearer Token**. Um token vencido, inválido ou pertencente a usuário inativo não concede acesso. O token expira após **3600 segundos (1 hora)**.

## Endpoints implementados

A lista a seguir foi confrontada com os controllers do código. Rotas não listadas aqui **não devem ser presumidas como implementadas**.

| Método | Rota | Operação |
|---|---|---|
| `POST` | `/auth/login` | Autenticar usuário. |
| `POST` | `/usuarios` | Cadastrar usuário. |
| `GET` | `/usuarios` | Listar usuários. |
| `DELETE` | `/usuarios/{id}` | Excluir usuário. |
| `POST` | `/unidades` | Cadastrar unidade. |
| `GET` | `/unidades` | Listar unidades. |
| `POST` | `/produtos` | Cadastrar produto. |
| `GET` | `/produtos` | Listar todos os produtos. |
| `GET` | `/produtos?unidadeId={id}` | Listar produtos ativos com estoque positivo na unidade ativa. |
| `POST` | `/estoque` | Cadastrar/atualizar estoque do produto na unidade. |
| `GET` | `/estoque` | Listar estoques. |
| `POST` | `/pedidos` | Criar pedido e reservar estoque. |
| `GET` | `/pedidos` | Listar pedidos acessíveis ao usuário autenticado. |
| `GET` | `/pedidos?canalPedido=APP` | Filtrar pedidos por canal. |
| `GET` | `/pedidos/{id}` | Consultar pedido por ID. |
| `PATCH` | `/pedidos/{id}/status` | Avançar status de pedido confirmado. |
| `PATCH` | `/pedidos/{id}/cancelar` | Cancelar pedido aguardando pagamento. |
| `POST` | `/pagamentos` | Processar pagamento simulado. |
| `GET` | `/pagamentos` | Listar pagamentos. |
| `GET` | `/pagamentos/{id}` | Consultar pagamento. |
| `POST` | `/fidelidade` | Aderir ao programa. |
| `GET` | `/fidelidade/{usuarioId}` | Consultar fidelidade de usuário. |
| `PATCH` | `/fidelidade/{usuarioId}/consentimento` | Alterar consentimento. |
| `POST` | `/fidelidade/{usuarioId}/pontos` | Creditar pontos. |
| `POST` | `/fidelidade/{usuarioId}/resgates` | Resgatar pontos. |
| `POST` | `/promocoes` | Cadastrar promoção. |
| `GET` | `/promocoes` | Listar promoções. |
| `GET` | `/promocoes/ativas` | Listar promoções ativas dentro da vigência e vinculadas a produtos ativos. |
| `GET` | `/promocoes/{id}` | Consultar promoção por ID. |
| `PATCH` | `/promocoes/{id}/desativar` | Desativar promoção. |

São **28 operações definidas nos controllers**, além das duas variações de consulta por query string (`unidadeId` e `canalPedido`).

## Perfis e permissões

As regras abaixo são implementadas em `SecurityConfig.java`, com verificações complementares em `UsuarioService`, `PedidoService` e `FidelidadeService`.

| Recurso | Permissões efetivas |
|---|---|
| Login e Swagger | Acesso sem token para `/auth/login` e páginas/documentos Swagger. |
| Usuários: cadastrar, listar e excluir | `ADMIN`, `GERENTE`. O gerente **não pode cadastrar `ADMIN`/`GERENTE` nem excluir usuários com esses perfis**. |
| Unidades: consultar | `ADMIN`, `GERENTE`, `ATENDENTE`, `COZINHA`, `CLIENTE`. |
| Unidades: cadastrar | `ADMIN`, `GERENTE`. |
| Produtos: consultar | `ADMIN`, `GERENTE`, `ATENDENTE`, `CLIENTE`. **`COZINHA` não tem acesso à rota `/produtos`** nesta versão. |
| Produtos: cadastrar | `ADMIN`, `GERENTE`. |
| Estoque: consultar | `ADMIN`, `GERENTE`, `ATENDENTE`. |
| Estoque: cadastrar/atualizar | `ADMIN`, `GERENTE`. |
| Pedidos: criar | `ADMIN`, `GERENTE`, `ATENDENTE`, `CLIENTE`. O cliente só cria pedido em seu próprio `usuarioId`. |
| Pedidos: listar e consultar | Todos os cinco perfis. O cliente só visualiza os próprios pedidos. |
| Pedidos: cancelar | `ADMIN`, `GERENTE`, `ATENDENTE`, `CLIENTE`; cliente só cancela pedido próprio e somente enquanto aguarda pagamento. |
| Pedidos: alterar status | `ADMIN`, `GERENTE`, `ATENDENTE`, `COZINHA`. |
| Pagamentos: criar e consultar | `ADMIN`, `GERENTE`, `ATENDENTE`. |
| Fidelidade: aderir, consultar, alterar consentimento, resgatar | `ADMIN`, `GERENTE`, `ATENDENTE`, `CLIENTE`. O cliente só atua sobre o próprio cadastro. |
| Fidelidade: adicionar pontos | `ADMIN`, `GERENTE`, `ATENDENTE`. |
| Promoções: todas as rotas existentes | `ADMIN`, `GERENTE`. |

Uma requisição sem autenticação válida a uma rota protegida pode receber `401 Unauthorized`; uma requisição autenticada sem permissão pode receber `403 Forbidden`.

## Exemplos reais de formato das requisições

Os JSONs abaixo utilizam **campos existentes no código**. Os valores são **ilustrativos**: IDs devem corresponder a registros previamente cadastrados e o token deve pertencer a usuário autorizado.

### Cadastrar usuário — `POST /usuarios`

```json
{
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "senha": "uma-senha-segura",
  "perfil": "CLIENTE",
  "ativo": true
}
```

O cadastro de usuários não é público: é necessário autenticar como `ADMIN` ou `GERENTE`. O e-mail é verificado sem diferenciar maiúsculas e minúsculas; senhas são gravadas como hash BCrypt.

### Cadastrar unidade — `POST /unidades`

```json
{
  "nome": "Unidade Centro",
  "cidade": "Recife",
  "estado": "PE",
  "ativo": true
}
```

### Cadastrar produto — `POST /produtos`

```json
{
  "nome": "Baião de dois",
  "descricao": "Prato regional",
  "preco": 32.90,
  "ativo": true
}
```

### Cadastrar/atualizar estoque — `POST /estoque`

```json
{
  "produtoId": 1,
  "unidadeId": 1,
  "quantidade": 20
}
```

O par `produtoId` e `unidadeId` identifica um saldo de estoque. A quantidade deve ser **zero ou maior**. Para consultar somente produtos disponíveis, utilize `GET /produtos?unidadeId=1`.

### Criar pedido — `POST /pedidos`

```json
{
  "usuarioId": 1,
  "unidadeId": 1,
  "canalPedido": "APP",
  "itens": [
    { "produtoId": 1, "quantidade": 2 }
  ]
}
```

Canais aceitos: `APP`, `TOTEM`, `BALCAO`, `PICKUP`, `WEB`. O sistema utiliza o preço atual dos produtos, calcula o total, desconta as quantidades do estoque e cria o pedido com status `AGUARDANDO_PAGAMENTO`. Produtos inativos, unidade inativa ou saldo insuficiente impedem a operação.

### Processar pagamento simulado — `POST /pagamentos`

```json
{
  "pedidoId": 1,
  "resultadoSimulado": "APROVADO"
}
```

O valor é obtido do próprio pedido. Os únicos resultados aceitos são `APROVADO` e `RECUSADO`. Não é possível registrar dois pagamentos para o mesmo pedido nem processar um pedido fora de `AGUARDANDO_PAGAMENTO`.

### Atualizar status — `PATCH /pedidos/{id}/status`

```json
{ "status": "EM_PREPARACAO" }
```

As transições aceitas são `CONFIRMADO` → `EM_PREPARACAO` → `PRONTO` → `ENTREGUE`. Não é possível saltar etapas. `PATCH /pedidos/{id}/cancelar` não exige corpo JSON e só funciona enquanto o pedido aguarda pagamento.

### Fidelidade

Adesão — `POST /fidelidade`:

```json
{ "usuarioId": 1, "consentimento": true }
```

Atualizar consentimento — `PATCH /fidelidade/{usuarioId}/consentimento`:

```json
{ "consentimento": false }
```

Crédito e resgate — `POST /fidelidade/{usuarioId}/pontos` ou `POST /fidelidade/{usuarioId}/resgates`:

```json
{ "pontos": 10 }
```

Os pontos devem ser maiores que zero. Operações de crédito/resgate exigem consentimento e, ao revogá-lo (`false`), o serviço zera os pontos registrados. Apenas a equipe autorizada pode creditar pontos; o `CLIENTE` pode resgatar os próprios pontos.

### Criar promoção — `POST /promocoes`

```json
{
  "nome": "Semana Nordestina",
  "produtoId": 1,
  "percentualDesconto": 10.00,
  "dataInicio": "2026-11-01",
  "dataFim": "2026-11-30"
}
```

As datas seguem `AAAA-MM-DD`; o desconto deve ser de `0.01` a `100.00`, e a data final não pode anteceder a inicial. A promoção é criada ativa, desde que o produto exista e esteja ativo. A listagem `/promocoes/ativas` considera vigência, status da promoção e situação do produto.

**Limitação confirmada no código:** as promoções são cadastradas e consultadas, mas `PedidoService` **não aplica automaticamente o desconto ao cálculo de pedidos**. Da mesma forma, a criação de pedidos e pagamentos **não credita pontos de fidelidade automaticamente**; o crédito ocorre pela rota específica.

## Fluxo de pedido e pagamento

1. Um usuário autorizado cadastra a unidade e os produtos; a equipe habilitada informa o estoque por unidade.
2. Um perfil permitido cria um pedido com `usuarioId`, `unidadeId`, `canalPedido` e lista de itens.
3. A aplicação valida usuário, unidade, produtos e saldo; agrega quantidades repetidas do mesmo produto, calcula o preço dos itens e **reserva estoque no momento da criação**.
4. O pedido passa a `AGUARDANDO_PAGAMENTO`.
5. A equipe autorizada envia o resultado simulado: `APROVADO` altera o pedido para `CONFIRMADO`; `RECUSADO` altera para `PAGAMENTO_RECUSADO` e **devolve as quantidades ao estoque**.
6. Um pedido ainda aguardando pagamento pode ser cancelado (`CANCELADO`), com devolução do estoque.
7. Após confirmação, a equipe autorizada atualiza sucessivamente para `EM_PREPARACAO`, `PRONTO` e `ENTREGUE`.

As alterações de pedido e pagamento relevantes estão marcadas como transações nos serviços. A implementação **não possui gateway de pagamento externo real**; o resultado é informado no campo `resultadoSimulado`.

## Respostas HTTP e validações

- `400 Bad Request`: valores inválidos, campos obrigatórios ausentes, canal/status incorreto ou outros dados rejeitados por validação.
- `401 Unauthorized`: falha de autenticação ou ausência de credenciais válidas em rota protegida.
- `403 Forbidden`: usuário autenticado sem permissão, inclusive cliente tentando acessar dados de outro cliente.
- `404 Not Found`: recurso solicitado não encontrado em operações que validam a existência.
- `409 Conflict`: conflito de negócio, como e-mail repetido, estoque insuficiente, pagamento duplicado ou transição de status inválida.
- `201 Created`: explicitamente utilizado na criação de fidelidade e de promoção.

O `GlobalExceptionHandler` padroniza diversos erros com os campos `status`, `mensagem` e `erros`. Nem todos os endpoints possuem anotação explícita de código de sucesso, e o status final depende da operação e da configuração do Spring.

## Verificação e evidências

**O que há no ZIP analisado:** um teste automatizado `RaizesDoNordesteBackendApplicationTests.contextLoads()`, que verifica o carregamento do contexto do Spring. Não há, no ZIP, suíte automatizada de cenários de negócio, coleção exportada do Postman, capturas de evidência, scripts SQL ou arquivos de diagramas. Esses materiais não devem ser apresentados como existentes até serem adicionados.

Para executar os testes automatizados disponíveis, com MySQL e variáveis de ambiente configurados:

```powershell
.\mvnw.cmd test
```

O comando acima **não foi executado nesta revisão documental**. O carregamento do contexto depende das configurações do ambiente utilizado.

Para verificar manualmente a API pelo Swagger/Postman, use um banco preparado e identidades autorizadas. Cenários reproduzíveis de conferência incluem:

| Cenário | Requisição | Resultado esperado pelas regras do código |
|---|---|---|
| Login válido | `POST /auth/login` com usuário ativo | Token e perfil retornados. |
| Listagem de unidades | `GET /unidades` com token permitido | Lista de unidades. |
| Cardápio por unidade | `GET /produtos?unidadeId=1`, com registros existentes | Apenas produtos ativos com estoque positivo. |
| Registro de pedido válido | `POST /pedidos` com saldo suficiente | Pedido aguardando pagamento e estoque reservado. |
| Pagamento aprovado | `POST /pagamentos` com `APROVADO` | Pedido confirmado. |
| Cancelamento antes do pagamento | `PATCH /pedidos/{id}/cancelar` | Pedido cancelado e estoque devolvido. |
| Login com senha incorreta | `POST /auth/login` | `401`. |
| Pedido com estoque insuficiente | `POST /pedidos` | `409`. |
| Cliente acessando pedido alheio | `GET /pedidos/{id}` | `403`. |
| Reprocessamento do mesmo pagamento | `POST /pagamentos` | `409`. |

A tabela descreve **resultados esperados pela implementação, não resultados de uma execução realizada nesta análise**. As evidências da entrega acadêmica devem ser coletadas com as requisições realmente executadas, e a coleção do Postman deve ser exportada separadamente quando estiver pronta.

## Limites e cuidados para publicação

- A implementação é uma **API de back-end**; não inclui front-end, aplicativo ou totem executável.
- Pagamentos são simulações internas, não transações com banco ou adquirente.
- Cadastro e consulta de promoções existem, mas o preço promocional não é aplicado automaticamente aos pedidos.
- Crédito de fidelidade não é automático após a compra.
- Não há controle de versionamento do schema por Flyway/Liquibase nesta versão; o ambiente local utiliza Hibernate `ddl-auto=update`.
- Os diagramas do projeto e a coleção Postman **não estão incluídos no ZIP analisado**; devem ser versionados separadamente se fizerem parte da entrega final.
- A configuração Swagger/OpenAPI está acessível sem autenticação; em implantação de produção, revise sua exposição, as políticas de segurança, os logs, a gestão de segredos e as permissões do banco.
- Antes de compartilhar arquivos compactados, exclua pastas locais como `.idea/`, `.git/` e `target/`. Em especial, configurações de execução da IDE podem armazenar variáveis privadas. Para publicação do código, prefira o Git com o `.gitignore` corretamente configurado.

## Transparência na elaboração da documentação

O README foi organizado e revisado com auxílio do **ChatGPT (OpenAI)**, a partir do código-fonte enviado pelo responsável pelo projeto. A solicitação foi verificar a versão atualizada do ZIP e produzir documentação fiel às classes e configurações existentes. Esta revisão foi **estática**: não substitui a execução dos testes e a conferência final no ambiente de entrega.

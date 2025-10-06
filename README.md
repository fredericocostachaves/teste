# Teste — Sistema de Prescrição (JSF/PrimeFaces + JPA/Hibernate)

Aplicação web (WAR) de exemplo com Java EE 8 que gerencia Pacientes, Medicamentos e Receitas, incluindo:
- CRUD de Pacientes e Medicamentos com paginação lazy e filtros (PrimeFaces DataTable)
- Cadastro de Receita permitindo associar um ou mais medicamentos
- Consulta “Medicamentos por Paciente” com filtros por paciente e medicamento, total por receita e paginação lazy
- Relatório “Medicamentos Prescritos” com:
  - Top 2 medicamentos mais prescritos
  - Top 2 pacientes com mais medicamentos prescritos
  - Tabela com total de medicamentos por paciente

## Tecnologias
- Java 8
- Java EE 8 (JSF, EJB, JPA)
- PrimeFaces 8
- Hibernate 5.4 (provider JPA)
- PostgreSQL
- Maven (empacotamento WAR)
- Servidor de aplicação compatível com Java EE 8 (ex.: WildFly)

## Requisitos
- JDK 8+ (JAVA_HOME configurado)
- Maven 3.6+
- PostgreSQL (recomendado 12+)
- Servidor Java EE 8 (WildFly 18+ ou compatível) com DataSource JTA configurado com JNDI `java:/PostgresDS`

## Configuração do Banco de Dados
1. Crie um banco (schema) no PostgreSQL.
2. Configure um DataSource no servidor de aplicação (WildFly, por exemplo) com:
   - JNDI: `java:/PostgresDS`
   - Driver: PostgreSQL
   - URL/Usuário/Senha conforme seu ambiente
3. Opcional, mas recomendado: popular dados de exemplo com o dump que acompanha o projeto.

Populando com o dump (via psql):
```bash
psql -h <host> -U <usuario> -d <database> -f db-dump-random-postgres.sql
```
Observações:
- O script executa DROP/CREATE das tabelas, insere dados determinísticos e ajusta as sequências.
- Compatível com o mapeamento JPA deste projeto (constraints e FKs nomeadas).

## Configuração de Persistência
O `persistence.xml` usa:
- Unidade: `testePU`
- `jta-data-source`: `java:/PostgresDS`
- `hibernate.hbm2ddl.auto=update` (apenas para desenvolvimento)

Se preferir não usar o dump, o Hibernate criará/atualizará o esquema automaticamente ao subir a aplicação (em dev). Para um ambiente controlado, use o dump fornecido.

## Build e Deploy
1. Build do WAR:
```bash
mvn clean package
```
Gera `target/teste.war`.

2. Deploy no servidor de aplicação (ex.: WildFly):
- Copie `target/teste.war` para `WILDFLY_HOME/standalone/deployments/`
- ou use a Console de Administração/CLI para fazer o deploy.

3. Acesso:
- URL inicial: `http://localhost:8080/teste/index.xhtml`

## Navegação e Telas
- Menu principal: `index.xhtml`
- Pacientes (CRUD, paginação lazy, filtro por nome e CPF): `/paciente/lista.xhtml`
- Medicamentos (CRUD, paginação lazy, filtro por nome): `/medicamento/lista.xhtml`
- Receita — associação de medicamentos a um paciente:
  - `/receita/cadastro.xhtml`
  - Fluxo: selecionar paciente → “Criar Receita” → adicionar/remover medicamentos
- Consulta — Medicamentos por Paciente:
  - `/consulta/medicamentos-por-paciente.xhtml`
  - Filtros por nome do paciente e do medicamento
  - Coluna “Total de Medicamentos” abre um diálogo com os itens da receita
- Relatório — Medicamentos Prescritos:
  - `/relatorio/medicamentos-prescritos.xhtml`
  - Exibe top 2 medicamentos, top 2 pacientes e totais por paciente

## Estrutura de Pastas (resumo)
- `src/main/java/br/com/teste/model` — Entidades JPA (Paciente, Medicamento, Receita, MedicamentoReceitado)
- `src/main/java/br/com/teste/repository` — EJBs Stateless (JPA/consultas)
- `src/main/java/br/com/teste/bean` — Backing beans (JSF/PrimeFaces)
- `src/main/java/br/com/teste/datamodel` — LazyDataModels para DataTables
- `src/main/java/br/com/teste/dto` — DTOs para projeções/relatórios
- `src/main/resources/META-INF/persistence.xml` — Configuração JPA
- `src/main/webapp` — Páginas JSF (XHTML)
- `db-dump-random-postgres.sql` — Dump de banco (DDL + dados de exemplo)

## Dicas e Solução de Problemas
- Datasource não encontrado (JNDI): verifique o nome exato `java:/PostgresDS` e o driver PostgreSQL instalado no servidor.
- Ícones PrimeFaces (pi pi-*) não aparecem: garanta a inclusão do CSS `primeicons/primeicons.css` (já configurado nas páginas deste projeto).
- Componentes JSF não atualizam após ação: nos pontos críticos o projeto utiliza `PrimeFaces.current().ajax().update(...)` com os clientIds corretos e, quando necessário, atualiza o formulário inteiro para componentes renderizados condicionalmente.
- Erro de CPF duplicado ao salvar Paciente: existe validação de unicidade via constraint `uk_paciente_cpf`. A mensagem amigável é exibida no diálogo.

## Licença
Projeto de exemplo para fins de teste.

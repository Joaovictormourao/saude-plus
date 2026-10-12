# Saúde+ — Sistema de Agendamento de Consultas

Sistema web de gerenciamento e agendamento de consultas médicas, desenvolvido como Trabalho de Conclusão de Curso (TCC) do curso técnico em Desenvolvimento de Sistemas na ETSM.

Pacientes se cadastram, escolhem especialidade e médico e agendam consultas. Médicos acompanham sua agenda e enviam relatórios. Administradores gerenciam pacientes, especialidades, consultas e mensagens de contato.

## Tecnologias

- **Backend:** Java 17, Jakarta Servlets 6, JDBC, Gson
- **Banco de dados:** MySQL 8
- **Build e execução:** Maven (WAR) + Tomcat 10 (via plugin Cargo)
- **Frontend:** HTML, CSS e JavaScript puro (fetch API)

## Funcionalidades

- Três perfis de acesso: paciente, médico e administrador
- Cadastro e login de pacientes, com redefinição de senha
- Agendamento, consulta e cancelamento de consultas
- Painel do médico com agenda e envio de relatórios aos pacientes
- Painel administrativo: pacientes, consultas, especialidades e mensagens de contato
- Formulário de contato

## Arquitetura

```
src/main/java/com/clinica/
├── model/     entidades (Paciente, Medico, Agendamento...)
├── dao/       acesso ao banco (JDBC com PreparedStatement)
├── servlet/   API REST em JSON
└── util/      conexão com o banco e respostas HTTP
src/main/webapp/   páginas HTML, css/ e js/
database/          script SQL (schema e dados de teste)
```

## Como executar

Requisitos: JDK 17+, Maven 3.8+ e MySQL 8+.

1. Crie o banco e os dados de teste:

   ```bash
   mysql -u root -p < database/clinica_saude_plus.sql
   ```

2. Se o seu MySQL tiver senha, informe por variável de ambiente (nada de senha no código):

   ```bash
   # Linux/Mac
   export DB_PASSWORD="sua_senha"
   # Windows PowerShell
   $env:DB_PASSWORD="sua_senha"
   ```

   Também são aceitos `DB_URL` e `DB_USER`. Os padrões são `jdbc:mysql://localhost:3306/clinica_saude_plus` e `root`.

3. Compile e suba o Tomcat:

   ```bash
   mvn clean install
   mvn cargo:run
   ```

   Os scripts `run.sh` (Linux/Mac) e `run.bat` (Windows) fazem esses passos.

4. Acesse **http://localhost:8080/clinica/index.html**

Para gerar o WAR: `mvn clean package` (saída em `target/clinica-saude-plus.war`).

## Contas de teste

Todos os dados abaixo são fictícios e existem só no script de teste.

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | admin@clinicasaudeplus.com.br | admin123 |
| Médico | joao@clinica.com.br | senha123 |
| Paciente | paciente@exemplo.com | 123456789 |

## API

Base: `/clinica/api`. As rotas aceitam singular ou plural (`/paciente` e `/pacientes`).

| Recurso | Rotas |
|---|---|
| Pacientes | `GET /pacientes`, `GET /pacientes?id=`, `POST /pacientes/cadastrar`, `POST /pacientes/login`, `POST /pacientes/redefinir-senha`, `PUT /pacientes`, `DELETE /pacientes?id=` |
| Médicos | `GET /medicos`, `GET /medicos?especialidade=`, `GET /medicos?id=`, `POST /medicos/login` |
| Agendamentos | `GET /agendamentos` (filtros `paciente`, `medico`, `id`), `POST /agendamentos`, `PUT /agendamentos/status`, `DELETE /agendamentos?id=` |
| Especialidades | `GET /especialidades`, `POST`, `PUT`, `DELETE ?id=` |
| Mensagens | `/mensagens/contato` e `/mensagens/relatorio` (GET, POST, PUT) |
| Admin | `POST /admin/login` |

## Limitações conhecidas e melhorias futuras

Este é um projeto acadêmico e **não está pronto para produção**. Pontos que eu faria diferente hoje:

- **Autenticação:** o login guarda os dados do usuário no `localStorage` e a API não valida sessão nem token. O próximo passo é usar sessão no servidor ou JWT, com checagem de perfil em cada rota.
- **Senhas:** o hash é SHA-256 sem salt (`SHA2` do MySQL). O ideal é BCrypt ou Argon2.
- **Redefinição de senha:** funciona apenas com o e-mail, sem token enviado por e-mail.
- **CORS:** está liberado para qualquer origem (`*`). Deve ser restrito ao domínio da aplicação.
- **Qualidade:** faltam testes automatizados e validação mais rigorosa de CPF e dados de entrada no servidor.
- **Interface:** adicionar capturas de tela e tornar o layout totalmente responsivo.

## Autor

**João Victor Mourão** — estudante de Segurança da Informação (UCB) e técnico em Desenvolvimento de Sistemas juntamente com demais colegas de equipe Rodrigo Alves e João Gabriel Feitosa.

Projeto originado como trabalho em grupo na ETSM.

## Licença

[MIT](LICENSE)

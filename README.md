README - Literalura API

📚 Descrição do Projeto

Literalura é uma aplicação Java que permite buscar e gerenciar informações sobre livros e autores, integrando com a API Gutendex (https://gutendex.com/). O sistema oferece funcionalidades para:

Buscar livros por título

Listar livros e autores registrados

Filtrar autores por período histórico

Filtrar livros por idioma

Extra, buscar livros por autor (tanto na API quanto no banco local)

🛠️ Pré-requisitos
Antes de executar o projeto, você precisará ter instalado:

Java JDK 17 ou superior

PostgreSQL (versão 15 ou superior)

IntelliJ IDEA (recomendado) ou outra IDE Java

Maven (para gerenciamento de dependências)

⚙️ Configuração do Banco de Dados

Instale o PostgreSQL a partir do site oficial:

https://www.postgresql.org/download/

Execute o pacote instalado pgAdim 4 e/ou pode ser necessario criar um banco de dados chamado "literalura" no PostgreSQL, conforme imagem abaixo:

<img width="1003" height="749" alt="image" src="https://github.com/user-attachments/assets/dd3b8d50-a9eb-4053-b4ef-d980bb3dc6a3" />


Configure o arquivo application.properties no IntelliJ:

Localização: src/main/resources/application.properties

Conteúdo a ser atualizado:

properties
spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
spring.datasource.username=seu_usuario_postgres
spring.datasource.password=sua_senha_postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

Substitua seu_usuario_postgres e sua_senha_postgres.

Pelas suas credenciais do PostgreSQL.

🚀 Como Executar o Projeto

Clone o repositório:


git clone https://github.com/seu-usuario/literalura.git
Abra o projeto no IntelliJ IDEA

Configure o JDK:

File → Project Structure → Project SDK (selecione JDK 17+)

Atualize as dependências do Maven:

Clique com o botão direito no arquivo pom.xml → Maven → Reload Project

Execute a aplicação:

Localize a classe principal LiteraluraApplication

Clique com o botão direito → Run 'LiteraluraApplication'

Acesse o menu interativo no console

🎯 Funcionalidades

O sistema oferece um menu interativo com as seguintes opções:

Buscar livro pelo título: Busca na API e salva no banco de dados

Listar livros registrados: Mostra todos os livros armazenados localmente

Listar autores registrados: Mostra todos os autores armazenados localmente

Listar autores vivos em determinado ano: Filtra autores por ano específico

Listar livros em determinado idioma: Filtra por idioma (en, es, fr, pt)

Extra, buscar livros por autor (API): Busca livros na API Gutendex pelo nome do autor

Extra, buscar livros por autor (Banco de Dados): Busca livros no banco local pelo autor

Conforme imagem abaixo:

<img width="466" height="364" alt="image" src="https://github.com/user-attachments/assets/380ceff9-45e2-4f72-b244-6729fea3fb5a" />


🛡️ Tratamento de Erros

A aplicação inclui tratamento de erros para:

Conexão com a API

Buscas sem resultados

Validação de entradas do usuário

Conexão com o banco de dados

📦 Estrutura do Projeto


literalura/
├── src/
│   ├── main/
│   │   ├── java/br/com/alura/literalura/
│   │   │   ├── model/          # Classes de modelo
│   │   │   ├── repository/     # Interfaces de repositório
│   │   │   ├── service/        # Classes de serviço
│   │   │   ├── principal/      # Classe principal com o menu
│   │   │   └── LiteraluraApplication.java  # Classe de inicialização
│   │   └── resources/
│   │       └── application.properties  # Configurações do banco
│   └── test/                   # Testes (se houver)
├── pom.xml                     # Configuração do Maven
└── README.md                   # Este arquivo

✉️ Contato
Para dúvidas ou sugestões, entre em contato com [rodrigodjlima@hotmail.com]

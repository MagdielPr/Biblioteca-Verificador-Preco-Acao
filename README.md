# Biblioteca Java para Consulta de Preços de Ações em Bolsa de Valores

## Descrição

Este projeto apresenta o desenvolvimento de uma biblioteca Java para consulta de preços de ações em tempo real. Utilizando uma arquitetura distribuída cliente-servidor com sockets e threads, a biblioteca oferece uma solução eficiente e escalável para a obtenção e armazenamento de dados financeiros.

A biblioteca é composta por três componentes principais:

- **Servidor**: Responsável por fornecer dados atualizados e históricos de preços de ações.
- **Cliente**: Realiza requisições ao servidor para obter informações sobre ações.
- **Database**: Armazena dados históricos em um banco de dados MySQL.

A integração com uma API externa permite a obtenção de cotações em tempo real, enquanto um cache local otimiza o desempenho e reduz a carga na API externa.

## Funcionalidades

- Consulta de preços de ações em tempo real.
- Armazenamento e recuperação de dados históricos com MySQL.
- Implementação de cache local para otimização de desempenho.
- Arquitetura distribuída utilizando sockets e threads.
- Interface simplificada para integração com outros projetos.

## Requisitos

- **Java JDK 11** ou superior.
- **MySQL**: Banco de dados para armazenamento histórico.
- **Gson**: Biblioteca para conversão de objetos Java para JSON e vice-versa.
- **MySQL Connector**: Biblioteca para conexão com o banco de dados MySQL.

## Instalação

1. **Clonar o repositório:**

   ```bash
   git clone https://github.com/seu-usuario/nome-do-repositorio.git
   ```

2. **Configurar o banco de dados MySQL:**

   Crie um banco de dados MySQL com o nome especificado em sua configuração e configure as credenciais no arquivo de configuração da biblioteca.

3. **Adicionar dependências:**

   Certifique-se de adicionar as bibliotecas Gson e MySQL Connector ao seu projeto. Você pode fazer isso adicionando os arquivos JAR ao seu classpath ou configurando um gerenciador de dependências como Maven ou Gradle.

   - [Gson](https://mvnrepository.com/artifact/com.google.code.gson/gson)
   - [MySQL Connector](https://mvnrepository.com/artifact/mysql/mysql-connector-java)

4. **Construir o projeto:**

   Compile o projeto utilizando seu IDE ou uma ferramenta de build como Maven ou Gradle.

## Uso

1. **Inicializar a biblioteca:**

   Configure a biblioteca com as informações da API e do banco de dados. Exemplo de configuração:

   ```java
   String apiBaseUrl = "https://brapi.dev/api/quote/";
   String apiToken = "seu_token_api";
   String dbHost = "localhost";
   int dbPort = 3306;
   String dbName = "nome_do_banco";
   String dbUser = "usuario";
   String dbPassword = "senha";
   int serverPort = 4000;
   String serverAddress = "127.0.0.1";
   int dbServicePort = 5000;

   Facade facade = new Facade(apiBaseUrl, apiToken, dbHost, dbPort, dbName, dbUser, dbPassword, serverPort, serverAddress, dbServicePort);
   facade.inicializar();
   ```

2. **Consultar preços de ações:**

   ```java
   API_Client apiClient = facade.getApiClient();
   String ticker = "AAPL"; // Exemplo de ticker
   String preco = apiClient.obterPrecoAcao(ticker);
   System.out.println("Preço da ação " + ticker + ": " + preco);
   ```

## Testes

Os testes incluem:

- **Testes de API**: Verifica a obtenção de preços de ações.
- **Testes de Conexão com o Banco de Dados**: Verifica a conexão com o MySQL.
- **Testes de Cache**: Verifica a operação do cache local.
- **Testes de Servidor e Cliente**: Verifica se o servidor aceita conexões e se o cliente consegue se conectar.

Execute os testes utilizando o framework JUnit.

## Diagrama do Projeto

![Diagrama do Sistema](caminho/para/o/diagrama.png)

O diagrama acima ilustra a arquitetura do sistema, mostrando a interação entre os componentes Cliente, Servidor e Banco de Dados, bem como a API externa de cotações.

## Considerações Finais

A biblioteca desenvolvida oferece uma solução robusta e escalável para a consulta e análise de preços de ações. Sua arquitetura modular e uso de tecnologias modernas garantem desempenho e flexibilidade.

## Referências

- **Butzke, C. G., Koepsel, J. W. Z., & Curvello, R.** Desenvolvimento de um sistema para monitoramento do preço de ações utilizando threads e socket. Instituto Federal Catarinense - Campus Rio do Sul, 18 jun. 2024.
- **Deitel, P., & Deitel, H.** Java: Como Programar. 8ª ed. Pearson Prentice Hall, 2010.
- **Richardson, L., & Ruby, S.** RESTful Web Services. O'Reilly Media, 2007.
- **Kurose, J. F., & Ross, K. W.** Redes de Computadores e a Internet: Uma Abordagem Top-Down. 6ª ed. Pearson, 2013.

## Contato

- **Magdiel Prestes Rodrigues**: magdielprestes@gmail.com
- **Rodrigo Curvello**: rodrigo.curvello@ifc.edu.br

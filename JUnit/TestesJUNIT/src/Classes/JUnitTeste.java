package Classes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import FACADE.Facade;
import API.API_Client;
import Database.ConnectionDatabase;
import Database.Database;
import SOCKET.Cache;
import SOCKET.Client;
import SOCKET.Server;

import java.io.IOException;
import java.net.Socket;
import java.lang.reflect.Field;
import java.sql.Connection;

public class JUnitTeste {

    private Facade facade;
    private API_Client apiClient;
    private ConnectionDatabase connectionDatabase;
    private Database database;
    private Cache cache;
    private Server server;
    private Client client;
    private int serverPort = 4000; 

    @BeforeEach
    public void setUp() throws IOException {
        System.out.println("Iniciando setup do teste");
        String apiBaseUrl = "https://brapi.dev/api/quote/";
        String apiToken = "vKDuZ2fBuAMAs8c37AiHdZ";
        String dbHost = "localhost";
        int dbPort = 3306;
        String dbName = "databasesocket";
        String dbUser = "root";
        String dbPassword = "1234";
        int dbServicePort = 5000;

        facade = new Facade(apiBaseUrl, apiToken, dbHost, dbPort, dbName, dbUser, dbPassword,
                            serverPort, "127.0.0.1", dbServicePort);
        facade.inicializar();
        
        apiClient = facade.getApiClient();
        connectionDatabase = facade.getConnectionDatabase();
        database = facade.getDatabase();
        cache = facade.getCache();
        server = facade.getServer();
        client = facade.getClient();
        
        System.out.println("Setup concluído");
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (client != null) client.fechar();
        if (server != null) server.parar();
    }

    @Test
    public void testAPIClientObterPrecoAcao() {
        String ticker = "AAPL"; // Exemplo de ticker
        String resultado = apiClient.obterPrecoAcao(ticker);
        assertNotNull(resultado, "O resultado da API não deve ser nulo");
    }

    @Test
    public void testConnectionDatabaseConectar() {
        Connection conexao = connectionDatabase.conectar();
        assertNotNull(conexao, "A conexão com o banco de dados não deve ser nula");
        connectionDatabase.fecharConexao(conexao);
    }

    @Test
    public void testCacheOperations() {
        String ticker = "GOOGL";
        String preco = "2500.00";
        cache.atualizarCache(ticker, preco);
        assertEquals(preco, cache.obterPreco(ticker), "O preço obtido deve ser igual ao preço armazenado");
    }

    @Test
    public void testServerStart() throws IOException {
        // Inicia o servidor em um thread separado
        Thread serverThread = new Thread(() -> {
            try {
                server.iniciar();
            } catch (IOException e) {
                System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
            }
        });
        serverThread.start();

        // Aguarda um pouco para garantir que o servidor esteja totalmente inicializado
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Tenta criar um socket para verificar se o servidor está aceitando conexões
        try (Socket socket = new Socket("localhost", serverPort)) {
            // Se a conexão for bem-sucedida, o servidor está rodando
            assertTrue(socket.isConnected(), "O servidor deve estar em execução");
        } catch (IOException e) {
            // Se não for possível conectar, o servidor não está em execução
            fail("O servidor não conseguiu aceitar conexões: " + e.getMessage());
        } finally {
            // Para o servidor após o teste
            server.parar();
            try {
                serverThread.join(); // Aguarda o servidor terminar
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    public void testClientConnection() throws IOException {
        // Inicia o servidor em um thread separado
        Thread serverThread = new Thread(() -> {
            try {
                server.iniciar();
            } catch (IOException e) {
                System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
            }
        });
        serverThread.start();

        // Aguarda um pouco para garantir que o servidor esteja totalmente inicializado
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Inicia o cliente
        client.iniciar();

        // Verifica se o cliente está conectado ao servidor
        try {
            // Acessa o socket do cliente usando reflexão
            Field clientSocketField = Client.class.getDeclaredField("clientSocket");
            clientSocketField.setAccessible(true);
            Socket clienteSocket = (Socket) clientSocketField.get(client);

            // Verifica se o socket está conectado
            assertTrue(clienteSocket != null && !clienteSocket.isClosed() && clienteSocket.isConnected(),
                       "O cliente deve estar conectado ao servidor");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Não foi possível acessar o socket do cliente: " + e.getMessage());
        } finally {
            // Fecha o cliente e o servidor após o teste
            client.fechar();
            server.parar();
            try {
                serverThread.join(); // Aguarda o servidor terminar
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

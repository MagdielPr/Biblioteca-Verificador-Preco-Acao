package FACADE;

import API.API_Client;
import Database.ConnectionDatabase;
import Database.Database;
import SOCKET.Cache;
import SOCKET.Client;
import SOCKET.Server;
import java.sql.Connection;

/**
 * A classe Facade fornece uma interface simplificada para inicializar e acessar
 * diferentes componentes do sistema, como API, banco de dados, cache, servidor e cliente.
 */
public class Facade {
    private API_Client apiClient;
    private ConnectionDatabase connectionDatabase;
    private Database database;
    private Cache cache;
    private Server server;
    private Client client;

    private String apiBaseUrl;
    private String apiToken;
    private String dbHost;
    private int dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private int serverPort;
    private String serverAddress;
    private int dbServicePort;

    /**
     * Construtor da classe Facade.
     *
     * @param apiBaseUrl    A URL base da API.
     * @param apiToken      O token de autenticação para a API.
     * @param dbHost        O endereço do host do banco de dados.
     * @param dbPort        A porta de conexão do banco de dados.
     * @param dbName        O nome do banco de dados.
     * @param dbUser        O nome de usuário para o banco de dados.
     * @param dbPassword    A senha para o banco de dados.
     * @param serverPort    A porta onde o servidor escutará.
     * @param serverAddress O endereço do servidor.
     * @param dbServicePort A porta de serviço do banco de dados.
     */
    public Facade(String apiBaseUrl, String apiToken, String dbHost, int dbPort, String dbName, 
                  String dbUser, String dbPassword, int serverPort, String serverAddress, int dbServicePort) {
        this.apiBaseUrl = apiBaseUrl;
        this.apiToken = apiToken;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.dbServicePort = dbServicePort;
    }

    /**
     * Inicializa os componentes do sistema, como cliente da API, conexão com o banco de dados,
     * cache, servidor e cliente de socket.
     */
    public void inicializar() {
        apiClient = new API_Client(apiBaseUrl, apiToken);
        connectionDatabase = new ConnectionDatabase(dbHost, dbPort, dbName, dbUser, dbPassword);
        Connection dbConnection = connectionDatabase.conectar();
        database = new Database(dbConnection, dbServicePort);
        cache = new Cache();
        server = new Server(cache, serverPort, dbHost, dbServicePort, apiBaseUrl, apiToken);
        client = new Client(serverAddress, serverPort);
    }

    /**
     * Retorna o cliente da API.
     *
     * @return O cliente da API.
     */
    public API_Client getApiClient() {
        return apiClient;
    }

    /**
     * Retorna a conexão com o banco de dados.
     *
     * @return A conexão com o banco de dados.
     */
    public ConnectionDatabase getConnectionDatabase() {
        return connectionDatabase;
    }

    /**
     * Retorna o banco de dados.
     *
     * @return O banco de dados.
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Retorna o cache.
     *
     * @return O cache.
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Retorna o servidor.
     *
     * @return O servidor.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Retorna o cliente de socket.
     *
     * @return O cliente de socket.
     */
    public Client getClient() {
        return client;
    }
}

package FACADE;

import java.io.IOException;

public class TestFacade {
    // Configurações
    private static final String API_BASE_URL = "https://brapi.dev/api/quote/";
    private static final String API_TOKEN = "vKDuZ2fBuAMAs8c37AiHdZ";
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "databasesocket";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";
    private static final int SERVER_PORT = 4000;
    private static final String SERVER_ADDRESS = "192.168.1.4";
    private static final int DB_SERVICE_PORT = 5000;

    public static void main(String[] args) {
        Facade facade = new Facade(
            API_BASE_URL, API_TOKEN, DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD,
            SERVER_PORT, SERVER_ADDRESS, DB_SERVICE_PORT
        );
        
        System.out.println("Inicializando o sistema...");
        facade.inicializar();
        
        System.out.println("Iniciando os componentes...");
        new Thread(facade.getDatabase()::iniciar).start();
        new Thread(() -> {
            try {
                facade.getServer().iniciar();
            } catch (IOException e) {
                System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
            }
        }).start();
        
        try {
            facade.getClient().iniciar();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o cliente: " + e.getMessage());
        }
        
        // Aguarde um pouco antes de parar o sistema
        try {
            Thread.sleep(5000); // Espera 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Parando os componentes...");
        facade.getServer().parar();
        facade.getClient().fechar();
        facade.getDatabase().fecharConexaoBanco();
        facade.getConnectionDatabase().fecharConexao(null);
        
        System.out.println("Sistema encerrado.");
    }
}

package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados.
 */
public class ConnectionDatabase {
    private String host;
    private int porta;
    private String nomeBanco;
    private String usuario;
    private String senha;

    /**
     * Construtor da classe ConnectionDatabase.
     * 
     * @param host      O endereço do host onde o banco de dados está localizado.
     * @param porta     A porta usada para conectar ao banco de dados.
     * @param nomeBanco O nome do banco de dados.
     * @param usuario   O nome de usuário para autenticação.
     * @param senha     A senha para autenticação.
     */
    public ConnectionDatabase(String host, int porta, String nomeBanco, String usuario, String senha) {
        this.host = host;
        this.porta = porta;
        this.nomeBanco = nomeBanco;
        this.usuario = usuario;
        this.senha = senha;
    }

    /**
     * Estabelece uma conexão com o banco de dados.
     * 
     * @return Um objeto {@link Connection} representando a conexão com o banco de dados,
     *         ou {@code null} em caso de falha.
     */
    public Connection conectar() {
        Connection conexao = null;
        try {
            String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC", 
                host, porta, nomeBanco);
            conexao = DriverManager.getConnection(url, usuario, senha);
            System.out.println("Conectado ao banco de dados: " + nomeBanco + " com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
        return conexao;
    }

    /**
     * Fecha a conexão com o banco de dados.
     * 
     * @param conexao A conexão a ser fechada. Se a conexão for {@code null}, o método
     *                não executará nenhuma ação.
     */
    public void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
                System.out.println("Conexão fechada com sucesso!");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }
}

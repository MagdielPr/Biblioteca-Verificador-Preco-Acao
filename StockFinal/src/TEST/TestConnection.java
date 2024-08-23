package TEST;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * A classe TestConnection é responsável por criar uma conexão com um banco de dados.
 */
public class TestConnection {

    /**
     * Cria uma conexão com o banco de dados usando as credenciais fornecidas.
     *
     * @param dbUrl A URL de conexão com o banco de dados. Por exemplo: "jdbc:mysql://localhost:3306/meuBanco".
     * @param user O nome de usuário para autenticação no banco de dados.
     * @param password A senha do usuário para autenticação no banco de dados.
     * @return A conexão com o banco de dados, ou null se a conexão falhar.
     */
    public Connection createConnection(String dbUrl, String user, String password) {
        Connection con = null;
        try {
            con = DriverManager.getConnection(dbUrl, user, password);
            if (con != null) {
                System.out.println("Conexão com o banco de dados estabelecida com sucesso.");
                return con;
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        return null;
    }
}

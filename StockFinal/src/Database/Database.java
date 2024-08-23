package Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados e processar requisições de clientes.
 */
public class Database {
    private int portaBanco;
    private ServerSocket serverSocket;
    private Connection conexaoBD;
    private volatile boolean executando = true;

    /**
     * Construtor da classe Database.
     * 
     * @param conexaoBD A conexão com o banco de dados.
     * @param portaBanco A porta na qual o servidor de banco de dados escutará.
     */
    public Database(Connection conexaoBD, int portaBanco) {
        this.conexaoBD = conexaoBD;
        this.portaBanco = portaBanco;
    }

    /**
     * Inicia o serviço de banco de dados.
     * Aguarda por conexões de clientes e processa suas requisições.
     */
    public void iniciar() {
        try {
            System.out.println("Serviço de banco de dados iniciando na porta: " + portaBanco);
            serverSocket = new ServerSocket(portaBanco);

            while (executando) {
                Socket socketCliente = serverSocket.accept();
                processarRequisicao(socketCliente);
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o serviço de banco de dados: " + e.getMessage());
        } finally {
            fecharConexaoBanco();
        }
    }

    /**
     * Processa uma requisição de um cliente.
     *
     * @param socketCliente O socket do cliente conectado.
     */
    public void processarRequisicao(Socket socketCliente) {
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            PrintWriter saida = new PrintWriter(socketCliente.getOutputStream(), true);
        ) {
            String linhaEntrada;
            while ((linhaEntrada = entrada.readLine()) != null) {
                String[] partes = linhaEntrada.split(" ");
                String comando = partes[0];
                String ticker = partes[1];
                String preco = partes.length > 2 ? partes[2] : null;

                if ("SALVAR".equalsIgnoreCase(comando)) {
                    salvarNoBanco(ticker, preco);
                    saida.println("PRECO_SALVO");
                } else if ("BUSCAR".equalsIgnoreCase(comando)) {
                    String[] dados = buscarNoBanco(ticker);
                    if (dados != null) {
                        saida.println(String.join(", ", dados));
                    } else {
                        saida.println("DADOS_NAO_ENCONTRADOS");
                    }
                } else if ("HISTORICO".equalsIgnoreCase(comando)) {
                    String historico = buscarHistorico(ticker);
                    String[] linhas = historico.split("\n");
                    for (String linha : linhas) {
                        saida.println(linha);
                    }
                    saida.println("FIM_HISTORICO");
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao comunicar com o cliente: " + e.getMessage());
        } finally {
            try {
                socketCliente.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar socket do cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Salva o preço de uma ação no banco de dados.
     *
     * @param ticker O código da ação.
     * @param preco O preço da ação.
     */
    public void salvarNoBanco(String ticker, String preco) {
        String sql = "INSERT INTO cache_precos_acoes (ticker, preco, data_hora) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = conexaoBD.prepareStatement(sql)) {
            stmt.setString(1, ticker);
            stmt.setString(2, preco);
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Novo preço inserido no banco de dados com sucesso!");
            } else {
                System.out.println("Nenhuma linha afetada ao inserir o preço.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao inserir preço no banco de dados: " + e.getMessage());
        }
    }

    /**
     * Busca o preço mais recente de uma ação no banco de dados.
     *
     * @param ticker O código da ação.
     * @return Um array com o ticker e o preço da ação, ou {@code null} se não encontrado.
     */
    public String[] buscarNoBanco(String ticker) {
        String sql = "SELECT * FROM cache_precos_acoes WHERE ticker = ? ORDER BY data_hora DESC LIMIT 1";
        try (PreparedStatement stmt = conexaoBD.prepareStatement(sql)) {
            stmt.setString(1, ticker);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("ticker"), rs.getString("preco")};
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar no banco: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca o histórico de preços de uma ação no banco de dados.
     *
     * @param ticker O código da ação.
     * @return Uma string contendo o histórico de preços da ação, ou uma mensagem de erro se ocorrer algum problema.
     */
    public String buscarHistorico(String ticker) {
        String sql = "SELECT preco, data_hora FROM cache_precos_acoes WHERE ticker = ? ORDER BY data_hora DESC LIMIT 10";
        StringBuilder resultado = new StringBuilder();
        try (PreparedStatement stmt = conexaoBD.prepareStatement(sql)) {
            stmt.setString(1, ticker);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultado.append(rs.getString("data_hora"))
                             .append(": ")
                             .append(rs.getString("preco"))
                             .append("\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar histórico no banco: " + e.getMessage());
            return "ERRO_AO_BUSCAR_HISTORICO";
        }
        return resultado.length() > 0 ? resultado.toString() : "HISTORICO_NAO_ENCONTRADO";
    }

    /**
     * Fecha a conexão com o banco de dados.
     */
    public void fecharConexaoBanco() {
        try {
            if (conexaoBD != null && !conexaoBD.isClosed()) {
                conexaoBD.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fechar conexão com o banco: " + e.getMessage());
        }
    }
}

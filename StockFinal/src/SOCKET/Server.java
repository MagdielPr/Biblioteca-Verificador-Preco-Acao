package SOCKET;

import API.API_Client;
import java.io.*;
import java.net.*;
import com.google.gson.*;

/**
 * A classe Server representa um servidor que atende solicitações de clientes para
 * obter preços de ações e históricos de preços.
 */
public class Server {
    private ServerSocket serverSocket;
    private volatile boolean rodando = true;
    private Cache cache;
    private String enderecoBanco;
    private int portaBanco;
    private int portaServidor;
    private API_Client apiClient;

    /**
     * Construtor da classe Server.
     *
     * @param cache O cache utilizado para armazenar preços de ações.
     * @param portaServidor A porta na qual o servidor será iniciado.
     * @param enderecoBanco O endereço do servidor do banco de dados.
     * @param portaBanco A porta do servidor do banco de dados.
     * @param apiBaseUrl A URL base da API utilizada para obter preços de ações.
     * @param apiToken O token de autenticação da API.
     */
    public Server(Cache cache, int portaServidor, String enderecoBanco, int portaBanco, String apiBaseUrl, String apiToken) {
        this.cache = cache;
        this.portaServidor = portaServidor;
        this.enderecoBanco = enderecoBanco;
        this.portaBanco = portaBanco;
        this.apiClient = new API_Client(apiBaseUrl, apiToken);
    }

    /**
     * Inicia o servidor e aguarda conexões de clientes.
     *
     * @throws IOException Se ocorrer um erro de I/O durante a inicialização do servidor.
     */
    public void iniciar() throws IOException {
        System.out.println("Servidor iniciando na porta: " + portaServidor);
        serverSocket = new ServerSocket(portaServidor);

        while (rodando) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ManipuladorCliente(clientSocket)).start();
        }
    }

    /**
     * Para o servidor e fecha o socket do servidor.
     */
    public void parar() {
        rodando = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao fechar o servidor: " + e.getMessage());
        }
    }

    /**
     * A classe interna ManipuladorCliente é responsável por processar as solicitações
     * de cada cliente conectado ao servidor.
     */
    private class ManipuladorCliente implements Runnable {
        private Socket clientSocket;

        /**
         * Construtor da classe ManipuladorCliente.
         *
         * @param clientSocket O socket do cliente conectado.
         */
        public ManipuladorCliente(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /**
         * Método principal que processa as solicitações do cliente.
         */
        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                String entrada;
                while ((entrada = in.readLine()) != null) {
                    if ("SAIR".equalsIgnoreCase(entrada)) {
                        break;
                    }

                    String[] partes = entrada.split(" ", 2);
                    if (partes.length != 2) {
                        out.println("Comando inválido");
                        continue;
                    }

                    String comando = partes[0];
                    String ticker = partes[1];
                    String resposta = processarComando(comando, ticker);
                    out.println(resposta);
                }
            } catch (IOException e) {
                System.out.println("Erro ao comunicar com o cliente: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar socket do cliente: " + e.getMessage());
                }
            }
        }

        /**
         * Processa o comando recebido do cliente.
         *
         * @param comando O comando enviado pelo cliente.
         * @param ticker O ticker da ação solicitado pelo cliente.
         * @return A resposta correspondente ao comando processado.
         */
        private String processarComando(String comando, String ticker) {
            switch (comando.toUpperCase()) {
                case "PRECO":
                    return obterPrecoAtual(ticker);
                case "HISTORICO":
                    return obterHistoricoPrecos(ticker);
                default:
                    return "Comando desconhecido";
            }
        }

        /**
         * Obtém o preço atual de uma ação.
         *
         * @param ticker O ticker da ação.
         * @return O preço atual da ação ou uma mensagem de erro se não for possível obter o preço.
         */
        private String obterPrecoAtual(String ticker) {
            String precoCache = cache.obterPreco(ticker);
            if (precoCache == null) {
                String respostaAPI = apiClient.obterPrecoAcao(ticker);
                if (respostaAPI != null) {
                    try {
                        Gson gson = new Gson();
                        JsonObject json = gson.fromJson(respostaAPI, JsonObject.class);
                        JsonArray results = json.getAsJsonArray("results");
                        if (results != null && results.size() > 0) {
                            JsonObject stockInfo = results.get(0).getAsJsonObject();
                            String novoPreco = stockInfo.get("regularMarketPrice").getAsString();

                            cache.atualizarCache(ticker, novoPreco);
                            salvarPrecoBanco(ticker, novoPreco);

                            return novoPreco;
                        }
                    } catch (Exception e) {
                        System.out.println("Erro ao processar JSON: " + e.getMessage());
                    }
                }
                return "Preço não disponível";
            }
            return precoCache;
        }

        /**
         * Salva o preço de uma ação no banco de dados.
         *
         * @param ticker O ticker da ação.
         * @param preco O preço da ação a ser salvo.
         */
        private void salvarPrecoBanco(String ticker, String preco) {
            try (Socket socketBanco = new Socket(enderecoBanco, portaBanco);
                 PrintWriter out = new PrintWriter(socketBanco.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socketBanco.getInputStream()))) {
                out.println("SALVAR " + ticker + " " + preco);
                String confirmacao = in.readLine();
                if ("PRECO_SALVO".equals(confirmacao)) {
                    System.out.println("Preço salvo no banco de dados com sucesso.");
                } else {
                    System.out.println("Erro ao salvar no banco de dados: " + confirmacao);
                }
            } catch (IOException e) {
                System.out.println("Erro ao comunicar com o banco de dados: " + e.getMessage());
            }
        }

        /**
         * Obtém o histórico de preços de uma ação.
         *
         * @param ticker O ticker da ação.
         * @return O histórico de preços da ação ou uma mensagem de erro se não for possível obter o histórico.
         */
        private String obterHistoricoPrecos(String ticker) {
            try (Socket socketBanco = new Socket(enderecoBanco, portaBanco);
                 PrintWriter out = new PrintWriter(socketBanco.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socketBanco.getInputStream()))) {
                out.println("HISTORICO " + ticker);
                StringBuilder historico = new StringBuilder();
                String linha;
                while ((linha = in.readLine()) != null && !linha.equals("FIM_HISTORICO")) {
                    historico.append(linha).append("\n");
                }
                if (historico.length() == 0) {
                    return "HISTORICO_NAO_ENCONTRADO\nFIM_HISTORICO";
                }
                historico.append("FIM_HISTORICO");
                return historico.toString();
            } catch (IOException e) {
                System.out.println("Erro ao comunicar com o banco de dados: " + e.getMessage());
                return "Erro ao obter histórico\nFIM_HISTORICO";
            }
        }
    }
}

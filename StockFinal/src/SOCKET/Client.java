package SOCKET;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * A classe Client representa um cliente que se conecta a um servidor
 * para solicitar preços de ações e históricos de preços.
 */
public class Client {
    private Socket clientSocket;
    private Scanner scanner;
    private String enderecoServidor;
    private int portaServidor;

    /**
     * Construtor da classe Client.
     *
     * @param enderecoServidor O endereço do servidor ao qual o cliente se conectará.
     * @param portaServidor A porta do servidor ao qual o cliente se conectará.
     */
    public Client(String enderecoServidor, int portaServidor) {
        this.scanner = new Scanner(System.in);
        this.enderecoServidor = enderecoServidor;
        this.portaServidor = portaServidor;
    }

    /**
     * Inicia a conexão do cliente com o servidor e exibe o menu de opções.
     *
     * @throws IOException Se ocorrer um erro de I/O durante a conexão ou comunicação.
     */
    public void iniciar() throws IOException {
        clientSocket = new Socket(enderecoServidor, portaServidor);
        System.out.println("Cliente conectado ao servidor: " + enderecoServidor + " na porta: " + portaServidor);
        loopMenu();
    }

    /**
     * Fecha a conexão do cliente com o servidor e libera os recursos.
     */
    public void fechar() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao fechar o cliente: " + e.getMessage());
        }
    }

    /**
     * Exibe o menu e processa as opções escolhidas pelo usuário.
     */
    private void loopMenu() {
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String opcao;
            do {
                exibirMenu();
                opcao = scanner.nextLine();
                switch (opcao) {
                    case "1":
                        solicitarPrecoAcao(out, in);
                        break;
                    case "2":
                        solicitarHistoricoPrecos(out, in);
                        break;
                    case "3":
                        System.out.println("Saindo...");
                        out.println("SAIR");
                        return;
                    default:
                        System.out.println("Opção inválida!");
                }
            } while (true);
        } catch (IOException e) {
            System.out.println("Erro ao comunicar com o servidor: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar socket do cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Exibe o menu de opções para o usuário.
     */
    private void exibirMenu() {
        System.out.println("\n=== Menu ===");
        System.out.println("1. Solicitar preço de ação");
        System.out.println("2. Solicitar histórico de preços");
        System.out.println("3. Sair");
        System.out.print("Escolha uma opção: ");
    }

    /**
     * Solicita o preço atual de uma ação ao servidor.
     *
     * @param out PrintWriter para enviar dados ao servidor.
     * @param in BufferedReader para receber dados do servidor.
     * @throws IOException Se ocorrer um erro de I/O durante a comunicação.
     */
    private void solicitarPrecoAcao(PrintWriter out, BufferedReader in) throws IOException {
        System.out.print("Digite o ticker da ação: ");
        String ticker = scanner.nextLine();
        out.println("PRECO " + ticker);
        String resposta = in.readLine();
        System.out.println("Preço atual de " + ticker + ": " + resposta);
    }

    /**
     * Solicita o histórico de preços de uma ação ao servidor.
     *
     * @param out PrintWriter para enviar dados ao servidor.
     * @param in BufferedReader para receber dados do servidor.
     * @throws IOException Se ocorrer um erro de I/O durante a comunicação.
     */
    private void solicitarHistoricoPrecos(PrintWriter out, BufferedReader in) throws IOException {
        System.out.print("Digite o ticker da ação: ");
        String ticker = scanner.nextLine();
        out.println("HISTORICO " + ticker);
        System.out.println("Histórico de preços de " + ticker + ":");
        String linha;
        while ((linha = in.readLine()) != null) {
            if (linha.equals("FIM_HISTORICO")) {
                break;
            }
            if (linha.equals("HISTORICO_NAO_ENCONTRADO")) {
                System.out.println("Histórico não encontrado para " + ticker);
                return;
            }
            System.out.println(linha);
        }
    }
}

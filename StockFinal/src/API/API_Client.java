package API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Classe responsável por fazer requisições à API de cotações de ações.
 */
public class API_Client {
    private String baseUrl;
    private String token;

    /**
     * Construtor da classe API_Client.
     * 
     * @param baseUrl A URL base da API.
     * @param token O token de autenticação da API.
     */
    public API_Client(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }

    /**
     * Obtém o preço atual de uma ação a partir da API.
     *
     * @param ticker O código da ação a ser consultada.
     * @return Uma string contendo a resposta JSON da API, ou {@code null} em caso de erro.
     */
    public String obterPrecoAcao(String ticker) {
        try {
            String urlStr = baseUrl + ticker + "?token=" + token;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                System.out.println("Erro na conexão: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

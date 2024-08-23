package TEST;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A classe TestConnectionAPI é responsável por verificar a conexão com uma API.
 */
public class TestConnectionAPI {
    private String baseUrl;

    /**
     * Construtor da classe TestConnectionAPI.
     *
     * @param baseUrl A URL base da API.
     */
    public TestConnectionAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Verifica a conexão com a API.
     *
     * @param ticker O código da ação a ser consultada na API.
     * @param apiToken O token de autenticação da API.
     * @return {@code true} se a conexão for bem-sucedida e a resposta for recebida com código 200, 
     *         {@code false} caso contrário.
     */
    public boolean checkConnection(String ticker, String apiToken) {
        try {
            String urlStr = baseUrl + ticker + "?token=" + apiToken;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Conexão com a API estabelecida com sucesso!");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Resposta da API: " + response.toString());
                return true;
            } else {
                System.out.println("Erro na conexão com a API: Código de resposta " + responseCode);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

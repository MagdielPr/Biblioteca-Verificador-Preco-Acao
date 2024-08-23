package SOCKET;

import java.util.HashMap;
import java.util.Map;

/**
 * A classe Cache é responsável por armazenar e gerenciar os preços das ações em um cache local.
 */
public class Cache {
    private Map<String, String> cache;

    /**
     * Construtor da classe Cache.
     * Inicializa o cache como um HashMap.
     */
    public Cache() {
        this.cache = new HashMap<>();
    }

    /**
     * Obtém o preço de uma ação a partir do cache.
     *
     * @param ticker O código da ação.
     * @return O preço da ação, ou {@code null} se o ticker não estiver presente no cache.
     */
    public String obterPreco(String ticker) {
        return cache.get(ticker);
    }

    /**
     * Atualiza o cache com o preço de uma ação.
     * Se o ticker já existir no cache, o preço será atualizado.
     *
     * @param ticker O código da ação.
     * @param preco  O preço da ação.
     */
    public void atualizarCache(String ticker, String preco) {
        cache.put(ticker, preco);
    }
}

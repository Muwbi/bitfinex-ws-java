package cat.muwbi.bitfinex.ws;

import cat.muwbi.bitfinex.ws.websocket.BitfinexWebSocketClient;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitfinexClient {

    @Getter
    private static BitfinexClient instance;

    @Getter
    private final ExecutorService executorService = Executors.newCachedThreadPool((runnable) -> {
        Thread thread = new Thread(runnable);
        return thread;
    });

    @Getter
    private EventBus eventBus;

    @Getter
    private Gson gson;

    @Getter
    private BitfinexWebSocketClient webSocketClient;

    public BitfinexClient() {
        instance = this;

        eventBus = new EventBus();

        gson = new GsonBuilder().create();

        webSocketClient = new BitfinexWebSocketClient(this);
        try {
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

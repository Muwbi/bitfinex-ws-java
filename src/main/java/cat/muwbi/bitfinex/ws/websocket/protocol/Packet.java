package cat.muwbi.bitfinex.ws.websocket.protocol;

import cat.muwbi.bitfinex.ws.BitfinexClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public abstract class Packet {

    public WebSocketFrame toWebSocketFrame() {
        return new TextWebSocketFrame(BitfinexClient.getInstance().getGson().toJson(this));
    }

}

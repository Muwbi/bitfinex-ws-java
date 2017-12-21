package cat.muwbi.bitfinex.ws.websocket.protocol;

import cat.muwbi.bitfinex.ws.BitfinexClient;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public abstract class Packet {

    public WebSocketFrame toWebSocketFrame(BitfinexClient bitfinexClient) {
        return new TextWebSocketFrame(bitfinexClient.getGson().toJson(this));
    }

}

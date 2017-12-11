package cat.muwbi.bitfinex.ws.websocket.protocol;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class PingPacket extends Packet {

    private final String event = "ping";
    private final int cid = 1234;

}

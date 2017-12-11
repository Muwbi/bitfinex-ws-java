package cat.muwbi.bitfinex.ws.websocket;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerMessageDeflateClientExtensionHandshaker;

public class BitfinexWebSocketClientCompressionHandler extends WebSocketClientExtensionHandler {

    public BitfinexWebSocketClientCompressionHandler() {
        super(new PerMessageDeflateClientExtensionHandshaker(6, false, 15, true, false), new DeflateFrameClientExtensionHandshaker(false), new DeflateFrameClientExtensionHandshaker(true));
    }
}

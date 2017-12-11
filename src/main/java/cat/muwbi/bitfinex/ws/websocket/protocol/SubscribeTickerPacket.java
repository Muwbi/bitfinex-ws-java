package cat.muwbi.bitfinex.ws.websocket.protocol;

import cat.muwbi.bitfinex.ws.websocket.items.Symbol;

public class SubscribeTickerPacket extends SubscribePacket {

    public SubscribeTickerPacket(Symbol symbol) {
        super("ticker", symbol);
    }

}

package cat.muwbi.bitfinex.ws.websocket.protocol;

import cat.muwbi.bitfinex.ws.websocket.items.Symbol;

public class SubscribePacket extends Packet {

    // tIOTAUSD
    /*

    {
  event: "subscribe",
  channel: "trades",
  symbol: SYMBOL
}

     */

    private final String event = "subscribe";
    private final String channel;
    private final Symbol symbol;

    public SubscribePacket(String channel, Symbol symbol) {
        this.channel = channel;
        this.symbol = symbol;
    }

}

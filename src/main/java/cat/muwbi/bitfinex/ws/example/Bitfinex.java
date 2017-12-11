package cat.muwbi.bitfinex.ws.example;

import cat.muwbi.bitfinex.ws.BitfinexClient;
import cat.muwbi.bitfinex.ws.websocket.items.Symbol;
import cat.muwbi.bitfinex.ws.websocket.protocol.SubscribeTickerPacket;
import cat.muwbi.bitfinex.ws.websocket.protocol.TickerPacket;

public class Bitfinex {


    public static void main(String[] args) {
        BitfinexClient bitfinexClient = new BitfinexClient();
        bitfinexClient.getEventBus().register(new BitfinexPacketHandler());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Sending SubscribeTickerPacket");
        bitfinexClient.getWebSocketClient().getWebSocketClientHandler().sendPacket(new SubscribeTickerPacket(Symbol.IOTUSD));
    }

}

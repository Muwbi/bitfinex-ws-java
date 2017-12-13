package cat.muwbi.bitfinex.ws.example;

import cat.muwbi.bitfinex.ws.BitfinexClient;
import cat.muwbi.bitfinex.ws.websocket.items.Symbol;
import cat.muwbi.bitfinex.ws.websocket.protocol.AuthenticationPacket;
import cat.muwbi.bitfinex.ws.websocket.protocol.SubscribeTickerPacket;
import cat.muwbi.bitfinex.ws.websocket.protocol.TickerPacket;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class Bitfinex {

    static String apiKey = "ORPv0raIny7d6MuglgRsB5uM6jehaMj2TeSgSkRnAcw";
    static String apiSecret = "9r8fntgCNtT141XpCl0u3pmpEDAiakKKgdLN7eFy75u";

    public static void main(String[] args) {
        BitfinexClient bitfinexClient = new BitfinexClient();
        bitfinexClient.getEventBus().register(new BitfinexPacketHandler());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Sending AuthenticationPacket");
        AuthenticationPacket authPacket = new AuthenticationPacket(apiKey, apiSecret);
        System.out.println("auth packet: " + ((TextWebSocketFrame) authPacket.toWebSocketFrame()).text());
        bitfinexClient.getWebSocketClient().getWebSocketClientHandler().sendPacket(authPacket);
        System.out.println("Sending SubscribeTickerPacket");
        //bitfinexClient.getWebSocketClient().getWebSocketClientHandler().sendPacket(new SubscribeTickerPacket(Symbol.IOTUSD));
    }

}

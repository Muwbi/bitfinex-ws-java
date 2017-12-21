package cat.muwbi.bitfinex.ws.example;

import cat.muwbi.bitfinex.ws.websocket.events.*;
import cat.muwbi.bitfinex.ws.websocket.items.Symbol;
import cat.muwbi.bitfinex.ws.websocket.protocol.AuthenticationPacket;
import cat.muwbi.bitfinex.ws.websocket.protocol.SubscribePacket;
import cat.muwbi.bitfinex.ws.websocket.protocol.TickerPacket;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

public class BitfinexPacketHandler {

    static String apiKey = "ORPv0raIny7d6MuglgRsB5uM6jehaMj2TeSgSkRnAcw";
    static String apiSecret = "9r8fntgCNtT141XpCl0u3pmpEDAiakKKgdLN7eFy75u";

    public BitfinexPacketHandler() {
    }

    @Subscribe
    public void onConnected(ConnectedEvent event) {
        System.out.println("example: connected yay");

        AuthenticationPacket authPacket = new AuthenticationPacket(apiKey, apiSecret);
        //Bitfinex.bitfinexClient.getWebSocketClient().getWebSocketClientHandler().sendPacket(authPacket);

        Bitfinex.bitfinexClient.getWebSocketClient().getWebSocketClientHandler().sendPacket(new SubscribePacket("ticker", Symbol.IOTUSD));
    }

    @Subscribe
    public void onAuthenticated(AuthenticatedEvent event) {
        System.out.println("example: auth successful");
    }

    @Subscribe
    public void onAuthenticationError(AuthentificationErrorEvent event) {
        System.out.println("example: auth error: " + event.getErrorCode());
    }

    @Subscribe
    public void onTickerUpdate(TickerUpdateEvent event) {
        System.out.println(event.getAsk() + " <> " + event.getBid());
    }

    @Subscribe
    public void onPacketReceived(PacketReceivedEvent event) {
        if (event.getPacket() instanceof TickerPacket) {
            System.out.println("Received TickerPacket");
        } else {
            System.out.println("onPacketReceived");
        }
    }

    @Subscribe
    public void onHeartbeat(HeartbeatEvent event) {
        System.out.println("hb");
    }

    @Subscribe
    public void onDeadEvent(DeadEvent event) {

    }

}

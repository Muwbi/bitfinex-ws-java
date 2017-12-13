package cat.muwbi.bitfinex.ws.example;

import cat.muwbi.bitfinex.ws.websocket.events.PacketReceivedEvent;
import cat.muwbi.bitfinex.ws.websocket.protocol.HeartbeatPacket;
import cat.muwbi.bitfinex.ws.websocket.protocol.TickerPacket;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

public class BitfinexPacketHandler {

    public BitfinexPacketHandler() {
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
    public void onHeartbeat(HeartbeatPacket event) {

    }

    @Subscribe
    public void onDeadEvent(DeadEvent event) {
        System.out.println("deadevent");
        System.out.println(event.getSource());
    }

}

package cat.muwbi.bitfinex.ws.websocket.protocol;

public class PingPacket extends Packet {

    private final String event = "ping";
    private final int cid = 1234;

}

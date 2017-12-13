package cat.muwbi.bitfinex.ws.websocket.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HeartbeatPacket extends Packet {

    private final int channelId;

}

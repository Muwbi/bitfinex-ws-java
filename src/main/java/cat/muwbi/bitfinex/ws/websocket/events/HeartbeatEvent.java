package cat.muwbi.bitfinex.ws.websocket.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HeartbeatEvent extends Event {

    private final int channelId;

}

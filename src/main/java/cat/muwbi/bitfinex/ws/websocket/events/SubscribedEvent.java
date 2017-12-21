package cat.muwbi.bitfinex.ws.websocket.events;

import cat.muwbi.bitfinex.ws.websocket.protocol.channels.ChannelInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubscribedEvent extends Event {

    private final ChannelInfo channelInfo;

}

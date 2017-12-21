package cat.muwbi.bitfinex.ws.websocket.events;

import cat.muwbi.bitfinex.ws.websocket.protocol.channels.ChannelInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookUpdateEvent extends Event {

    private final ChannelInfo channelInfo;
    private final float price;
    private final int count;
    private final float amount;

}

package cat.muwbi.bitfinex.ws.websocket.events;

import cat.muwbi.bitfinex.ws.websocket.protocol.channels.ChannelInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CandleUpdateEvent extends Event {

    private final ChannelInfo channelInfo;
    private final int mts;
    private final float open;
    private final float close;
    private final float high;
    private final float low;
    private final float volume;

}

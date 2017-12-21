package cat.muwbi.bitfinex.ws.websocket.events;

import cat.muwbi.bitfinex.ws.websocket.protocol.channels.ChannelInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TickerUpdateEvent extends Event {

    private final ChannelInfo channelInfo;
    private final float bid;
    private final float bidSize;
    private final float ask;
    private final float askSize;
    private final float dailyChange;
    private final float dailyChangePercentage;
    private final float lastPrice;
    private final float volume;
    private final float high;
    private final float low;

}

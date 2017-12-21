package cat.muwbi.bitfinex.ws.websocket.events;

import cat.muwbi.bitfinex.ws.websocket.protocol.channels.ChannelInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TradeUpdateEvent extends Event {

    public enum Type {
        TRADE_EXECUTION, TRADE_UPDATE
    }

    private final ChannelInfo channelInfo;
    private final Type type;
    private final float id;
    private final int mts;
    private final float amount;
    private final float price;

}

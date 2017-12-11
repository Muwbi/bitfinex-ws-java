package cat.muwbi.bitfinex.ws.websocket.protocol.channels;

import lombok.Getter;

@Getter
public class TradeSubscriptionChannelInfo extends SubscriptionChannelInfo {

    private final String symbol;
    private final String pair;

    public TradeSubscriptionChannelInfo(String channel, int channelId, String symbol, String pair) {
        super(channel, channelId);
        this.symbol = symbol;
        this.pair = pair;
    }

}

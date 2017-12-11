package cat.muwbi.bitfinex.ws.websocket.protocol.channels;

import lombok.Getter;

@Getter
public class TickerSubscriptionChannelInfo extends SubscriptionChannelInfo {

    private final String pair;

    public TickerSubscriptionChannelInfo(String channel, int channelId, String pair) {
        super(channel, channelId);
        this.pair = pair;
    }

}

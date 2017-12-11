package cat.muwbi.bitfinex.ws.websocket.protocol.channels;

import lombok.Getter;

@Getter
public class CandleSubscriptionChannelInfo extends SubscriptionChannelInfo {

    private final String key;

    public CandleSubscriptionChannelInfo(String channel, int channelId, String key) {
        super(channel, channelId);
        this.key = key;
    }

}

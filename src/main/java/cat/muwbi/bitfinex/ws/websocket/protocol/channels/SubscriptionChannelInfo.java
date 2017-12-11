package cat.muwbi.bitfinex.ws.websocket.protocol.channels;

import lombok.Getter;

@Getter
public class SubscriptionChannelInfo extends ChannelInfo {

    SubscriptionChannelInfo(String channel, int channelId) {
        super(channel, channelId);
    }

}

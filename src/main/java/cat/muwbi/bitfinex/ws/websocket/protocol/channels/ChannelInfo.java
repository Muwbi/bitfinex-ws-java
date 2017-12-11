package cat.muwbi.bitfinex.ws.websocket.protocol.channels;

import lombok.Getter;

@Getter
public class ChannelInfo {

    private final String channel;
    private final int channelId;

    ChannelInfo(String channel, int channelId) {
        this.channel = channel;
        this.channelId = channelId;
    }

}

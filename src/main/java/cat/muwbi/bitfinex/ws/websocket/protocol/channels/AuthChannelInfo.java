package cat.muwbi.bitfinex.ws.websocket.protocol.channels;


public class AuthChannelInfo extends ChannelInfo {

    public AuthChannelInfo(int channelId) {
        super("auth", channelId);
    }

}

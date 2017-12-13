package cat.muwbi.bitfinex.ws.websocket.protocol.channels;


public class AccountChannelInfo extends ChannelInfo {

    public AccountChannelInfo(int channelId) {
        super("auth", channelId);
    }

}

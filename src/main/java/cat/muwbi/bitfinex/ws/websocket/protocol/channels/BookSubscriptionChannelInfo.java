package cat.muwbi.bitfinex.ws.websocket.protocol.channels;

import lombok.Getter;

@Getter
public class BookSubscriptionChannelInfo extends SubscriptionChannelInfo {

    private final String symbol;
    private final String precision;
    private final String frequency;
    private final String length;

    public BookSubscriptionChannelInfo(String channel, int channelId, String symbol, String precision, String frequency, String length) {
        super(channel, channelId);
        this.symbol = symbol;
        this.precision = precision;
        this.frequency = frequency;
        this.length = length;
    }

}

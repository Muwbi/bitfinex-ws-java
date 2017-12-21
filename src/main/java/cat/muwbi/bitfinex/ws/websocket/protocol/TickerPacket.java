package cat.muwbi.bitfinex.ws.websocket.protocol;

import lombok.*;

import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TickerPacket extends ResponsePacket {

    public static Pattern PATTERN = Pattern.compile("\\[(\\d{1,}),(\\[\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,}),(\\d{1,}\\.\\d{1,})\\]\\]");

    @NonNull
    private final boolean isHeartbeat;
    //private float flashReturnRate;
    private int channelId;
    private float bid;
    //private int bidPeriod;
    private float bidSize;
    private float ask;
    //private int askPeriod;
    private float askSize;
    private float dailyChange;
    private float dailyChangePercentage;
    private float lastPrice;
    private float volume;
    private float high;
    private float low;

    public static boolean isTickerPacket(String message) {
        return PATTERN.matcher(message).matches();
    }

}

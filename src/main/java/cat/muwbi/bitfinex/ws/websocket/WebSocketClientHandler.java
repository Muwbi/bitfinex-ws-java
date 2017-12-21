package cat.muwbi.bitfinex.ws.websocket;

import cat.muwbi.bitfinex.ws.BitfinexClient;
import cat.muwbi.bitfinex.ws.websocket.events.*;
import cat.muwbi.bitfinex.ws.websocket.protocol.Packet;
import cat.muwbi.bitfinex.ws.websocket.protocol.channels.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@EqualsAndHashCode(callSuper = true)
@Data
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private final BitfinexClient bitfinexClient;
    private final WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;

    private Channel channel;

    private HashMap<Integer, ChannelInfo> channelMap = new HashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) {
        handshakeFuture = channelHandlerContext.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        handshaker.handshake(channelHandlerContext.channel());

        channel = channelHandlerContext.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        System.out.println("WebSocket Client disconnected!");
        System.out.println(channelHandlerContext.channel().isActive());

        bitfinexClient.getEventBus().post(new ConnectionClosedEvent());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        channelHandlerContext.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) {
        System.out.println("Channel unregistered");
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        Channel channel = channelHandlerContext.channel();

        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(channel, (FullHttpResponse) message);
                System.out.println("WebSocket Client connected");
                handshakeFuture.setSuccess();

                BitfinexClient.getInstance().getEventBus().post(new ConnectedEvent());
            } catch (WebSocketHandshakeException exception) {
                System.out.println("WebSocket Client failed to connect");
                handshakeFuture.setFailure(exception);
            }
            return;
        }

        if (message instanceof FullHttpResponse) {
            System.out.println("Unexpected FullHttpResponse");
            return;
        }

        WebSocketFrame frame = (WebSocketFrame) message;
        if (frame instanceof TextWebSocketFrame) {

            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) frame;

            JsonElement jsonElement = BitfinexClient.getInstance().getGson().fromJson(textWebSocketFrame.text(), JsonElement.class);
            System.out.println("debug: " + textWebSocketFrame.text());

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                int channelId = jsonArray.get(0).getAsInt();

                if (!channelMap.containsKey(channelId)) {
                    return;
                }

                if (!jsonArray.get(1).isJsonArray() && jsonArray.get(1).getAsString().equals("hb")) {
                    BitfinexClient.getInstance().getEventBus().post(new HeartbeatEvent(channelId));
                    return;
                }

                ChannelInfo channelInfo = channelMap.get(channelId);

                if (channelInfo instanceof BookSubscriptionChannelInfo) {
                    JsonArray updateData = jsonArray.get(1).getAsJsonArray();

                    float price = updateData.get(0).getAsFloat();
                    int count = updateData.get(1).getAsInt();
                    float amount = updateData.get(2).getAsFloat();

                    BitfinexClient.getInstance().getEventBus().post(new BookUpdateEvent(channelInfo, price, count, amount));

                    System.out.println("Received BookSubscription update");
                } else if (channelInfo instanceof TradeSubscriptionChannelInfo) {
                    if (jsonArray.get(1).isJsonArray()) {
                        //ignore snapshots
                        return;
                    }

                    String type = jsonArray.get(1).getAsString();
                    JsonArray updateData = jsonArray.get(2).getAsJsonArray();

                    float id = updateData.get(0).getAsFloat();
                    int mts = updateData.get(1).getAsInt();
                    float amount = updateData.get(2).getAsFloat();
                    float price = updateData.get(3).getAsFloat();

                    BitfinexClient.getInstance().getEventBus().post(new TradeUpdateEvent(channelInfo, (type.equals("tu") ? TradeUpdateEvent.Type.TRADE_UPDATE : TradeUpdateEvent.Type.TRADE_EXECUTION), id, mts, amount, price));

                    System.out.println("Received TradeSubscription update");
                } else if (channelInfo instanceof TickerSubscriptionChannelInfo) {
                    JsonArray updateData = jsonArray.get(1).getAsJsonArray();

                    float bid = updateData.get(0).getAsFloat();
                    float bidSize = updateData.get(1).getAsFloat();
                    float ask = updateData.get(2).getAsFloat();
                    float askSize = updateData.get(3).getAsFloat();
                    float dailyChange = updateData.get(4).getAsFloat();
                    float dailyChangePercentage = updateData.get(5).getAsFloat();
                    float lastPrice = updateData.get(6).getAsFloat();
                    float volume = updateData.get(7).getAsFloat();
                    float high = updateData.get(8).getAsFloat();
                    float low = updateData.get(9).getAsFloat();

                    BitfinexClient.getInstance().getEventBus().post(new TickerUpdateEvent(channelInfo, bid, bidSize, ask, askSize, dailyChange, dailyChangePercentage, lastPrice, volume, high, low));

                    System.out.println("Received TickerSubscription update");
                } else if (channelInfo instanceof CandleSubscriptionChannelInfo) {
                    JsonArray updateData = jsonArray.get(1).getAsJsonArray();

                    int mts = updateData.get(0).getAsInt();
                    float open = updateData.get(1).getAsFloat();
                    float close = updateData.get(2).getAsFloat();
                    float high = updateData.get(3).getAsFloat();
                    float low = updateData.get(4).getAsFloat();
                    float volume = updateData.get(5).getAsFloat();

                    BitfinexClient.getInstance().getEventBus().post(new CandleUpdateEvent(channelInfo, mts, open, close, high, low, volume));

                    System.out.println("Received CandleSubscription update");
                } else if (channelInfo instanceof AccountChannelInfo) {
                    System.out.println("Received Auth update: " + textWebSocketFrame.text());
                } else {
                    System.out.println("Message in unknown channel");
                    System.out.println("Received txt: " + ((TextWebSocketFrame) frame).text());
                }
            } else {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("event")) {
                    String event = jsonObject.get("event").getAsString();

                    if (event.equals("subscribed")) {
                        // Subscription info
                        int channelId = jsonObject.get("chanId").getAsInt();
                        String channelName = jsonObject.get("channel").getAsString();

                        switch (channelName) {
                            case "book":
                                channelMap.put(channelId, new BookSubscriptionChannelInfo(channelName,
                                        channelId,
                                        jsonObject.get("symbol").getAsString(),
                                        jsonObject.get("prec").getAsString(),
                                        jsonObject.get("freq").getAsString(),
                                        jsonObject.get("len").getAsString()));
                                break;
                            case "trades":
                                channelMap.put(channelId, new TradeSubscriptionChannelInfo(channelName,
                                        channelId,
                                        jsonObject.get("symbol").getAsString(),
                                        jsonObject.get("pair").getAsString()));
                                break;
                            case "ticker":
                                channelMap.put(channelId, new TickerSubscriptionChannelInfo(channelName,
                                        channelId,
                                        jsonObject.get("symbol").getAsString()));
                                break;
                            case "candles":
                                channelMap.put(channelId, new CandleSubscriptionChannelInfo(channelName,
                                        channelId,
                                        jsonObject.get("key").getAsString()));
                                break;
                            default:
                                // unknown channel
                                System.out.println("Received subscription info for unknown channel");
                                System.out.println("Received txt: " + ((TextWebSocketFrame) frame).text());
                                return;
                        }

                        BitfinexClient.getInstance().getEventBus().post(new SubscribedEvent(channelMap.get(channelId)));
                    } else if (event.equals("auth") && !jsonObject.get("status").getAsString().equals("OK")) {
                        // Auth error
                        BitfinexClient.getInstance().getEventBus().post(new AuthentificationErrorEvent(jsonObject.get("code").getAsInt()));
                    } else if (event.equals("auth")) {
                        // Auth successful
                        // Account channel always uses 0 as channelId
                        channelMap.put(0, new AccountChannelInfo(0));

                        BitfinexClient.getInstance().getEventBus().post(new AuthenticatedEvent());
                    } else if (event.equals("info")) {

                        // TODO: EventBus post
                    } else {
                        // Nothing important, just post to the EventBus

                        // TODO: EventBus post
                    }
                }
            }
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("Received closing");
            BitfinexClient.getInstance().getEventBus().post(new ConnectionClosedEvent());
        } else {
            System.out.println("Received something else: " + frame.getClass().getName());
        }
    }

    public void sendPacket(Packet packet) {
        System.out.println("Sending packet: " + packet.getClass().getSimpleName());
        channel.writeAndFlush(packet.toWebSocketFrame());
    }

}

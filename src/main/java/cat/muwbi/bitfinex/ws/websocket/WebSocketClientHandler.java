package cat.muwbi.bitfinex.ws.websocket;

import cat.muwbi.bitfinex.ws.BitfinexClient;
import cat.muwbi.bitfinex.ws.websocket.protocol.HeartbeatPacket;
import cat.muwbi.bitfinex.ws.websocket.protocol.Packet;
import cat.muwbi.bitfinex.ws.websocket.protocol.channels.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;

import java.util.HashMap;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    private Channel channel;

    private HashMap<Integer, ChannelInfo> channelMap = new HashMap<>();

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

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

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                int channelId = jsonArray.get(0).getAsInt();

                if (!channelMap.containsKey(channelId)) {
                    return;
                }

                if (!jsonArray.get(1).isJsonArray() && jsonArray.get(1).getAsString().equals("hb")) {
                    BitfinexClient.getInstance().getEventBus().post(new HeartbeatPacket(channelId));
                    return;
                }

                ChannelInfo channelInfo = channelMap.get(channelId);

                if (channelInfo instanceof BookSubscriptionChannelInfo) {
                    System.out.println("Received BookSubscription update: " + textWebSocketFrame.text());
                } else if (channelInfo instanceof TradeSubscriptionChannelInfo) {
                    System.out.println("Received TradeSubscription update: " + textWebSocketFrame.text());
                } else if (channelInfo instanceof TickerSubscriptionChannelInfo) {
                    System.out.println("Received TickerSubscription update: " + textWebSocketFrame.text());
                } else if (channelInfo instanceof CandleSubscriptionChannelInfo) {
                    System.out.println("Received CandleSubscription update: " + textWebSocketFrame.text());
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
                                System.out.println("Received a book subscription info");
                                break;
                            case "trades":
                                channelMap.put(channelId, new TradeSubscriptionChannelInfo(channelName,
                                        channelId,
                                        jsonObject.get("symbol").getAsString(),
                                        jsonObject.get("pair").getAsString()));
                                System.out.println("Received a trade subscription info");
                                break;
                            case "ticker":
                                channelMap.put(channelId, new TickerSubscriptionChannelInfo(channelName,
                                        channelId,
                                        jsonObject.get("symbol").getAsString()));
                                System.out.println("Received a ticker subscription info");
                                break;
                            case "candles":
                                channelMap.put(channelId, new CandleSubscriptionChannelInfo(channelName,
                                        channelId,
                                        jsonObject.get("key").getAsString()));
                                System.out.println("Received a candle subscription info");
                                break;
                            default:
                                // unknown channel
                                System.out.println("Received subscription info for unknown channel");
                                System.out.println("Received txt: " + ((TextWebSocketFrame) frame).text());
                                return;
                        }

                        // TODO: EventBus post
                    } else if (event.equals("auth") && !jsonObject.get("status").getAsString().equals("OK")) {
                        // Auth error
                        System.out.println("Auth error: status: " + jsonObject.get("status") + ", code: " + jsonObject.get("code"));
                    } else if (event.equals("auth")) {
                        // Auth successful
                        // Account channel always uses 0 as channelId
                        System.out.println("Auth successfull");
                        channelMap.put(0, new AccountChannelInfo(0));

                        // TODO: EventBus post
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
        } else {
            System.out.println("Received something else: " + frame.getClass().getName());
        }
    }

    public void sendPacket(Packet packet) {
        System.out.println("Sending packet: " + packet.getClass().getSimpleName());
        channel.writeAndFlush(packet.toWebSocketFrame());
    }

}

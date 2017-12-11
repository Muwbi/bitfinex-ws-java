package cat.muwbi.bitfinex.ws.websocket;

import cat.muwbi.bitfinex.ws.BitfinexClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;

public class BitfinexWebSocketClient {

    private static final String API_URL = "wss://api.bitfinex.com:443/ws/2";


    private WebSocketClientHandler webSocketClientHandler;

    public BitfinexWebSocketClient() {

    }

    public void connectSync() {
        BitfinexClient.getInstance().getExecutorService().execute(() -> {
            try {
                connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void connect() throws Exception {
        URI apiUri = new URI(API_URL);

        final String host = apiUri.getHost();
        final int port = apiUri.getPort();

        Bootstrap bootstrap = new Bootstrap();

        final SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        webSocketClientHandler = new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(apiUri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(sslContext.newHandler(channel.alloc(), host, port));
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        pipeline.addLast(new BitfinexWebSocketClientCompressionHandler());
                        pipeline.addLast(webSocketClientHandler);
                    }
                });

        BitfinexClient.getInstance().getExecutorService().execute(() -> {
            Channel channel = null;
            try {
                channel = bootstrap.connect(host, port).sync().channel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                webSocketClientHandler.handshakeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public WebSocketClientHandler getWebSocketClientHandler() {
        return webSocketClientHandler;
    }


}


package cat.muwbi.bitfinex.ws.example;

import cat.muwbi.bitfinex.ws.BitfinexClient;

public class Bitfinex {

    static String apiKey = "";
    static String apiSecret = "";

    public static BitfinexClient bitfinexClient;

    public static void main(String[] args) {
        bitfinexClient = new BitfinexClient();
        bitfinexClient.getEventBus().register(new BitfinexPacketHandler());
    }

}

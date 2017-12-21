package cat.muwbi.bitfinex.ws.example;

import cat.muwbi.bitfinex.ws.BitfinexClient;

public class Bitfinex {

    static String apiKey = "ORPv0raIny7d6MuglgRsB5uM6jehaMj2TeSgSkRnAcw";
    static String apiSecret = "9r8fntgCNtT141XpCl0u3pmpEDAiakKKgdLN7eFy75u";

    public static BitfinexClient bitfinexClient;

    public static void main(String[] args) {
        bitfinexClient = new BitfinexClient();
        bitfinexClient.getEventBus().register(new BitfinexPacketHandler());
    }

}

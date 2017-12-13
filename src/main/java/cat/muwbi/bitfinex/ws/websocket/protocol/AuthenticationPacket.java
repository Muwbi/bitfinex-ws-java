package cat.muwbi.bitfinex.ws.websocket.protocol;

import lombok.Getter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Getter
public class AuthenticationPacket extends Packet {

    private final String apiKey;
    private final String authSig;
    private String authNonce;
    private final String authPayload;
    private final String event = "auth";

    public AuthenticationPacket(String apiKey, String apiSecret) {
        this.authNonce = String.valueOf(System.currentTimeMillis() * 1000);
        System.out.println("authNonce: " + authNonce);
        this.authPayload = "AUTH" + authNonce + authNonce;
        this.authSig = generateSignature(authPayload, apiSecret);
        this.apiKey = apiKey;
        System.out.println("authSig: " + authSig);
        this.authNonce = String.valueOf(Long.valueOf(authNonce) + 1);
    }

    private String generateSignature(String payload, String apiSecret) {
        try {
            return hmacSHA384(payload.getBytes(), apiSecret.getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String hmacSHA384(byte[] payload, byte[] nonce) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(nonce, "HmacSHA384");
        Mac mac = Mac.getInstance("HmacSHA384");

        mac.init(signingKey);

        return bytesToHex(mac.doFinal(payload));
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte aHash : hash) {
            final String hex = Integer.toHexString(0xff & aHash);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}

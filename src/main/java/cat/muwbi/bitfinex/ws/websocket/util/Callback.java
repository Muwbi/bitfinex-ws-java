package cat.muwbi.bitfinex.ws.websocket.util;

public interface Callback<T> {
    void done( T arg );
}

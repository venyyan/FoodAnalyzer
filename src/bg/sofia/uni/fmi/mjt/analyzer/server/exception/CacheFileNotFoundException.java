package bg.sofia.uni.fmi.mjt.analyzer.server.exception;

public class CacheFileNotFoundException extends Exception {
    public CacheFileNotFoundException(String message) {
        super(message);
    }

    public CacheFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

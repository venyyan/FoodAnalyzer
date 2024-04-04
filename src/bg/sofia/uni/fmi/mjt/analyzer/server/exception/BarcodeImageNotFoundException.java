package bg.sofia.uni.fmi.mjt.analyzer.server.exception;

public class BarcodeImageNotFoundException extends Exception {
    public BarcodeImageNotFoundException(String message) {
        super(message);
    }

    public BarcodeImageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

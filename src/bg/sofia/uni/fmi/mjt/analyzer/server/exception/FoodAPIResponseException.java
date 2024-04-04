package bg.sofia.uni.fmi.mjt.analyzer.server.exception;

public class FoodAPIResponseException extends Exception {
    public FoodAPIResponseException(String message) {
        super(message);
    }

    public FoodAPIResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}

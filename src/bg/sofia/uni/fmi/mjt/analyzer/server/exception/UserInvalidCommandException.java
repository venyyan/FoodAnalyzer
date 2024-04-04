package bg.sofia.uni.fmi.mjt.analyzer.server.exception;

public class UserInvalidCommandException extends Exception {
    public UserInvalidCommandException(String message) {
        super(message);
    }

    public UserInvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}

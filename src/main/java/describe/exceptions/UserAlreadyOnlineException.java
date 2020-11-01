package describe.exceptions;

public class UserAlreadyOnlineException extends RuntimeException {
    public UserAlreadyOnlineException(String message) {
        super(message);
    }
}
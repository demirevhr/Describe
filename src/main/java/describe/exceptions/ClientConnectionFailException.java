package describe.exceptions;

public class ClientConnectionFailException extends RuntimeException {
    public ClientConnectionFailException(String message) {
        super(message);
    }
}
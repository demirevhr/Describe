package describe.exceptions;

public class GameDoesNotExistException extends RuntimeException {
    private static String message = "Game does not exist";
    public GameDoesNotExistException() {
        super(message);
    }
}
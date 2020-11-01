package describe.exceptions.commands;

public class GameAlreadyExistsException extends CommandFailureException {
    public GameAlreadyExistsException(String message) {
            super(message);
        }
}
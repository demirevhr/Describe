package describe.exceptions.commands;

public class GameAlreadyStartedException extends CommandFailureException {
    public GameAlreadyStartedException(String message) {
        super(message);
    }
}
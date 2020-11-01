package describe.exceptions.commands;

public class GameFullException extends CommandFailureException {
    public GameFullException(String message) { super(message); }
}
package describe.exceptions.commands;

public class CommandFailureException extends RuntimeException {
    public CommandFailureException(String message) {
        super(message);
    }
}
package describe.exceptions.commands;

public class IllegalCommandException extends CommandFailureException {
    public IllegalCommandException(String message) {
        super(message);
    }
}
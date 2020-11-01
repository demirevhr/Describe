package describe.exceptions.commands;

public class NoSuchGameException extends CommandFailureException {
    public NoSuchGameException(String message) {
        super(message);
    }
}
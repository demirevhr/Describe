package describe.exceptions.commands;

public class WordPointsOutOfRangeException extends CommandFailureException {
    public WordPointsOutOfRangeException(String message) {
        super(message);
    }
}
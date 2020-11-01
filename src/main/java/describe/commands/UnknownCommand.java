package describe.commands;

import describe.enums.ConnectionState;
import describe.exceptions.commands.IllegalCommandException;

public class UnknownCommand extends Command {
    private static final String COMMAND_NAME = "unknown";

    @Override
    public void execute(String commandLine) {
        throw new IllegalCommandException("Unknown command : " + commandLine);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() {
        return null;
    }
}
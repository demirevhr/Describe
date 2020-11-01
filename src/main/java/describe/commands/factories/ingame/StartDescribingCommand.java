package describe.commands.factories.ingame;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;

public class StartDescribingCommand extends Command {
    private static final String COMMAND_NAME = "go";

    StartDescribingCommand(DescribeClientData client, DescribeServer server) {
        super(client, server);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() {
        return ConnectionState.YOUR_TURN;
    }

    @Override
    public void execute(String commandLine) {
        if (commandLine.split("\\s+")[0].equals(COMMAND_NAME)) {
            client.getCurrentGame().play();
        }
    }
}
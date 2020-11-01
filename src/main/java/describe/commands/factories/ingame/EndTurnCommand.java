package describe.commands.factories.ingame;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.exceptions.commands.CommandFailureException;
import describe.server.DescribeServer;

public class EndTurnCommand extends Command {
    private static final String COMMAND_NAME = "end-turn";

    EndTurnCommand(DescribeClientData client, DescribeServer server) {
        super(client, server);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() {
        if (client.getCurrentState() == ConnectionState.YOUR_TURN) {
            if (client.getCurrentGame() == null) {
                return ConnectionState.MENU;
            }
            return ConnectionState.NOT_YOUR_TURN;
        } else {
            return client.getCurrentState();
        }
    }

    @Override
    public void execute(String commandLine) {
        String turnResult = commandLine.split("\\s+")[0];
        if (turnResult.equals("yes")) {
            client.getCurrentGame().endTurn(true);
        } else if (turnResult.equals("no")) {
            client.getCurrentGame().endTurn(false);
        } else {
            throw new CommandFailureException("Enter \"yes\" if you guessed correctly and \"no\" if you didn't");
        }
    }
}
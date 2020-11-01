package describe.commands.factories.awaitinggamestart;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;

public class SendChatMessageCommand extends Command {
    private static final String COMMAND_NAME = "chat";
    SendChatMessageCommand(DescribeClientData client, DescribeServer server) {
        super(client, server);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() {
        return ConnectionState.AWAITING_GAME_START;
    }

    @Override
    public void execute(String commandLine) {
        client.getCurrentGame().printToEachPlayer(commandLine);
    }
}
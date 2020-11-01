package describe.commands.factories.lobby;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;

import java.util.regex.Pattern;

public class StartGameCommand extends Command {
    private static final String COMMAND_NAME = "start";
    private static final String START_GAME_REGEX = String.format("%s(\\s)*", COMMAND_NAME);
    private static final Pattern START_GAME_PATTERN = Pattern.compile(START_GAME_REGEX);
    private static final String UNABLE_TO_START_GAME_MESSAGE = "Game %s cannot be started yet" +
            "\nYou must wait for other players to join the game\n";

    StartGameCommand(DescribeClientData client, DescribeServer server) {
        super(client, server);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() {
        if (client.getCurrentState() == ConnectionState.IN_LOBBY) {
            return ConnectionState.AWAITING_GAME_START;
        } else {
            return client.getCurrentState();
        }
    }

    @Override
    public void execute(String commandLine) {
        client.getCurrentGame().enterGameQueue(client);
        client.send("Ready to play");
    }
}
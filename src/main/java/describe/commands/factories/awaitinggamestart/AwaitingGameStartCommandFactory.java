package describe.commands.factories.awaitinggamestart;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.commands.factories.CommandFactory;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AwaitingGameStartCommandFactory extends CommandFactory {
    public AwaitingGameStartCommandFactory(DescribeClientData client, DescribeServer server) {
        Collection<Command> commands = Arrays
                .asList(new ListPlayersCommand(client, server), new SendChatMessageCommand(client, server));
        commandMap = commands.stream()
                .collect(Collectors.toMap(Command::getCommandName, Function.identity()));
    }

    @Override
    public ConnectionState getFactoryState() {
        return ConnectionState.AWAITING_GAME_START;
    }

    @Override
    public Command getCommand(String commandName) {
        return commandMap.getOrDefault(commandName, commandMap.get("chat"));
    }
}
package describe.commands.factories.lobby;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.commands.factories.CommandFactory;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LobbyCommandFactory extends CommandFactory {
    public LobbyCommandFactory(DescribeClientData client, DescribeServer server) {
        Collection<Command> commands = Arrays
                .asList(new StartGameCommand(client, server));
        commandMap = commands.stream()
                        .collect(Collectors.toMap(Command::getCommandName, Function.identity()));
    }

    @Override
    public ConnectionState getFactoryState() {
        return ConnectionState.IN_LOBBY;
    }
}
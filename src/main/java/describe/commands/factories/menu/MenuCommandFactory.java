package describe.commands.factories.menu;

import describe.client.DescribeClientData;
import describe.commands.*;
import describe.commands.factories.CommandFactory;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuCommandFactory extends CommandFactory {
    public MenuCommandFactory(DescribeClientData client, DescribeServer server) {
        Collection<Command> commands = Arrays
                .asList(new CreateGameCommand(client, server), new JoinGameCommand(client, server),
                        new ListGamesCommand(client, server), new DisconnectCommand(client, server));
        commandMap = commands.stream()
                        .collect(Collectors.toMap(Command::getCommandName, Function.identity()));
    }

    @Override
    public ConnectionState getFactoryState() {
        return ConnectionState.MENU;
    }
}
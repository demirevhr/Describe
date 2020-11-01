package describe.commands.factories.ingame;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.commands.UnknownCommand;
import describe.commands.factories.CommandFactory;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InGameCommandFactory extends CommandFactory {
    public InGameCommandFactory(DescribeClientData client, DescribeServer server) {
        Collection<Command> commands = Arrays
                .asList(new SelectWordDifficultyCommand(client, server), new StartDescribingCommand(client, server),
                        new EndTurnCommand(client, server));
        commandMap = commands.stream()
                .collect(Collectors.toMap(Command::getCommandName, Function.identity()));
    }

    @Override
    public Command getCommand(String commandName) {
        if (commandName.equals("yes") || commandName.equals("no")) {
            return commandMap.get("end-turn");
        } else {
            return commandMap.getOrDefault(commandName, new UnknownCommand());
        }
    }

    @Override
    public ConnectionState getFactoryState() {
        return ConnectionState.YOUR_TURN;
    }
}
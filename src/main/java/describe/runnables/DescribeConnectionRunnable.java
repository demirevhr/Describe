package describe.runnables;

import describe.client.DescribeClientData;
import describe.commands.factories.awaitinggamestart.AwaitingGameStartCommandFactory;
import describe.commands.factories.ingame.InGameCommandFactory;
import describe.commands.factories.lobby.LobbyCommandFactory;
import describe.commands.factories.menu.MenuCommandFactory;
import describe.enums.ConnectionState;
import describe.exceptions.commands.CommandFailureException;
import describe.commands.Command;
import describe.commands.factories.CommandFactory;
import describe.server.DescribeServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DescribeConnectionRunnable implements Runnable {
    private DescribeClientData client;
    private Map<ConnectionState, CommandFactory> commandFactoriesMap;

    public DescribeConnectionRunnable(DescribeClientData client, DescribeServer server) {
        this.client = client;
        Collection<CommandFactory> factories = Arrays
                .asList(new MenuCommandFactory(client, server), new LobbyCommandFactory(client, server),
                        new InGameCommandFactory(client, server), new AwaitingGameStartCommandFactory(client, server));
        commandFactoriesMap = factories.stream()
                .collect(Collectors.toMap(CommandFactory::getFactoryState, Function.identity()));
    }

    @Override
    public void run() {
        try {
            String commandLine;
            while ((commandLine = client.receive()) != null) {
                if (!client.isConnected() || client.isClosed()) {
                    break;
                }
                if (client.getCurrentState() == ConnectionState.NOT_YOUR_TURN) {
                    continue;
                }
                System.out.println("Command received " + commandLine);

                CommandFactory commandFactory = commandFactoriesMap.get(client.getCurrentState());
                String[] tokens = commandLine.split("\\s+");
                String commandName = tokens[0];

                Command command = commandFactory.getCommand(commandName);
                try {
                    command.execute(commandLine);
                    client.setCurrentState(command.getStateAfterCompletion());
                } catch (CommandFailureException e) {
                    client.send(e.getMessage());
                }
            }

            client.closeConnections();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
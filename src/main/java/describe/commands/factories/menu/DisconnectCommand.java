package describe.commands.factories.menu;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;

public class DisconnectCommand extends Command {
    private static final String COMMAND_NAME = "disconnect";
    static final String USER_NOT_CONNECTED_MESSAGE = "Cannot disconnect, user %s is not connected";
    static final String SUCCESSFULLY_DISCONNECTED_MESSAGE = "User %s has disconnected";
    static final String DISCONNECTING_MESSAGE = "Disconnecting from server";

    public DisconnectCommand(DescribeClientData client, DescribeServer server) {
        super(client, server);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() {
        return ConnectionState.MENU;
    }

    @Override
    public void execute(String commandLine) {
        String username = client.getUsername();
        if (!client.isConnected()) {
            System.out.printf((USER_NOT_CONNECTED_MESSAGE) + "%n", username);
            return;
        }

        client.send(DISCONNECTING_MESSAGE);
        client.closeConnections();
        System.out.printf((SUCCESSFULLY_DISCONNECTED_MESSAGE) + "%n", username);
        server.removePlayer(username);
    }
}
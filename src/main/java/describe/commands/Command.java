package describe.commands;

import describe.client.DescribeClientData;
import describe.enums.ConnectionState;
import describe.server.DescribeServer;
 public abstract class Command {
    protected DescribeServer server;
    protected DescribeClientData client;

    Command() {}

    public Command(DescribeClientData client, DescribeServer server) {
        this.client = client;
        this.server = server;
    }

    public abstract void execute(String commandLine);
    public abstract String getCommandName();
    public abstract ConnectionState getStateAfterCompletion();
}
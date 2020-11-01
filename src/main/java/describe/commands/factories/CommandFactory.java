package describe.commands.factories;

import describe.commands.*;
import describe.enums.ConnectionState;
import java.util.Map;

public abstract class CommandFactory {
    protected Map<String, Command> commandMap;
    public Command getCommand(String commandName) { return commandMap.getOrDefault(commandName, new UnknownCommand()); }
    public abstract ConnectionState getFactoryState();
}
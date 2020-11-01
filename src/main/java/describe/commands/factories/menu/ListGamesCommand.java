package describe.commands.factories.menu;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.exceptions.commands.IllegalCommandException;
import describe.game.Game;
import describe.server.DescribeServer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListGamesCommand extends Command {
    private static final String COMMAND_NAME = "list-games";
    private static final String LIST_GAMES_REGEX = String.format("%s(\\s)*", COMMAND_NAME);
    private static final Pattern LIST_GAMES_PATTERN = Pattern.compile(LIST_GAMES_REGEX);

    private static final int ACTIVE_GAMES_LIST_TEMPLATE_ROWS = 3;
    private static final int ACTIVE_GAMES_LIST_GAME_ROW_INDEX = 2;
    private static final String[] ACTIVE_GAMES_LIST_TEMPLATE = new String[ACTIVE_GAMES_LIST_TEMPLATE_ROWS];

    private static final String WRONG_ARGUMENTS_MESSAGE = "Too many arguments for command list-games." +
            "\nUsage : list-games";
    static {
        initActiveGameListTemplate();
    }

    ListGamesCommand(DescribeClientData client, DescribeServer server) {
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
        if (!canParse(commandLine)) {
            throw new IllegalCommandException(WRONG_ARGUMENTS_MESSAGE);
        }
        printActiveGamesList();
    }

    private boolean canParse(String commandLine) {
        Matcher matcher = LIST_GAMES_PATTERN.matcher(commandLine);
        return matcher.matches();
    }

    private void printActiveGamesList() {
        for (int i = 0; i < ACTIVE_GAMES_LIST_GAME_ROW_INDEX; i++) {
            client.send(ACTIVE_GAMES_LIST_TEMPLATE[i]);
        }

        for (Game activeGame : server.getListOfActiveGames()) {
            String formattedGameInfoRow = String.format(
                    ACTIVE_GAMES_LIST_TEMPLATE[ACTIVE_GAMES_LIST_GAME_ROW_INDEX], activeGame.getName(),
                    activeGame.getCreator(), activeGame.getState(), activeGame.getNumberOfPlayers());
            client.send(formattedGameInfoRow);
        }
    }

    private static void initActiveGameListTemplate() {
        ACTIVE_GAMES_LIST_TEMPLATE[0] = "|      NAME      |     CREATOR     |    STATUS     | PLAYERS |";
        ACTIVE_GAMES_LIST_TEMPLATE[1] = "|----------------+-----------------+---------------+---------|";
        ACTIVE_GAMES_LIST_TEMPLATE[2] = "| %-" + Game.MAXIMUM_GAME_NAME_LENGTH + "s| %-" +
                Game.MAXIMUM_PLAYER_NAME_LENGTH + "s| %-" + Game.MAXIMUM_GAME_STATUS_LENGTH + "s| %d/" +
                Game.MAX_PLAYERS + "     |";
    }
}
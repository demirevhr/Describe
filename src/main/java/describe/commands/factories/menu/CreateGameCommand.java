package describe.commands.factories.menu;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.exceptions.commands.CommandFailureException;
import describe.exceptions.commands.GameAlreadyExistsException;
import describe.exceptions.commands.IllegalCommandException;
import describe.game.Game;
import describe.server.DescribeServer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static describe.game.Game.MAXIMUM_GAME_NAME_LENGTH;

public class CreateGameCommand extends Command {
    private static final String COMMAND_NAME = "create-game";
    private static final String CREATE_GAME_REGEX = String.format("%s(\\s)+(\\w)+(\\s)*", COMMAND_NAME);
    private static final Pattern CREATE_GAME_PATTERN = Pattern.compile(CREATE_GAME_REGEX);

    private static final String GAME_ALREADY_EXISTS_MESSAGE = "Game with name %s already exits." +
            " Please choose another name for your game.";
    private static final String WRONG_ARGUMENTS_MESSAGE = "Wrong arguments for command create-game\n" +
            "Usage : create-game <game-name>";
    private static final String GAME_NAME_TOO_LONG_MESSAGE =
            "Game name should be no more than " + MAXIMUM_GAME_NAME_LENGTH + " characters";

    public CreateGameCommand(DescribeClientData client, DescribeServer server) {
        super(client, server);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() {
        return ConnectionState.IN_LOBBY;
    }

    @Override
    public void execute(String commandLine) {
        if (!canParse(commandLine)) {
            throw new IllegalCommandException(WRONG_ARGUMENTS_MESSAGE);
        }

        String gameName = getGameName(commandLine);
        addGame(gameName);
        printLobby();
        System.out.println("Game " + gameName + " created");
    }

    private boolean canParse(String commandLine) {
        Matcher matcher = CREATE_GAME_PATTERN.matcher(commandLine);
        return matcher.matches();
    }

    private String getGameName(String commandLine) {
        String[] tokens = commandLine.trim().split("\\s+");
        String gameName = tokens[1];
        if (gameName.length() > MAXIMUM_GAME_NAME_LENGTH) {
            throw new CommandFailureException(GAME_NAME_TOO_LONG_MESSAGE);
        }
        return gameName;
    }

    private void addGame(String gameName) {
        if (server.containsGame(gameName)) {
            throw new GameAlreadyExistsException(String.format(GAME_ALREADY_EXISTS_MESSAGE, gameName));
        }

        Game newGame = new Game(gameName, client, server);
        server.addGame(newGame);
    }

    private void printLobby() {
        String[] lobby;
        lobby = client.getCurrentGame().getLobby();
        for (String line : lobby) {
            client.send(line);
        }
    }
}
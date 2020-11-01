package describe.commands.factories.menu;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.enums.GameState;
import describe.exceptions.commands.GameAlreadyStartedException;
import describe.exceptions.commands.GameFullException;
import describe.exceptions.commands.NoSuchGameException;
import describe.exceptions.commands.IllegalCommandException;
import describe.game.Game;
import describe.server.DescribeServer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinGameCommand extends Command {
    private static final String COMMAND_NAME = "join-game";
    private static final String JOIN_GAME_REGEX = String.format("%s((\\s)+(\\w)+)?(\\s)*", COMMAND_NAME);
    private static final Pattern JOIN_GAME_PATTERN = Pattern.compile(JOIN_GAME_REGEX);

    private static final String GAME_FULL_MESSAGE = "Cannot join %s : game is full";
    private static final String GAME_STARTED_MESSAGE = "Cannot join %s : game has been started";
    private static final String NO_AVAILABLE_GAMES_MESSAGE = "There are no available games you can join right now.";
    private static final String NO_SUCH_GAME_MESSAGE = "Game %s does not exist.";
    private static final String WRONG_ARGUMENTS_MESSAGE = "Wrong arguments for command join-game\n" +
            "Usage : join-game [<game-name>]";

    JoinGameCommand(DescribeClientData client, DescribeServer server) {
        super(client, server);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() { return ConnectionState.IN_LOBBY; }

    @Override
    public void execute(String commandLine) {
        if (!canParse(commandLine)) {
            throw new IllegalCommandException(WRONG_ARGUMENTS_MESSAGE);
        }

        if (isRandomGame(commandLine)) {
            Game gameToJoin = server.getRandomPendingGame();
            if (gameToJoin == null) {
                throw new NoSuchGameException(NO_AVAILABLE_GAMES_MESSAGE);
            } else {
                joinGame(gameToJoin);
            }
        } else {
            String gameName = commandLine.split("\\s+")[1];
            joinGame(server.getGame(gameName));
        }
    }

    private boolean canParse(String commandLine) {
        Matcher matcher = JOIN_GAME_PATTERN.matcher(commandLine);
        return matcher.matches();
    }


    private boolean isRandomGame(String commandLine) {
        String[] tokens = commandLine.split("\\s+");
        return tokens.length == 1;
    }

    private void joinGame(Game gameToJoin) {
        if (gameToJoin != null) {
            if (gameToJoin.isFull()) {
                throw new GameFullException(String.format(GAME_FULL_MESSAGE, gameToJoin.getName()));
            }
            if (gameToJoin.getState() == GameState.IN_PROGRESS) {
                throw new GameAlreadyStartedException(String.format(GAME_STARTED_MESSAGE, gameToJoin.getName()));
            }

            gameToJoin.join(client);
        } else {
            String message = String.format(NO_SUCH_GAME_MESSAGE, gameToJoin.getName());
            throw new NoSuchGameException(message);
        }
        updateLobby(gameToJoin);

    }

    private void updateLobby(Game currentGame) {
        client.send("Joined game " + currentGame.getName());

        String[] lobby = currentGame.getLobby();
        for (String line : lobby) {
            currentGame.printToEachPlayer(line);
        }

        currentGame.printToEachPlayer(currentGame.getReadyToStartMessage());
    }
}
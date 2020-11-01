package describe.game;

import describe.client.DescribeClientData;
import describe.enums.ConnectionState;
import describe.enums.GameState;
import describe.server.DescribeServer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Game {
    public static final int MAXIMUM_PLAYER_NAME_LENGTH = 16;
    public static final int MAXIMUM_GAME_NAME_LENGTH = 15;
    public static final int MAXIMUM_GAME_STATUS_LENGTH = 14;
    public static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;
    private static final int IN_GAME_LOBBY_TEMPLATE_ROWS = 13;
    private static final int GAME_NAME_ROW = 6;
    private static final int PLAYER_ROW = 8;
    private static final String[] IN_GAME_LOBBY_TEMPLATE = new String[IN_GAME_LOBBY_TEMPLATE_ROWS];
    private static final char[] PLAYER_SYMBOLS = {'#', '@', '*', '$'};
    private static final String READY_TO_START_MESSAGE = "Type \"start\" to start the game";
    private static final String IT_IS_YOUR_TURN_MESSAGE = "It's your turn, %s! (%c)\n" + "[type \"p N\" (where N is 3, 4 or 5 points) ]\nChoose points :";
    private static final int SECONDS_PER_ROUND_SHORT = 60;
    private static final int SECONDS_PER_ROUND_LONG = 120;

    private Sound timerSound = new Sound ("ticking-clock.wav");
    private Sound victorySound = new Sound("victory.wav");

    static {
        initLobby();
    }

    private final String name;
    private final String creator;
    private GameState state;
    private Board board;

    private List<DescribeClientData> playerConnections;

    private int activePlayerIndex;
    private DescribeClientData activePlayer;
    private int secondsForThisRound;

    private DescribeServer server;

    public Game(String name, DescribeClientData creatorConnection, DescribeServer server) {
        this.name = name;
        this.creator = creatorConnection.getUsername();
        this.state = GameState.PENDING;

        this.server = server;

        playerConnections = new ArrayList<>();
        join(creatorConnection);

        board = new Board();
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public synchronized GameState getState() { return state; }

    public synchronized boolean isFull() { return playerConnections.size() == MAX_PLAYERS; }

    public synchronized int getNumberOfPlayers() { return playerConnections.size(); }

    public String getReadyToStartMessage() {
        return READY_TO_START_MESSAGE;
    }

    public synchronized List<String> getPlayerNames() {
        List<String> result = new ArrayList<>();

        for(var player : playerConnections) {
            result.add(player.getUsername());
        }
        return result;
    }

    public synchronized void printToEachPlayer(String message) {
        for(DescribeClientData client : playerConnections) {
            client.send(message);
        }
    }

    public synchronized void join(DescribeClientData playerConnection) {
        playerConnection.setCurrentGame(this);
        playerConnection.setPawn(PLAYER_SYMBOLS[playerConnections.size()]);
        playerConnections.add(playerConnection);
    }

    public synchronized void enterGameQueue(DescribeClientData client) {
        client.setCurrentState(ConnectionState.AWAITING_GAME_START);

        if (areAllPlayersReady()) {
            startGame();
        }
    }

    private synchronized boolean areAllPlayersReady() {
        if (getNumberOfPlayers() < MIN_PLAYERS) {
            return false;
        }

        for (var player : playerConnections) {
            if (player.getCurrentState() != ConnectionState.AWAITING_GAME_START) {
                return false;
            }
        }
        return true;
    }

    private synchronized void startGame() {
        state = GameState.IN_PROGRESS;
        updateBoard();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        activePlayerIndex = random.nextInt(getNumberOfPlayers());
        activePlayer = playerConnections.get(activePlayerIndex);

        for (int i = 0; i < getNumberOfPlayers(); i++) {
            if (i == activePlayerIndex) {
                activePlayer.setCurrentState(ConnectionState.YOUR_TURN);
                activePlayer.send(String.format(IT_IS_YOUR_TURN_MESSAGE, activePlayer.getUsername(), activePlayer.getPawn()));
            } else {
                playerConnections.get(i).setCurrentState(ConnectionState.NOT_YOUR_TURN);
            }
        }
    }

    public synchronized void updateBoard() {
        String[] gameBoard = board.getBoard();
        for (String s : gameBoard) {
            printToEachPlayer(s);
        }
    }

    public synchronized void play() {
        activePlayer.send("Your word is : " + activePlayer.getCurrentWord() +
                "\nDescribe it by " + activePlayer.getDescribeMethodForCurrentField());
        startDescribing();
    }


    private synchronized void startDescribing() {
        String currentMethod= activePlayer.getDescribeMethodForCurrentField();
        if (currentMethod.equals(Board.ASSOCIATIONS) || currentMethod.equals(Board.DRAWING)) {
            secondsForThisRound = SECONDS_PER_ROUND_LONG;
            timerSound.play();
        } else {
            secondsForThisRound = SECONDS_PER_ROUND_SHORT;
        }
        new Timer().schedule(new TimerTask(){
            int remainingSeconds = secondsForThisRound;
            @Override
            public void run() {
                if (remainingSeconds > 0) {
                    if (remainingSeconds == 60) {
                        if (timerSound.isRunning()) {
                            timerSound.stop();
                        }
                        timerSound.play();
                    }
                    printToEachPlayer("Time remaining : " + remainingSeconds-- + " seconds. \r");
                } else if (remainingSeconds == 0) {
                    finishDescribing();
                    cancel();
                }
            }
        },0, 1000);
    }

    private synchronized void finishDescribing() {
        printToEachPlayer("Round over");
        activePlayer.send("Did your team guess correctly?\nType \"yes\" or \"no\"");
    }

    public synchronized void endTurn(boolean hasGuessed) {
        if (hasGuessed) {
            moveActivePlayerForward();
        } else {
            activePlayer.send("Too bad! You'll get it next time!");
        }
        if (state != GameState.GAME_OVER) {
            nextPlayerTurn();
        }
    }

    private synchronized void moveActivePlayerForward() {
        char pawn = activePlayer.getPawn();
        int position = activePlayer.getPosition();
        int movesForward = activePlayer.getPointsForCurrentWord();
        int newPosition = position + movesForward;
        board.movePlayerForward(pawn, position, movesForward);
        if (newPosition >= Board.NUMBER_OF_MOVES - 1) {
            gameOver();
            return;
        }
        activePlayer.setPosition(newPosition);
        activePlayer.setDescribeMethodForCurrentField(Board.getDescribingMethod(newPosition));
    }

    private synchronized void nextPlayerTurn() {
        activePlayer.setCurrentState(ConnectionState.NOT_YOUR_TURN);

        activePlayerIndex = ++activePlayerIndex % getNumberOfPlayers();
        activePlayer = playerConnections.get(activePlayerIndex);
        activePlayer.setCurrentState(ConnectionState.YOUR_TURN);

        updateBoard();
        activePlayer.send(String.format(IT_IS_YOUR_TURN_MESSAGE, activePlayer.getUsername(), activePlayer.getPawn()));
    }

    private synchronized void gameOver() {
        victorySound.play();

        printToEachPlayer("\n-------------GAME OVER-------------\n\n       The winner is " + activePlayer.getUsername() + "!\n\n");

        state = GameState.GAME_OVER;
        for (var player : playerConnections) {
            player.setCurrentGame(null);
            player.setCurrentState(ConnectionState.MENU);
            server.printMenu(player);
        }

        server.removeGame(name);
    }

    private static void initLobby() {
        IN_GAME_LOBBY_TEMPLATE[0] = " |||=================================|||";
        IN_GAME_LOBBY_TEMPLATE[1] = "  |||===============================|||";
        IN_GAME_LOBBY_TEMPLATE[2] = "   |||====== D E S C R I B E ======|||";
        IN_GAME_LOBBY_TEMPLATE[3] = "    |||===========================|||";
        IN_GAME_LOBBY_TEMPLATE[4] = "     |||=========================|||";
        IN_GAME_LOBBY_TEMPLATE[5] = "=========================================";
        IN_GAME_LOBBY_TEMPLATE[6] = "=============%-15s=============";
        IN_GAME_LOBBY_TEMPLATE[7] = "=========================================";
        IN_GAME_LOBBY_TEMPLATE[8] = "||%d.| %-16s                ||";
    }

    public synchronized String[] getLobby() {
        int inGameLobbySize = PLAYER_ROW + playerConnections.size();
        String[] inGameLobby = new String[inGameLobbySize];
        int rowIndex;
        for (rowIndex = 0; rowIndex < GAME_NAME_ROW; rowIndex++) {
            inGameLobby[rowIndex] = IN_GAME_LOBBY_TEMPLATE[rowIndex];
        }

        inGameLobby[rowIndex++] = String.format(IN_GAME_LOBBY_TEMPLATE[GAME_NAME_ROW], name);

        for (; rowIndex < PLAYER_ROW; rowIndex++) {
            inGameLobby[rowIndex] = IN_GAME_LOBBY_TEMPLATE[rowIndex];
        }

        int playerNumber = 1;
        for(var player : playerConnections) {
            inGameLobby[rowIndex++] = String.format(IN_GAME_LOBBY_TEMPLATE[PLAYER_ROW],
                    playerNumber++, player.getUsername());
        }
        return inGameLobby;
    }
}
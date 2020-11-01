package describe.client;

import describe.enums.ConnectionState;
import describe.game.Game;
import describe.socketaction.SocketAction;

import java.net.Socket;

public class DescribeClientData extends SocketAction {
    private Game currentGame;

    private final String username;
    private ConnectionState currentState;

    private String describeMethodForCurrentField = "a method of your choosing : speaking, using word associations, drawing, or using gestures";
    private int position = 0;
    private String currentWord;
    private int pointsForCurrentWord;
    private char pawn;

    public DescribeClientData(Socket socket, String username) {
        super(socket);
        this.username = username;
        this.currentState = ConnectionState.MENU;
    }

    public String getUsername() {
        return username;
    }

    public ConnectionState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ConnectionState currentState) {
        this.currentState = currentState;
    }

    public char getPawn() {
        return pawn;
    }

    public void setPawn(char pawn) {
        this.pawn = pawn;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        currentGame = game;
    }

    public String getDescribeMethodForCurrentField() { return describeMethodForCurrentField; }

    public void setDescribeMethodForCurrentField(String method) { describeMethodForCurrentField = method; }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public int getPointsForCurrentWord() {
        return pointsForCurrentWord;
    }

    public void setPointsForCurrentWord(int pointsForCurrentWord) {
        this.pointsForCurrentWord = pointsForCurrentWord;
    }
}
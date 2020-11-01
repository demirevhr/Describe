package describe.enums;

public enum ConnectionState {
    MENU("menu"),
    IN_LOBBY("in lobby"),
    AWAITING_GAME_START("awaiting game start"),
    YOUR_TURN("your turn"),
    NOT_YOUR_TURN("not your turn");

    String state;

    ConnectionState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}
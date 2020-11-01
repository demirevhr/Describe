package describe.enums;

public enum GameState {
    PENDING("pending"),
    AWAITING_START("awaiting start"),
    IN_PROGRESS("in progress"),
    GAME_OVER("game over");

    String state;

    GameState(String state) {this.state = state; }

    @Override
    public String toString() { return state; }
}
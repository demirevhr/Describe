package describe.game;

public class Board {
    public static final int NUMBER_OF_MOVES = 59;
    private static final int ROWS = 19;
    private static final int COLUMN_OF_FIRST_FIELD = 3;
    private static final int NEXT_SPACE_TO_THE_RIGHT = 4;
    private static final int FIRST_ROW_OF_FIELDS = 1;
    private static final int LAST_ROW_OF_FIELDS = 17;
    private static final int DOWN_AND_UPS = 2;
    private static final char EMPTY_FIELD = ' ';
    private static final String[] INITIAL_BOARD = new String[ROWS];
    private static final Field[] MOVES = new Field[NUMBER_OF_MOVES];
    public static final String DRAWING = "drawing";
    public static final String ASSOCIATIONS = "word associations";
    public static final String SPEAKING = "speaking";
    public static final String GESTURES = "using gestures";
    private static final String[] DESCRIBING_METHODS_VALUES = {DRAWING, ASSOCIATIONS, SPEAKING, GESTURES};
    private static final String[] DESCRIBING_METHODS = new String[NUMBER_OF_MOVES];

    static {
        setInitialBoard();
        setSpaceCoordinates();
        setDescribingMethods();
    }

    private String[] board;

    public Board() {
        board = new String[ROWS];
        if (ROWS >= 0) System.arraycopy(INITIAL_BOARD, 0, board, 0, ROWS);
    }

    public String[] getBoard() {
        return board;
    }

    public void movePlayerForward(char pawn, int position, int forwardMoves) {
        Field from = MOVES[position];
        int positionTo = (position + forwardMoves >= NUMBER_OF_MOVES) ? 58 : position + forwardMoves;
        Field to = MOVES[positionTo];

        removePawnFromCurrentField(pawn, from);
        movePawnToNextField(pawn, to);
    }

    private void removePawnFromCurrentField(char pawn, Field from) {
        String moveFromRow = board[from.getRow()];

        if (moveFromRow.charAt(from.getColumn()) != pawn) {
            moveFromRow = board[from.getRow() + 1];
            if (moveFromRow.charAt(from.getColumn()) == pawn) {
                board[from.getRow() + 1] = moveFromRow.substring(0, from.getColumn()) + '_' + moveFromRow.substring(from.getColumn() + 1);
            } else {
                board[from.getRow() + 1] = moveFromRow.substring(0, from.getColumn() - 1) + '_' + moveFromRow.substring(from.getColumn());
            }
        } else {
            board[from.getRow()] = moveFromRow.substring(0, from.getColumn()) + EMPTY_FIELD + moveFromRow.substring(from.getColumn() + 1);
        }
    }

    private void movePawnToNextField(char pawn, Field to) {
        String moveTo = board[to.getRow()];

        if (moveTo.charAt(to.getColumn()) != EMPTY_FIELD) {
            moveTo = board[to.getRow() + 1];
            if (moveTo.charAt(to.getColumn()) != '_') {
                board[to.getRow() + 1] = moveTo.substring(0, to.getColumn() - 1) + pawn + moveTo.substring(to.getColumn());
            } else {
                board[to.getRow() + 1] = moveTo.substring(0, to.getColumn()) + pawn + moveTo.substring(to.getColumn() + 1);
            }
        } else {
            board[to.getRow()] = moveTo.substring(0, to.getColumn()) + pawn + moveTo.substring(to.getColumn() + 1);
        }
    }

    private static void setInitialBoard() {
        INITIAL_BOARD[0] =  " ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ ";
        INITIAL_BOARD[1] =  "|D  |A  |S  |   |D  |A  |S  |   |D  |A  |S  |";
        INITIAL_BOARD[2] =  "|___|___|___|___|___|___|___|___|___|___|___|";
        INITIAL_BOARD[3] =  "|G  |   |G  |   |G  |   |G  |   |G  |   |G  |";
        INITIAL_BOARD[4] =  "|___|___|___|___|___|___|___|___|___|___|___|";
        INITIAL_BOARD[5] =  "|S  |   |D  |   |S  |   |D  |   |S  |   |D  |";
        INITIAL_BOARD[6] =  "|___|___|___|___|___|___|___|___|___|___|___|";
        INITIAL_BOARD[7] =  "|A  |   |A  |   |A  |   |A  |   |A  |   |A  |";
        INITIAL_BOARD[8] =  "|___|___|___|___|___|___|___|___|___|___|___|";
        INITIAL_BOARD[9] =  "|D  |   |S  |   |D  |   |S  |   |D  |   |S  |";
        INITIAL_BOARD[10] = "|___|___|___|___|___|___|___|___|___|___|___|";
        INITIAL_BOARD[11] = "|G  |   |G  |   |G  |   |G  |   |G  |   |G  |";
        INITIAL_BOARD[12] = "|___|___|___|___|___|___|___|___|___|___|___|";
        INITIAL_BOARD[13] = "|   |   |D  |   |S  |   |D  |   |S  |   |D  |";
        INITIAL_BOARD[14] = "|___|___|___|___|___|___|___|___|___|___|___|";
        INITIAL_BOARD[15] = "|   |   |A  |   |A  |   |A  |   |A  |   |A  |";
        INITIAL_BOARD[16] = "|___|___|___|___|___|___|___|___|___|___|___|";
        INITIAL_BOARD[17] = "|   |   |S  |G  |D  |   |S  |G  |D  |   |FIN|";
        INITIAL_BOARD[18] = "|___|___|___|___|___|___|___|___|___|___|___|";
    }

    private static void setDescribingMethods() {
        int values = DESCRIBING_METHODS_VALUES.length;
        for (int i = 0; i < NUMBER_OF_MOVES; i++) {
            DESCRIBING_METHODS[i] = DESCRIBING_METHODS_VALUES[(i % values)];
        }
    }

    public static String getDescribingMethod(int position) {
        return DESCRIBING_METHODS[position];
    }

    // Hardcoded initialisation of every possible move
    private static void setSpaceCoordinates() {
        int moveCount = 0;
        int row = LAST_ROW_OF_FIELDS;
        int column = COLUMN_OF_FIRST_FIELD;
        moveCount = setCoordinatesForColumn(row, column, true, moveCount);

        for (int i = 0; i < DOWN_AND_UPS; i++) {
            moveCount = setRightAndDown(column, moveCount);
            column += 2 * NEXT_SPACE_TO_THE_RIGHT;

            moveCount = setRightAndUp(column, moveCount);
            column += 2 * NEXT_SPACE_TO_THE_RIGHT;
        }

        moveCount = setRightAndDown(column, moveCount);

        assert(moveCount == NUMBER_OF_MOVES);
    }

    private static int setCoordinatesForColumn(int startingRow, int column, boolean isUp, int moveCount) {
        int increment;
        int lastRow;
        if (isUp) {
            increment = -2;
            lastRow = 1 + increment;
        } else {
            increment = 2;
            lastRow = 17 + increment;
        }

        for (; startingRow != lastRow; startingRow += increment) {
            MOVES[moveCount++] = new Field(startingRow, column);
        }
        return moveCount;
    }

    private static int setCoordinatesForSingleSpace(int row, int column, int moveCount) {
        MOVES[moveCount++] = new Field(row, column);
        return moveCount;
    }

    private static int setRightAndDown(int column, int moveCount) {
        int row = FIRST_ROW_OF_FIELDS;
        moveCount = setCoordinatesForSingleSpace(row, column + NEXT_SPACE_TO_THE_RIGHT, moveCount);
        return setCoordinatesForColumn(row, column + 2*NEXT_SPACE_TO_THE_RIGHT, false, moveCount);
    }

    private static int setRightAndUp(int column, int moveCount) {
        int row = LAST_ROW_OF_FIELDS;
        moveCount = setCoordinatesForSingleSpace(row, column + NEXT_SPACE_TO_THE_RIGHT, moveCount);
        return setCoordinatesForColumn(row, column + 2*NEXT_SPACE_TO_THE_RIGHT, true, moveCount);
    }
}
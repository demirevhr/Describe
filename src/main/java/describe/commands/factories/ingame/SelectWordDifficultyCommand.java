package describe.commands.factories.ingame;

import describe.client.DescribeClientData;
import describe.commands.Command;
import describe.enums.ConnectionState;
import describe.exceptions.commands.IllegalCommandException;
import describe.exceptions.commands.WordPointsOutOfRangeException;
import describe.server.DescribeServer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectWordDifficultyCommand extends Command {
    private static final String COMMAND_NAME = "p";
    private static final String SELECT_DIFFICULTY_REGEX = String.format("%s(\\s)+(\\d)(\\s)*", COMMAND_NAME);
    private static final Pattern SELECT_DIFFICULTY_PATTERN = Pattern.compile(SELECT_DIFFICULTY_REGEX);

    private static final String WRONG_ARGUMENTS_MESSAGE = "Wrong arguments to select points for next word\n" +
            "Usage : p N (where N is 3, 4 or 5 points)";
    private static final String WORD_POINTS_WRONG_MESSAGE = "Wrong number of points.\n Please, choose a word for 3, 4 or 5 points";

    SelectWordDifficultyCommand(DescribeClientData client, DescribeServer server) {
        super(client, server);
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public ConnectionState getStateAfterCompletion() {
        return ConnectionState.YOUR_TURN;
    }

    @Override
    public void execute(String commandLine) {
        if (!canParse(commandLine)) {
            throw new IllegalCommandException(WRONG_ARGUMENTS_MESSAGE);
        }

        int points = Integer.parseInt(commandLine.split("\\s")[1]);
        if (points < 3 || points > 5) {
            throw new WordPointsOutOfRangeException(WORD_POINTS_WRONG_MESSAGE);
        }

        client.send("You chose a word for " + points + " points");
        String currentWord = server.getWord(points);

        client.setPointsForCurrentWord(points);
        client.setCurrentWord(currentWord);

        client.send("You got the word : " + currentWord +
                "\nDescribe it by " + client.getDescribeMethodForCurrentField() +
                "\nType \"go\" when you are ready to Describe!");
    }

    private boolean canParse(String commandLine) {
        Matcher matcher = SELECT_DIFFICULTY_PATTERN.matcher(commandLine);
        return matcher.matches();
    }
}
package describe.server;

import describe.client.DescribeClientData;
import describe.enums.GameState;
import describe.exceptions.ClientConnectionFailException;
import describe.exceptions.GameDoesNotExistException;
import describe.game.Game;
import describe.runnables.DescribeConnectionRunnable;
import describe.socketaction.SocketAction;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DescribeServer {
    private ServerSocket serverSocket;

    private Map<String, DescribeClientData> users = new ConcurrentHashMap<>();
    private Map<String, Game> allGames = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private String HOST = "localhost";
    private int PORT = 4444;

    private final Path EASY_WORDS_FILEPATH = Path.of("easy.txt");
    private final Path MEDIUM_WORDS_FILEPATH = Path.of("medium.txt");
    private final Path HARD_WORDS_FILEPATH = Path.of("hard.txt");

    private Map<Integer, List<String>> wordListsMap = new HashMap<>();
    private Random random = ThreadLocalRandom.current();

    private static final String MENU_TEXT = "Welcome to Describe!\nHere are the available commands :" + System.lineSeparator() +
            "create-game <game-name> - create a game with name \"game-name\"" + System.lineSeparator() +
            "join-game [<game-name>] - join a game with a given name or join a random game" + System.lineSeparator() +
            "list-games              - get a list of pending games" + System.lineSeparator() +
            "disconnect              - disconnect from server";

    private DescribeServer(String hostName, int port) {
        HOST = hostName;
        PORT = port;
        try {
            serverSocket = new ServerSocket(port, 0, InetAddress.getByName(hostName));
            initWordLists();
        }
        catch (IOException e) {
            System.out.println("Problem while creating server. Couldn't access port " + port + ": " + e);
            System.exit(1);
        }
    }

    private DescribeServer() {
        new DescribeServer(HOST, PORT);
    }

    private void initWordLists() {
        List<String> EASY_WORDS_LIST = readWordList(EASY_WORDS_FILEPATH);
        List<String> MEDIUM_WORDS_LIST = readWordList(MEDIUM_WORDS_FILEPATH);
        List<String> HARD_WORDS_LIST = readWordList(HARD_WORDS_FILEPATH);

        wordListsMap.put(3, EASY_WORDS_LIST);
        wordListsMap.put(4, MEDIUM_WORDS_LIST);
        wordListsMap.put(5, HARD_WORDS_LIST);
    }

    private List<String> readWordList(Path filePath) {
        try (Stream<String> stream = Files.lines(filePath)) {
            return stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            new DescribeServer().start();
        } else {
            String hostName = args[0];
            int port = -1;
            if (args[1].matches("\\d+")) {
                port = Integer.parseInt(args[1]);
            } else {
                System.out.println("Error : check if port is available");
                System.exit(1);
            }
            new DescribeServer(hostName, port);
        }
    }

    public void start() {
        System.out.printf(serverSocket.getInetAddress() +
                "\nServer is running on %s:%d%n", HOST, PORT);
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectClient(clientSocket);
            }
        } catch (IOException e) {
            throw new ClientConnectionFailException("Error connecting to server on " + HOST + ":" + PORT);
        }
    }

    private synchronized void connectClient(Socket socket) throws IOException {
        SocketAction clientConnectionCheck = new SocketAction(socket);
        String username = clientConnectionCheck.receive();
        if (users.containsKey(username) && users.get(username).isConnected()) {
            clientConnectionCheck.send("User <" + username + "> is already online");
            System.err.println("User <" + username + "> is already online");
            return;
        }

        var clientData = new DescribeClientData(socket, username);
        users.put(username, clientData);

        System.out.println(username + " has connected");
        clientData.send("Connected to server " + HOST + ":" + PORT);
        clientData.send(MENU_TEXT);

        executor.execute(new DescribeConnectionRunnable(clientData, this));
    }

    public void addGame(Game newGame) {
        allGames.put(newGame.getName(), newGame);
    }
    public Game getGame(String key) {
        return allGames.get(key);
    }

    public Game getRandomPendingGame() {
        for (Map.Entry<String, Game> entry : allGames.entrySet()) {
            String name = entry.getKey();
            Game game = entry.getValue();
            if (!game.isFull() && game.getState() == GameState.PENDING) {
                return getGame(name);
            }
        }
        return null;
    }

    public void removePlayer(String username) {
        users.remove(username);
    }

    public boolean containsGame(String gameName) {
        return allGames.containsKey(gameName);
    }

    public void removeGame(String gameName) {
        Game gameToRemove = allGames.get(gameName);
        if (gameToRemove == null) {
            throw new GameDoesNotExistException();
        }

        allGames.remove(gameName);
    }

    public List<Game> getListOfActiveGames() {
        return new ArrayList<>(allGames.values());
    }

    public String getWord(int points) {
        return getRandomWord(wordListsMap.get(points));
    }

    public String getRandomWord(List<String> wordList) {
        return wordList.get(random.nextInt(wordList.size()));
    }

    public synchronized void printMenu(DescribeClientData player) {
        player.send(MENU_TEXT);
    }
}
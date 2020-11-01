package describe.client;

import describe.exceptions.UserAlreadyOnlineException;
import describe.runnables.DescribeServerResponseListener;
import describe.socketaction.SocketAction;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class DescribeClient extends SocketAction {
    private DescribeClient(Socket socket, String username) {
        super(socket);
        send(username);
        try {
            String serverResponse = receive();
            if (serverResponse.equals("User <" + username + "> is already online")) {
                throw new UserAlreadyOnlineException(serverResponse);
            } else {
                new Thread(new DescribeServerResponseListener(socket)).start();
                System.out.println(serverResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java ...\\GameClient <username> <host> <port>");
            System.exit(1);
        }

        String username = args[0];
        String hostName = args[1];
        int port = -1;
        if (args[2].matches("\\d+")) {
            port = Integer.parseInt(args[2]);
        } else {
            System.out.println("Error : check if server is at the right port");
            System.exit(1);
        }

        Socket socket = null;
        try {
            socket = new Socket(hostName, port);
        } catch (IOException e) {
            System.err.printf("Could not connect to server on %s:%d\nPlease try again", hostName, port);
            System.exit(1);
        }

        new DescribeClient(socket, username).start();
    }

    private void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (isClosed()) {
                    return;
                }
                String commandLine = scanner.nextLine();
                send(commandLine);
            }
        }
    }
}
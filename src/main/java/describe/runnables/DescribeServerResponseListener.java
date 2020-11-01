package describe.runnables;

import describe.socketaction.SocketAction;

import java.io.IOException;
import java.net.Socket;

public class DescribeServerResponseListener extends SocketAction implements Runnable {
    public DescribeServerResponseListener(Socket clientSocket) {
        super(clientSocket);
    }

    @Override
    public void run() {
            String serverResponse;
            try {
                while ((serverResponse = receive()) != null) {
                    if (!isConnected() || isClosed()) {
                        System.out.println("Client socket is closed, stop waiting for server messages");
                        return;
                    }

                    System.out.println(serverResponse);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            closeConnections();
    }
}
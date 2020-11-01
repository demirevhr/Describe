package describe.socketaction;

// SocketAction Class
// SocketAction.java

// All code graciously developed by Greg Turner. You have the right
// to reuse this code however you choose. Thanks Greg!

// Refactored by me though
import java.io.*;
import java.net.Socket;

public class SocketAction {
    private  BufferedReader inStream = null;
    protected PrintWriter outStream = null;
    private Socket socket = null;

    public SocketAction(Socket socket) {
        try {
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new PrintWriter(socket.getOutputStream(), true);
            this.socket = socket;
        }
        catch (IOException e) {
            System.out.println("Couldn't initialize SocketAction: " + e);
            System.exit(1);
        }
    }

    public void send(String s) {
        outStream.println(s);
    }

    public String receive() throws IOException {
        return inStream.readLine();
    }

    public void closeConnections() {
        try {
            socket.close();
            socket = null;
        }
        catch (IOException e) {
            System.out.println("Couldn't close socket: " + e);
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public boolean isConnected() {
        return ((inStream != null) && (outStream != null) && (socket != null));
    }
}
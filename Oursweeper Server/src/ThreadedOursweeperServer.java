import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class ThreadedOursweeperServer {
	
    static final int PORT = 8252;

    public static void main(String args[]) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
             serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName("192.168.0.2"));
        	 // serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName("131.179.52.178")); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            new ClientThread(socket).start();
        }
    }
}
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.oracle.webservices.internal.api.EnvelopeStyle.Style;

/*

Assignment for Michelle:

Write the Server Code that relays client's message to other clients with the same teamname. The process is as follows:
	0. Read this entirely and double check with me for your proposed implementation.
	1. Open up port 8252 for multithreaded listening (already implemented in Server.java).
    2. Set up shared data structure among all threads. It should map (String) teamName to List<ClientHandler> clients. 
       Be mindful of your method types to prevent race conditions (Or you can just start over in python).
    3. Upon Receiving complete JSON (as you had learned about TCP, payloads may be broken into multiple packets and you 
       would need to concat strings if JSON is incomplete (hint: it's unparcable)), read the value of "ID" and "PlayerName" 
       Field. These two fields are guranteed in every JSON and it should serve as the key(s) for the data structure(s) 
       you've decided set up in (2). 
	4. "Action" field is guranteed in every JSON:
        - If "Action" field is "EnterRoom", read "TeamName" field, map to "PlayerName" that maps to this (type: ClientHandler), 
          or however you decided to implement (2).
		- If it's "LeaveRoom", read "TeamName" and "PlayerName" and remove the entry correspondingly. Destroy the current thread.
	5. If "Action" field is anything else, simply forward message to all other ClientHandlers that shares the same teamname.
	6. Assume TeamName's are unique, but not PlayerName (Hint: ID with ClientHandler).
*/

class Server {
    public static void main(String argv[]) throws Exception {
        new mineServer(8252); // UCLA
        System.out.print("Server Started");
    }
}

class mineServer extends Thread {
    private ServerSocket server;
    protected List<ClientHandler> clients;

    public mineServer(int port) {
        try {
            this.server = new ServerSocket(port);
            clients = Collections.synchronizedList(new ArrayList<ClientHandler>());
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket client = server.accept();
                System.out.println(client.getInetAddress().getHostName() + " connected");
                ClientHandler newClient = new ClientHandler(client);
                clients.add(newClient);
                new SendMessage(clients);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class ClientHandler {
    protected Socket client;
    protected PrintWriter out;

    public ClientHandler(Socket client) {
        this.client = client;
        try {
            this.out = new PrintWriter(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SendMessage extends Thread {
    protected List<ClientHandler> clients;
    protected String userInput;
    protected BufferedReader console;

    public SendMessage(List<ClientHandler> clients) {
        this.clients = clients;
        this.userInput = null;
        this.start();
    }

    public void run() {
        System.out.println("New Communication Thread Started");
        if (clients.size() == 1) {
            System.out.println("Enter message:");
        }
        try {
            if (clients.size() > 0) {
                this.console = new BufferedReader(new InputStreamReader(System.in));
                while ((this.userInput = console.readLine()) != null) {
                    if (userInput != null & userInput.length() > 0) {
                        for (ClientHandler client : clients) {
                            client.out.println(userInput);
                            client.out.flush();
                            Thread.currentThread();
                            Thread.sleep(1 * 1000);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

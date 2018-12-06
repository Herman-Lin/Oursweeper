import java.net.Socket;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class ClientThread extends Thread {
    protected Socket socket;
    protected String teamName;
    protected JSONObject player;
    protected boolean isGameMaster;
    BlockingQueue<String> queue;

    public ClientThread(Socket serverSocket) {
        this.socket = serverSocket;
        this.teamName = "*";
        this.queue = new LinkedBlockingQueue<String>();
        this.player = null;
        this.isGameMaster = false;
        System.out.println("Connection Established from " + this.socket.getInetAddress().toString() + " (" + String.valueOf(this.socket.getPort()) + ")");
    }

    public void run() {
        InputStream in = null;
        BufferedReader inReader = null;
        PrintWriter outWrite = null;
        try {
            inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outWrite = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String line = "";
        JSONObject clientData = null;
        
        while (true) {
            try {
            	
                if (inReader.ready() && (line = inReader.readLine()) != null) {
                	try { 
                		line = line.replace("\\u0000", "");
                		clientData = new JSONObject(line);
                	} catch (Exception e) {
                		System.out.println(line);
                	}
                	
            		try {
            			if (this.player == null) {
            				this.player = new JSONObject();
            				this.player.put("Color", " ");
            				this.player.put("Name", clientData.get("ID"));
            				if (SocketStorage.getInstance().playerStorage.containsKey(clientData.get("TeamName"))) {
            					SocketStorage.getInstance().playerStorage.get(clientData.get("TeamName")).put(this.player);
            				}
            				else {
            					this.isGameMaster = true;
            					JSONArray playerList = new JSONArray();
            					playerList.put(this.player);
            					SocketStorage.getInstance().playerStorage.put(clientData.get("TeamName").toString(), playerList);
            				}
            			}
                		System.out.println("Recv | " + this.player.get("Name") + " | " + line);
            			if (clientData.get("Action").equals("EnterRoom")) {
            				SocketStorage.getInstance().changeTeamName(this.teamName, clientData.get("TeamName").toString(), this);
            				this.teamName = clientData.get("TeamName").toString();
            				JSONObject initResponse = new JSONObject();
            				initResponse.put("Action", "RoomInfo");
            				initResponse.put("Players", SocketStorage.getInstance().playerStorage.get(clientData.get("TeamName")));
                			System.out.println("Sent | " + this.player.get("Name") + " | " + initResponse.toString());
                			outWrite.println(initResponse.toString());
                			outWrite.flush();
                			SocketStorage.getInstance().sendMsgToAll(this, this.teamName, line);
            			}
            			else if (clientData.get("Action").equals("Color")) {
            				for (int i = 0; i < SocketStorage.getInstance().playerStorage.get(this.teamName).length(); i++) {
            					if (SocketStorage.getInstance().playerStorage.get(this.teamName).getJSONObject(i).get("Name").equals(clientData.get("ID"))) {
            						SocketStorage.getInstance().playerStorage.get(this.teamName).getJSONObject(i).put("Color", clientData.get("Color"));
            						SocketStorage.getInstance().sendMsgToAll(this, this.teamName, line);
            						break;
            					} 
            				}
            			}
            			else if (clientData.get("Action").equals("LeaveRoom")) {
            				for (int i = 0; i < SocketStorage.getInstance().playerStorage.get(this.teamName).length(); i++) {
            					if (SocketStorage.getInstance().playerStorage.get(clientData.get("TeamName")).getJSONObject(i).get("Name").equals(clientData.get("ID"))) {
            						SocketStorage.getInstance().playerStorage.get(clientData.get("TeamName")).remove(i);
            					}
            					break;
            				}
                			SocketStorage.getInstance().sendMsgToAll(this, clientData.getString("TeamName"), line);
            				SocketStorage.getInstance().changeTeamName(clientData.getString("TeamName"), "*", this);
            				this.teamName = "*";
            				this.player = null;
            			}
            			else {
            				SocketStorage.getInstance().sendMsgToAll(this, this.teamName, line);
            			}
            			line = "";
            		} catch (JSONException e) {
            			System.out.println(line);
            			e.printStackTrace();
            		}
            	}
            	else {
            		String msg;
            		while (((msg = queue.poll()) != null)) {
            			System.out.println("Sent | " + this.player.get("Name") + " | " + msg);
            			outWrite.println(msg);
            			outWrite.flush();
            		}
            	}
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
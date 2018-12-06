import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

import org.json.JSONArray;

public class SocketStorage {
    private static volatile SocketStorage socketStorageInstance;
    private static Object mutex = new Object();
    ConcurrentHashMap<String, ArrayList<ClientThread>> socketStorage;
    ConcurrentHashMap<String, JSONArray> playerStorage;

    public SocketStorage() {
    	this.socketStorage = new ConcurrentHashMap<String, ArrayList<ClientThread>>();
    	this.playerStorage = new ConcurrentHashMap<String, JSONArray>();
        this.socketStorage.put("*", new ArrayList<ClientThread>());
    }

    public void removeClient(String teamName, ClientThread ct) {
        ArrayList<ClientThread> clientList = this.socketStorage.get(teamName);
        for (int i = 0; i < clientList.size(); i++) {
            if (clientList.get(i).equals(ct)) {
                clientList.remove(i);
                break;
            }
        }
        this.socketStorage.put(teamName, clientList);
    }

    public void changeTeamName(String oldTeamName, String newTeamName, ClientThread ct) {
        if (this.socketStorage.containsKey(newTeamName)) {
            this.socketStorage.get(newTeamName).add(ct);
        } else {
            ArrayList<ClientThread> newList = new ArrayList<ClientThread>();
            newList.add(ct);
            this.socketStorage.put(newTeamName, newList);
        }
        this.removeClient(oldTeamName, ct);
        return;
    }

    public boolean sendMsgToAll(ClientThread sender, String teamName, String msg) {
        if (this.socketStorage.containsKey(teamName)) {
            for (ClientThread ct : this.socketStorage.get(teamName)) {
            	if (ct.equals(sender)) continue;
                try {
                    ct.queue.put(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    public static SocketStorage getInstance() {
        SocketStorage result = socketStorageInstance;
        if (result == null) {
            synchronized (mutex) {
                result = socketStorageInstance;
                if (result == null) {
                    socketStorageInstance = result = new SocketStorage();
                }
            }
        }
        return result;
    }
}
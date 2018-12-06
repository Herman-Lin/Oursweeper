package com.cs117.oursweeper;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.cs117.oursweeper.Client;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class GameState {

    private static volatile GameState gameState;
    private static Object mutex = new Object();
    private int state; // 0 = Menu; 1 = Player Customization Room; 2 = Waiting Room; 3 = Game Room; 4 = Game Over; 5 = Leaderboard
    private long lastTime;
    private long startTime;
    private long elapsedTime;
    private String teamName;
    private String clientName;
    private char clientColor;
    private volatile boolean updated;
    private boolean isMaster;
    private boolean clientThreadCreated;
    private Board board;
    private String finalTime;
    ArrayList<Player> players;
    public Client clientConnection;

    public GameState() {
        this.state = 0;
        isMaster = false;
        players = new ArrayList<Player>();
        clientThreadCreated = false;
        try {
            new Client(new Socket(Inet4Address.getByName("172.113.249.1"), 8252)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GameState getGameStateInstance() {
        GameState result = gameState;
        if (result == null) {
            synchronized (mutex) {
                result = gameState;
                if (result == null) {
                    gameState = result = new GameState();
                }
            }
        }
        return result;
    }

    public void setClientColor(char color) {
        if (!isColorAvailable(color)) return;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(this.clientName)) {
                players.get(i).setColor(color);
            }
        }
        this.sendColor(color);
    }

    public void setColor(String playerName, char color) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(playerName)) {
                players.get(i).setColor(color);
                return;
            }
        }
        return;
    }

    public void setReady(String playerName) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(playerName)) {
                players.get(i).setReady();
                return;
            }
        }
        return;
    }

    public void setClientInfo(String teamName, String clientName) {
        this.teamName = teamName;
        this.clientName = clientName;
    }

    public void addPlayer(String playerName) {
        Player p = new Player (playerName);
        this.players.add(p);
    }

    public void removePlayer(String playerName) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(playerName)) {
                this.players.remove(players.get(i));
                return;
            }
        }
        return;
    }

    public void goMenu() {this.state = 0;}
    public void goPlayerCustomizationRoom() {
        if (this.state != 0) {
            this.sendLeaveRoom();
            this.isMaster = false;
            this.players = new ArrayList<Player>();
        }
        this.state = 1;
    }
    public void goWaitingRoom() {
        this.sendEnterRoom();
        this.state = 2;
    }
    public void goGameRoom() {
        this.state = 3;
        for (Player p: players) {
            p.clearScore();
        }
        if (isMaster) {
            this.board = new Board();
            JSONObject JSON = new JSONObject();

            // {"ID": (Current Player's Name);
            //  "Action": "Board";
            //  "Board": Board Object in JSON
            // }
            try {
                JSON.put("ID", this.clientName);
                JSON.put("Action", "Board");
                JSON.put("Board", this.board.exportBoard());
                clientConnection.queue.put(JSON.toString());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.startTime = System.currentTimeMillis() + 3000;
        this.setUpdated();
    }

    public void goLeaderBoard() {
        this.state = 4;
        this.finalTime = this.getElapsedTime();
        this.setUpdated();
    }
    public String getBombsLeft() {
        if (this.board == null) {
            return "99";
        }
        int v = this.board.getMineCount();
        if (v < 0) v = 0;
        return String.valueOf(v);
    }
    public void setGameMaster(boolean bool) {
        this.isMaster = bool;
    }

    // Returns number of seconds before game starts
    public int getCountdownTime() {
        long cs = System.currentTimeMillis();
        if (this.startTime - cs > 2500) {
            this.setUpdated();
            return 3;
        } else if (this.startTime - cs > 1500) {
            this.setUpdated();
            return 2;
        } else if (this.startTime - cs > 500) {
            this.setUpdated();
            return 1;
        } else if (this.startTime - cs > 0) {
            this.setUpdated();
            return 0;
        } else {
            this.setUpdated();
            return -1;
        }
    }
    // Returns the time elapsed since game started
    public String getElapsedTime() {
        if (System.currentTimeMillis() > this.startTime) {
            this.elapsedTime = System.currentTimeMillis() - this.startTime + 3500;
            if (elapsedTime - lastTime > 100) {
                lastTime = elapsedTime;
                this.setUpdated();
            }
            return String.format (Locale.US,"%.1f", ((float)(elapsedTime - 3500) / 1000));
        }
        else return "0.0";
    }

    public String getFinalTime(){
        return this.finalTime;
    }

    private void sendMove(int x, int y, String type) {

        // {"ID": (Client Name);
        //  "Action": "flag" / "unflag" / "reveal"
        //  "x": x;
        //  "y": y;
        // }
        JSONObject JSON = new JSONObject();

        try {
            JSON.put("ID", this.clientName);
            JSON.put("Action", type);
            JSON.put("x", x);
            JSON.put("y", y);
            clientConnection.queue.put(JSON.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEnterRoom() {
        // Send JSON to Server:
        // {"ID": (Client Name);
        //  "Action": "EnterRoom";
        //  "TeamName": (this.teamName);
        // }

        JSONObject JSON = new JSONObject();

        try {
            JSON.put("ID", this.clientName);
            JSON.put("Action", "EnterRoom");
            JSON.put("TeamName", this.teamName);
            clientConnection.queue.put(JSON.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void sendLeaveRoom() {
        // Send JSON to Server:
        // {"ID": (Client Name);
        //  "Action": "LeaveRoom";
        // }

        JSONObject JSON = new JSONObject();
        try {
            JSON.put("ID", this.clientName);
            JSON.put("Action", "LeaveRoom");
            JSON.put("TeamName", this.teamName);
            clientConnection.queue.put(JSON.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendColor(char c) {
        // Send JSON to Server:
        // {"ID": (Client Name);
        //  "Action": "Color";
        //  "Color": "A", "B", "C" or "D"
        // }

        JSONObject JSON = new JSONObject();
        try {
            JSON.put("ID", this.clientName);
            JSON.put("Action", "Color");
            JSON.put("Color", Character.toString(c));
            clientConnection.queue.put(JSON.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String[][] getLeaderBoard() {
        String[][] leaderboard = new String[4][3];
        Collections.sort(this.players);
        for (int i = 0; i < 4; i++) {
            if (i < players.size()) {
                leaderboard[i][0] = Character.toString(players.get(i).getColor());
                leaderboard[i][1] = players.get(i).getName();
                leaderboard[i][2] = Integer.toString(players.get(i).getScore());
            }
            else {
                for (int j = 0; j < 3; j++) {
                    leaderboard[i][j] = "";
                }
            }
        }
        return leaderboard;
    }

    public int getState() {return this.state;}
    public void setUpdated() {
        this.updated = true;
    }
    public boolean playerUpdated () {
        boolean hasUpdated = this.updated;
        this.updated = false;
        return hasUpdated;
    }
    public ArrayList<Player> getPlayers() {return this.players;}
    public void setBoard(Board b) {
        this.board = b;
    }
    public void reveal(String playerName, int x, int y) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(playerName)) {
                int val = this.board.reveal(x, y);
                if (val == -1) return;
                if (val == 0) {
                    Gdx.input.vibrate(750);
                    players.get(i).subPoints();
                }
                else {
                    players.get(i).addPoints();
                }
                this.sendMove(x, y, "reveal");
                if (this.board.isCleared()) this.goLeaderBoard();
                return;
            }
        }
        return;
    }

    public void flag(String playerName, int x, int y) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(playerName)) {
                this.board.flag(players.get(i), x, y);
                if(this.clientName.equals(playerName))
                    this.sendMove(x, y, "flag");
                return;
            }
        }
        return;
    }

    public boolean isDisabled() {
        if (System.currentTimeMillis() < this.startTime) {return true;}
        else return false;
    }

    public boolean isColorAvailable(char c) {
        for (Player p: players) {
            if (p.getColor() == c) return false;
        }
        return true;
    }
    public void checkReady() {
        if (players.size() < 2) return;
        // if (players.size() < 1) return;
        for (Player p: players) {
            if (p.getColor() == ' ') return;
        }
        this.goGameRoom();
        this.setUpdated();
        return;
    }
    public char[][] getCurrentBoard() {
        if (this.board == null) {
            char[][] rc = new char[24][30];
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 30; j++) {
                    rc[i][j] = '?';
                }
            }
            return rc;
        }
        return this.board.displayBoard();
    }
}

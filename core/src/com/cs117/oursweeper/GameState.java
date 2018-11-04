package com.cs117.oursweeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class GameState {

    private int state; // 0 = Menu; 1 = Player Customization Room; 2 = Waiting Room; 3 = Game Room; 4 = Game Over; 5 = Leaderboard
    private long currentTime;
    private long startTime;
    private long elapsedTime;
    private long clientOffset;
    private String teamName;
    private String clientName;
    private char clientColor;
    private boolean isMaster;
    private Board board;
    private ArrayList<Player> players;

    public GameState() {
        this.state = 0;
        isMaster = false;
        players = new ArrayList<Player>();
    }

    public void setTeamName(String name) {this.teamName = name;}
    public void setClientPlayerName(String name) {this.clientName = name;}
    public void setClientColor(char color) {this.clientColor = color;}
    public void setReady() {
        Player client = new Player(this.clientName, this.clientColor);
        client.setReady();
        this.players.add(client);
        // TODO: Herman - send ready JSON
    }

    public void goMenu() {this.state = 0;}
    public void goPlayerCustomizationRoom() {
        this.state = 1;
    }
    public void goWaitingRoom() {this.state = 2;}
    public void goGameRoom() {
        this.state = 3;
        for (Player p: players) {
            p.clearScore();
        }

        if (isMaster) {
            this.board = new Board();
            this.clientOffset = 0;
            this.currentTime = System.currentTimeMillis();
            this.startTime = currentTime + 3000;
        }
        else {
            // TODO: Herman
        }
    }
    public void goGaveOver() {
        this.state = 4;
        // set all to unready
    }

    public void goLeaderBoard() {this.state = 5;}

    public void leave() {
        this.sendLeaveRoom();
    }

    // Returns number of seconds before game starts
    public String getCountdownTime() {
        if (System.currentTimeMillis() < this.startTime) {
            return String.format (Locale.US,"%d", (System.currentTimeMillis() - this.startTime) / 1000);
        } else return "0";
    }
    // Returns the time elapsed since game started
    public String getElapsedTime() {
        if (System.currentTimeMillis() > this.startTime) {
            this.elapsedTime = System.currentTimeMillis() - this.startTime;
            return String.format (Locale.US,"%.2f", (float)((elapsedTime) / 1000));
        }
        else return "0.00";
    }


    private void sendMove(int x, int y, boolean flag) {

        // {"ID": (Team Name);
        //  "Action": flag? "Flag": "Reveal";
        //  "TeamName": (this.teamName);
        //  "PlayerName": (Current Player's Name);
        //  "x": x;
        //  "y": y;
        // }
        // TODO: Herman
    }


    // TODO: Wei
    // Implement sendScore, sendBoard, sendReady, and sendEnterRoom, and sendLeaveRoom.
    // Refer to sendMove example above.

    private void sendScore() {
        // Send JSON to Server:
        // {"ID": (Team Name);
        //  "Action": "Score";
        //  "TeamName": (this.teamName);
        //  "PlayerName": (Current Player's Name);
        //  "Score": (Current Player's Score);
        // }
    }
    private void sendBoard() {
        // Send JSON to Server:
        // {"ID": (Team Name);
        //  "Action": "Board";
        //  "Time": (Local Time in millis);
        //  "TeamName": (this.teamName);
        //  "PlayerName": (Current Player's Name);
        //  "Board": (JSON) this.board;
        // }
    }
    private void sendReady() {
        // Send JSON to Server:
        // {"ID": (Team Name);
        //  "Action": "Ready";
        //  "TeamName": (this.teamName);
        //  "PlayerName": (Current Player's Name);
        // }
    }
    private void sendEnterRoom() {
        // Send JSON to Server:
        // {"ID": (Team Name);
        //  "Action": "EnterRoom";
        //  "TeamName": (this.teamName);
        //  "PlayerName": (Current Player's Name);
        //  "PlayerColor": ('A', 'B', 'C', or 'D');
        // }
    }
    private void sendLeaveRoom() {
        // Send JSON to Server:
        // {"ID": (Team Name);
        //  "Action": "LeaveRoom";
        //  "PlayerColor": ('A', 'B', 'C', or 'D');
        // }
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
    public ArrayList<Player> getPlayers() {return this.players;}

    public void reveal(int x, int y) {

        this.board.reveal(y, x);
        this.sendReveal(x, y);
    }

    public boolean isColorAvailable(char c) {
        for (Player p: players) {
            if (p.getColor() == c) return false;
        }
        return true;
    }
    public void checkReady() {
        for (Player p: players) {
            if (!p.getReadyState()) return;
        }
        this.goGameRoom();
        return;
    }
    public char[][] getBoard() {
        this.board = new Board();
        return board.displayBoard();
    }

    //TODO: Wei
}

package com.cs117.oursweeper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Client extends Thread{
    Socket socket;
    BlockingQueue<String> queue;

    public Client(Socket clientSocket) {
        this.socket = clientSocket;
        this.queue = new LinkedBlockingQueue<String>();
    }

    public void run() {
        BufferedReader inReader = null;
        PrintWriter outWrite = null;
            try {
                inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outWrite = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            String line;
            String boardString;
            JSONObject clientData = null;
            while (true) {
                if (GameState.getGameStateInstance().clientConnection == null) {
                    GameState.getGameStateInstance().clientConnection = this;
                }

                try {
                    if (inReader.ready()) {
                    if ((line = inReader.readLine()) != null) {
                        try {
                            line = line.replace("\\u0000", "");
                            clientData = new JSONObject(line);
                        } catch (Exception e) {e.printStackTrace();}
                        System.out.println(line);
                            try {
                                if (clientData.get("Action").equals("Board")) {
                                    boardString = clientData.getString("Board");
                                    Board gameBoard = new Board(boardString);
                                    GameState.getGameStateInstance().setBoard(gameBoard);
                                    GameState.getGameStateInstance().goGameRoom();
                                } else if (clientData.get("Action").equals("flag")) {
                                    GameState.getGameStateInstance().flag(clientData.getString("ID"),
                                            clientData.getInt("x"),
                                            clientData.getInt("y"));
                                } else if (clientData.get("Action").equals("reveal")) {
                                    GameState.getGameStateInstance().reveal(clientData.getString("ID"),
                                            clientData.getInt("x"),
                                            clientData.getInt("y"));
                                } else if (clientData.get("Action").equals("EnterRoom")) {
                                    String playerName = (String) clientData.get("ID");
                                    GameState.getGameStateInstance().addPlayer(playerName);
                                    GameState.getGameStateInstance().setUpdated();
                                } else if (clientData.get("Action").equals("Color")) {
                                    String playerName = (String) clientData.get("ID");
                                    String playerColor = (String) clientData.get("Color");
                                    GameState.getGameStateInstance().setColor(playerName, playerColor.charAt(0));
                                    GameState.getGameStateInstance().setUpdated();
                                } else if (clientData.get("Action").equals("Ready")) {
                                    String playerName = (String) clientData.get("ID");
                                    GameState.getGameStateInstance().setReady(playerName);
                                } else if (clientData.get("Action").equals("LeaveRoom")) {
                                    String playerName = (String) clientData.get("ID");
                                    GameState.getGameStateInstance().removePlayer(playerName);
                                    GameState.getGameStateInstance().setUpdated();
                                    if (GameState.getGameStateInstance().players.size() == 1) {
                                        GameState.getGameStateInstance().setGameMaster(true);
                                    }
                                } else if (clientData.get("Action").equals("RoomInfo")) {
                                    JSONArray jarr = clientData.getJSONArray("Players");
                                    String color;
                                    String name;
                                    GameState.getGameStateInstance().players = new ArrayList<Player>();
                                    for (int i = 0; i < jarr.length(); i++) {
                                        name = jarr.getJSONObject(i).getString("Name");
                                        GameState.getGameStateInstance().addPlayer(name);
                                        color = jarr.getJSONObject(i).getString("Color");
                                        GameState.getGameStateInstance().setColor(name, color.charAt(0));
                                    }
                                    if (GameState.getGameStateInstance().players.size() == 1) {
                                        GameState.getGameStateInstance().setGameMaster(true);
                                    }
                                    GameState.getGameStateInstance().setUpdated();
                                } else {
                                    System.out.println(clientData.get("Action"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                    }
                    else {
                        String msg;
                        while (((msg = queue.poll()) != null)) {
                            System.out.println("Sent: " + msg);
                            outWrite.println(msg);
                            outWrite.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
}
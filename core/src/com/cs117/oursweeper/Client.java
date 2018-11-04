package com.cs117.oursweeper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

public class Client {
    protected Socket client;
    protected BufferedReader in;
    protected BufferedWriter out;

    public Client(String hostName, int ip) {
        try {
            this.client = new Socket(hostName, ip);
            this.in = new BufferedReader(new InputStreamReader(
                    this.client.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(
                    this.client.getOutputStream()));
            String buffer = null;
            while ((buffer = in.readLine()) != null) {
                // Debug purpose - remove later
                System.out.println(buffer);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String s) {
        try {
            out.write(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

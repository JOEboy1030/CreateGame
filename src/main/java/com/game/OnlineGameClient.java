package com.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class OnlineGameClient {

    private final String host;
    private final int port;

    public OnlineGameClient(String host, int port) {
        this.host = host;
        this.port = port;

    }

    public void start() {

        System.out.println("サーバーに接続しています...");

        try (
                Socket socket = new Socket(host, port);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()))) {

            System.out.println("接続しました！");

            String message;

            while ((message = reader.readLine()) != null) {
                System.out.println(message);
            }

        } catch (IOException e) {
            System.out.println("サーバーに接続できませんでした。");
            e.printStackTrace();
        }
    }
}
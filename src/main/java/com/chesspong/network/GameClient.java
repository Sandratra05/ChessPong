package com.chesspong.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running;
    private NetworkMessageListener listener;
    private ExecutorService executor;

    public interface NetworkMessageListener {
        void onMessageReceived(NetworkMessage message);
        void onConnected();
        void onDisconnected();
    }

    public GameClient() {
        executor = Executors.newSingleThreadExecutor();
    }

    public void setListener(NetworkMessageListener listener) {
        this.listener = listener;
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        running = true;

        if (listener != null) {
            listener.onConnected();
        }

        executor.submit(() -> {
            try {
                while (running) {
                    try {
                        NetworkMessage message = (NetworkMessage) in.readObject();
                        if (listener != null) {
                            listener.onMessageReceived(message);
                        }
                    } catch (EOFException | SocketException e) {
                        System.out.println("Déconnecté du serveur");
                        break;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            } finally {
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        });
    }

    public void sendMessage(NetworkMessage message) {
        if (out != null) {
            try {
                out.writeObject(message);
                out.flush();
                out.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            executor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
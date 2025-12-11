package com.chesspong.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running;
    private NetworkMessageListener listener;
    private ExecutorService executor;

    public interface NetworkMessageListener {
        void onMessageReceived(NetworkMessage message);
        void onClientConnected();
        void onClientDisconnected();
    }

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        executor = Executors.newSingleThreadExecutor();
    }

    public void setListener(NetworkMessageListener listener) {
        this.listener = listener;
    }

    public void start() {
        running = true;
        executor.submit(() -> {
            try {
                System.out.println("Serveur en attente de connexion sur le port " + serverSocket.getLocalPort());
                clientSocket = serverSocket.accept();
                System.out.println("Client connecté: " + clientSocket.getInetAddress());

                out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(clientSocket.getInputStream());

                if (listener != null) {
                    listener.onClientConnected();
                }

                while (running) {
                    try {
                        NetworkMessage message = (NetworkMessage) in.readObject();
                        if (listener != null) {
                            listener.onMessageReceived(message);
                        }
                    } catch (EOFException | SocketException e) {
                        System.out.println("Client déconnecté");
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
                    listener.onClientDisconnected();
                }
            }
        });
    }

    public void sendMessage(NetworkMessage message) {
        if (out != null) {
            try {
                out.writeObject(message);
                out.flush();
                out.reset(); // Important pour éviter les fuites mémoire
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            executor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }
}
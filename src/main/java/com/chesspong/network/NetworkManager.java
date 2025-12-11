package com.chesspong.network;

import com.chesspong.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkManager {
    private GameServer server;
    private GameClient client;
    private boolean isHost;
    private NetworkUpdateListener updateListener;

    public interface NetworkUpdateListener {
        void onPaddleUpdate(int playerId, double x, double y);
        void onBallUpdate(double x, double y, double vx, double vy);
        void onGameStateUpdate(GameStateData gameStateData);
        void onGameConfigReceived(GameConfig gameConfig);  // ← Ajout
    }

    public NetworkManager() {
        this.isHost = false;
    }

    public void setUpdateListener(NetworkUpdateListener listener) {
        this.updateListener = listener;
    }

    public void startAsHost(int port, Runnable onClientConnected) {
        try {
            isHost = true;
            server = new GameServer(port);
            server.setListener(new GameServer.NetworkMessageListener() {
                @Override
                public void onMessageReceived(NetworkMessage message) {
                    handleReceivedMessage(message);
                }

                @Override
                public void onClientConnected() {
                    System.out.println("Client connecté au serveur");
                    if (onClientConnected != null) {
                        onClientConnected.run();
                    }
                }

                @Override
                public void onClientDisconnected() {
                    System.out.println("Client déconnecté");
                }
            });
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectAsClient(String host, int port, Runnable onConnected) {
        try {
            isHost = false;
            client = new GameClient();
            client.setListener(new GameClient.NetworkMessageListener() {
                @Override
                public void onMessageReceived(NetworkMessage message) {
                    handleReceivedMessage(message);
                }

                @Override
                public void onConnected() {
                    System.out.println("Connecté au serveur");
                    if (onConnected != null) {
                        onConnected.run();
                    }
                }

                @Override
                public void onDisconnected() {
                    System.out.println("Déconnecté du serveur");
                }
            });
            client.connect(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleReceivedMessage(NetworkMessage message) {
        if (updateListener == null) return;

        switch (message.getType()) {
            case PADDLE_UPDATE:
                PaddleData paddleData = (PaddleData) message.getData();
                updateListener.onPaddleUpdate(paddleData.getPlayerId(), paddleData.getX(), paddleData.getY());
                break;

            case BALL_UPDATE:
                BallData ballData = (BallData) message.getData();
                updateListener.onBallUpdate(ballData.getX(), ballData.getY(), ballData.getVx(), ballData.getVy());
                break;

            case GAME_STATE:
                GameStateData gameStateData = (GameStateData) message.getData();
                updateListener.onGameStateUpdate(gameStateData);
                break;
            case GAME_CONFIG:  // ← Ajout
                GameConfig gameConfig = (GameConfig) message.getData();
                updateListener.onGameConfigReceived(gameConfig);
                break;
        }
    }

    public void sendPaddleUpdate(int playerId, double x, double y) {
        PaddleData data = new PaddleData(playerId, x, y);
        NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.PADDLE_UPDATE, data);

        if (isHost && server != null) {
            server.sendMessage(message);
        } else if (!isHost && client != null) {
            client.sendMessage(message);
        }
    }

    public void sendBallUpdate(Ball ball) {
        BallData data = new BallData(ball.getX(), ball.getY(), ball.getVx(), ball.getVy());
        NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.BALL_UPDATE, data);

        if (isHost && server != null) {
            server.sendMessage(message);
        }
    }

    public void sendGameState(Ball ball, PongPaddle paddle1, PongPaddle paddle2, Board board) {
        BallData ballData = new BallData(ball.getX(), ball.getY(), ball.getVx(), ball.getVy());
        PaddleData paddle1Data = new PaddleData(1, paddle1.getX(), paddle1.getY());
        PaddleData paddle2Data = new PaddleData(2, paddle2.getX(), paddle2.getY());

        List<PieceData> piecesData = new ArrayList<>();
        for (Piece piece : board.getAllPieces()) {
            PieceData pieceData = new PieceData(
                piece.getX(),
                piece.getY(),
                piece.getHealth(),
                piece.isAlive(),
                piece.getClass().getSimpleName()
            );
            piecesData.add(pieceData);
        }

        GameStateData gameStateData = new GameStateData(ballData, paddle1Data, paddle2Data, piecesData, 0, 0);
        NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.GAME_STATE, gameStateData);

        if (isHost && server != null) {
            server.sendMessage(message);
        }
    }

    public void shutdown() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.disconnect();
        }
    }

    public boolean isHost() {
        return isHost;
    }

    public boolean isConnected() {
        if (isHost) {
            return server != null && server.isConnected();
        } else {
            return client != null && client.isConnected();
        }
    }

    public void sendGameConfig(int numFiles, Map<String, Integer> pieceLives) {
        GameConfig config = new GameConfig(numFiles, pieceLives);
        NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.GAME_CONFIG, config);

        if (isHost && server != null) {
            server.sendMessage(message);
        }
    }


}
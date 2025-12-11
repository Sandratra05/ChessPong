package com.chesspong.controller;

import com.chesspong.model.GameState;
import com.chesspong.model.Joueur;
import com.chesspong.network.NetworkManager;
import com.chesspong.view.BallView;
import com.chesspong.view.BoardView;
import com.chesspong.view.PaddleView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameController {
    private GameState gameState;
    private BoardView boardView;
    private BallView ballView;
    private PaddleView paddleView1, paddleView2;
    private InputHandler inputHandler;
    private CollisionHandler collisionHandler;
    private AnimationTimer timer;
    private NetworkManager networkManager;
    private Boolean isHost; // null = local, true = host, false = client

    public GameController(Stage stage, int numFiles, NetworkManager networkManager, Boolean isHost) {
        this.networkManager = networkManager;
        this.isHost = isHost;

        Joueur player1 = new Joueur("Player 1", true);
        Joueur player2 = new Joueur("Player 2", false);
        gameState = new GameState(numFiles, player1, player2, isHost);

        boardView = new BoardView(gameState.getBoard());
        boardView.setLayoutX(400 - (numFiles * 70) / 2.0);
        boardView.setLayoutY(100);
        ballView = new BallView(gameState.getBall());
        paddleView1 = new PaddleView(gameState.getPaddle1());
        paddleView2 = new PaddleView(gameState.getPaddle2());

        // Ajuster la taille des paddles selon le nombre de colonnes
        double cellSize = 70;
        double playAreaWidth = numFiles * cellSize;
        double playAreaX = boardView.getLayoutX();

        // Règle : si 2 colonnes, réduire la largeur des paddles pour ne pas dépasser
        if (numFiles <= 2) {
            double newPaddleWidth = 30; // un peu plus petit que la zone
            gameState.getPaddle1().setWidth(newPaddleWidth);
            gameState.getPaddle2().setWidth(newPaddleWidth);
        } else {
            // valeurs par défaut (déjà initialisées) ; mais on peut s'assurer qu'elles tiennent
            double defaultWidth = 100;
            gameState.getPaddle1().setWidth(defaultWidth);
            gameState.getPaddle2().setWidth(defaultWidth);
        }

        // Centrer les paddles horizontalement dans la zone de jeu
        double p1x = playAreaX + (playAreaWidth - gameState.getPaddle1().getWidth()) / 2.0;
        double p2x = playAreaX + (playAreaWidth - gameState.getPaddle2().getWidth()) / 2.0;
        gameState.getPaddle1().setX(p1x);
        gameState.getPaddle2().setX(p2x);

        Pane root = new Pane();
        root.getChildren().addAll(boardView, ballView, paddleView1, paddleView2);

        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.setTitle("ChessPong");

        inputHandler = new InputHandler(scene, gameState.getPaddle1(), gameState.getPaddle2(), playAreaX, playAreaWidth);
        collisionHandler = new CollisionHandler();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();

        if (networkManager != null && isHost != null) {
            setupNetworkListeners();
        }
    }


    private void setupNetworkListeners() {
        networkManager.setUpdateListener(new NetworkManager.NetworkUpdateListener() {
            @Override
            public void onPaddleUpdate(int playerId, double x, double y) {
                javafx.application.Platform.runLater(() -> {
                    if (isHost && playerId == 2) {
                        gameState.getPaddle2().setY(y);
                    } else if (!isHost && playerId == 1) {
                        gameState.getPaddle1().setY(y);
                    }
                });
            }

            @Override
            public void onBallUpdate(double x, double y, double vx, double vy) {
                javafx.application.Platform.runLater(() -> {
                    if (!isHost) {
                        gameState.getBall().setPosition(x, y);
                        gameState.getBall().setVelocity(vx, vy);
                    }
                });
            }

            @Override
            public void onGameStateUpdate(com.chesspong.network.GameStateData gameStateData) {
                javafx.application.Platform.runLater(() -> {
                    if (!isHost) {
                        gameState.getBall().setPosition(
                                gameStateData.getBallData().getX(),
                                gameStateData.getBallData().getY()
                        );
                        gameState.getBall().setVelocity(
                                gameStateData.getBallData().getVx(),
                                gameStateData.getBallData().getVy()
                        );
                        gameState.getPaddle1().setY(gameStateData.getPaddle1Data().getY());
                        gameState.getPaddle2().setY(gameStateData.getPaddle2Data().getY());
                    }
                });
            }

            @Override
            public void onGameConfigReceived(com.chesspong.network.GameConfig gameConfig) {
                javafx.application.Platform.runLater(() -> {
                    if (!isHost) {
                        // Le client reçoit la configuration et démarre le jeu
                        System.out.println("Configuration reçue: " + gameConfig.getNumFiles() + " types de pièces");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Configuration reçue");
                        alert.setHeaderText(null);
                        alert.setContentText("Configuration: " + gameConfig.getNumFiles() + " types de pièces\nDémarrage de la partie...");
                        alert.showAndWait();

                        // Créer une nouvelle instance du contrôleur avec les bons paramètres
                        Stage stage = (Stage) ballView.getScene().getWindow();
                        new GameController(stage, gameConfig.getNumFiles(), networkManager, isHost);
                    }
                });
            }
        });
    }

    private void update() {
        gameState.getBall().updatePosition();
        collisionHandler.handleBallCollisions(gameState.getBall(), gameState.getBoard(), gameState.getPaddle1(), gameState.getPaddle2());
        ballView.updatePosition();
        paddleView1.updatePosition();
        paddleView2.updatePosition();
        boardView.draw();
        gameState.checkWinCondition();
        if (gameState.isGameOver()) {
            timer.stop();
            System.out.println("Game Over! Winner: " + gameState.getWinner().getName());
        }

        // Ajout de la synchronisation réseau
        if (networkManager != null && isHost != null) {
            if (isHost) {
                networkManager.sendPaddleUpdate(1, gameState.getPaddle1().getX(), gameState.getPaddle1().getY());
                networkManager.sendBallUpdate(gameState.getBall());
                // Envoi périodique de l'état complet toutes les secondes
                if (System.currentTimeMillis() % 1000 < 16) {
                    networkManager.sendGameState(gameState.getBall(), gameState.getPaddle1(), gameState.getPaddle2(), gameState.getBoard());
                }
            } else {
                networkManager.sendPaddleUpdate(2, gameState.getPaddle2().getX(), gameState.getPaddle2().getY());
            }
        }
    }
}

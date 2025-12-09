package com.chesspong.controller;

import com.chesspong.model.GameState;
import com.chesspong.model.Joueur;
import com.chesspong.view.BallView;
import com.chesspong.view.BoardView;
import com.chesspong.view.PaddleView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
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

    public GameController(Stage stage, int numFiles) {
        Joueur player1 = new Joueur("Player1", true);
        Joueur player2 = new Joueur("Player2", false);
        gameState = new GameState(numFiles, player1, player2);

        boardView = new BoardView(gameState.getBoard());
        boardView.setLayoutX(200);
        boardView.setLayoutY(100);
        ballView = new BallView(gameState.getBall());
        paddleView1 = new PaddleView(gameState.getPaddle1());
        paddleView2 = new PaddleView(gameState.getPaddle2());

        Pane root = new Pane();
        root.getChildren().addAll(boardView, ballView, paddleView1, paddleView2);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("ChessPong");

        inputHandler = new InputHandler(scene, gameState.getPaddle1(), gameState.getPaddle2());
        collisionHandler = new CollisionHandler();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
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
    }
}

package com.chesspong.controller;

import com.chesspong.model.PongPaddle;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private Scene scene;
    private PongPaddle paddle1, paddle2;
    private boolean aPressed = false;
    private boolean dPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private AnimationTimer timer;

    // Mouvement lisse
    private double velocity1 = 0;
    private double velocity2 = 0;

    // Bornes de la zone de jeu (en pixels, coordonnées locales de la scène)
    private double playAreaX;
    private double playAreaWidth;

    public InputHandler(Scene scene, PongPaddle paddle1, PongPaddle paddle2, double playAreaX, double playAreaWidth) {
        this.scene = scene;
        this.paddle1 = paddle1;
        this.paddle2 = paddle2;
        this.playAreaX = playAreaX;
        this.playAreaWidth = playAreaWidth;
        setupInput();
        timer.start();
    }

    private void setupInput() {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.A) {
                aPressed = true;
            } else if (code == KeyCode.D) {
                dPressed = true;
            } else if (code == KeyCode.LEFT) {
                leftPressed = true;
            } else if (code == KeyCode.RIGHT) {
                rightPressed = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.A) {
                aPressed = false;
                velocity1 = 0; // arrêt instantané paddle1 quand A relâché
            } else if (code == KeyCode.D) {
                dPressed = false;
                velocity1 = 0; // arrêt instantané paddle1 quand D relâché
            } else if (code == KeyCode.LEFT) {
                leftPressed = false;
                velocity2 = 0; // arrêt instantané paddle2 quand LEFT relâché
            } else if (code == KeyCode.RIGHT) {
                rightPressed = false;
                velocity2 = 0; // arrêt instantané paddle2 quand RIGHT relâché
            }
        });

        timer = new AnimationTimer() {
            private long last = -1;

            @Override
            public void handle(long now) {
                if (last < 0) last = now;
                double deltaSec = (now - last) / 1_000_000_000.0;
                last = now;

                // Paramètres de mouvement — ajustés pour "moyen" et smooth
                double maxSpeed = 400.0; // px/sec
                double accel = 2000.0; // px/sec^2

                // Paddle 1
                double targetDir1 = 0;
                if (aPressed) targetDir1 -= 1;
                if (dPressed) targetDir1 += 1;
                // Calculer vitesse avec accélération
                if (targetDir1 != 0) {
                    velocity1 += targetDir1 * accel * deltaSec;
                } else {
                    // arrêt instantané si aucune touche n'est pressée
                    velocity1 = 0;
                }
                // Clamp
                if (Math.abs(velocity1) > maxSpeed) velocity1 = Math.signum(velocity1) * maxSpeed;

                double newX1 = paddle1.getX() + velocity1 * deltaSec;
                double leftBound = playAreaX;
                double rightBound = playAreaX + playAreaWidth - paddle1.getWidth();
                if (newX1 < leftBound) {
                    newX1 = leftBound;
                    velocity1 = 0;
                } else if (newX1 > rightBound) {
                    newX1 = rightBound;
                    velocity1 = 0;
                }
                paddle1.setX(newX1);

                // Paddle 2
                double targetDir2 = 0;
                if (leftPressed) targetDir2 -= 1;
                if (rightPressed) targetDir2 += 1;
                if (targetDir2 != 0) {
                    velocity2 += targetDir2 * accel * deltaSec;
                } else {
                    // arrêt instantané si aucune touche n'est pressée
                    velocity2 = 0;
                }
                if (Math.abs(velocity2) > maxSpeed) velocity2 = Math.signum(velocity2) * maxSpeed;

                double newX2 = paddle2.getX() + velocity2 * deltaSec;
                double rightBound2 = playAreaX + playAreaWidth - paddle2.getWidth();
                if (newX2 < leftBound) {
                    newX2 = leftBound;
                    velocity2 = 0;
                } else if (newX2 > rightBound2) {
                    newX2 = rightBound2;
                    velocity2 = 0;
                }
                paddle2.setX(newX2);
            }
        };
    }
}

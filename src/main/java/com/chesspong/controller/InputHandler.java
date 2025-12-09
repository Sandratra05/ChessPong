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

    public InputHandler(Scene scene, PongPaddle paddle1, PongPaddle paddle2) {
        this.scene = scene;
        this.paddle1 = paddle1;
        this.paddle2 = paddle2;
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
            } else if (code == KeyCode.D) {
                dPressed = false;
            } else if (code == KeyCode.LEFT) {
                leftPressed = false;
            } else if (code == KeyCode.RIGHT) {
                rightPressed = false;
            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double speed = 5.0;
                if (aPressed && paddle1.getX() > 0) {
                    paddle1.setX(paddle1.getX() - speed);
                }
                if (dPressed && paddle1.getX() < 800 - paddle1.getWidth()) {
                    paddle1.setX(paddle1.getX() + speed);
                }
                if (leftPressed && paddle2.getX() > 0) {
                    paddle2.setX(paddle2.getX() - speed);
                }
                if (rightPressed && paddle2.getX() < 800 - paddle2.getWidth()) {
                    paddle2.setX(paddle2.getX() + speed);
                }
            }
        };
    }
}

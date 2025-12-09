package com.chesspong.controller;

import com.chesspong.model.PongPaddle;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private Scene scene;
    private PongPaddle paddle1, paddle2;

    public InputHandler(Scene scene, PongPaddle paddle1, PongPaddle paddle2) {
        this.scene = scene;
        this.paddle1 = paddle1;
        this.paddle2 = paddle2;
        setupInput();
    }

    private void setupInput() {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.A && paddle1.getX() > 0) {
                paddle1.setX(paddle1.getX() - 10);
            } else if (code == KeyCode.D && paddle1.getX() < 800 - paddle1.getWidth()) {
                paddle1.setX(paddle1.getX() + 10);
            } else if (code == KeyCode.LEFT && paddle2.getX() > 0) {
                paddle2.setX(paddle2.getX() - 10);
            } else if (code == KeyCode.RIGHT && paddle2.getX() < 800 - paddle2.getWidth()) {
                paddle2.setX(paddle2.getX() + 10);
            }
        });
    }
}

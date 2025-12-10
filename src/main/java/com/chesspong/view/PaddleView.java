package com.chesspong.view;

import com.chesspong.model.PongPaddle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PaddleView extends Rectangle {
    private PongPaddle paddle;

    public PaddleView(PongPaddle paddle) {
        super(paddle.getWidth(), paddle.getHeight());
        this.paddle = paddle;
        setFill(Color.BLUE);
        updatePosition();
    }

    public void updatePosition() {
        // Mettre à jour la taille au cas où le modèle ait changé
        setWidth(paddle.getWidth());
        setHeight(paddle.getHeight());
        setX(paddle.getX());
        setY(paddle.getY());
    }

    public PongPaddle getPaddle() {
        return paddle;
    }
}

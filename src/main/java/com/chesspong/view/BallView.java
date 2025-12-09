package com.chesspong.view;

import com.chesspong.model.Ball;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BallView extends Circle {
    private Ball ball;

    public BallView(Ball ball) {
        super(ball.getRadius());
        this.ball = ball;
        setFill(Color.RED);
        updatePosition();
    }

    public void updatePosition() {
        setCenterX(ball.getX());
        setCenterY(ball.getY());
    }

    public Ball getBall() {
        return ball;
    }
}

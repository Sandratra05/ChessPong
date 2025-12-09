package com.chesspong.controller;

import com.chesspong.model.Ball;
import com.chesspong.model.Board;
import com.chesspong.model.Piece;
import com.chesspong.model.PongPaddle;

public class CollisionHandler {
    public void handleBallCollisions(Ball ball, Board board, PongPaddle paddle1, PongPaddle paddle2) {
        // Collision with paddles
        if (ball.getY() - ball.getRadius() <= paddle1.getY() + paddle1.getHeight() &&
            ball.getY() + ball.getRadius() >= paddle1.getY() &&
            ball.getX() >= paddle1.getX() && ball.getX() <= paddle1.getX() + paddle1.getWidth()) {
            ball.setVy(-ball.getVy());
        }
        if (ball.getY() + ball.getRadius() >= paddle2.getY() &&
            ball.getY() - ball.getRadius() <= paddle2.getY() + paddle2.getHeight() &&
            ball.getX() >= paddle2.getX() && ball.getX() <= paddle2.getX() + paddle2.getWidth()) {
            ball.setVy(-ball.getVy());
        }

        // Collision with pieces
        double boardOffsetX = 200;
        double boardOffsetY = 100;
        for (Piece piece : board.getAllPieces()) {
            if (piece.isAlive()) {
                double px = piece.getX() * 50 + 25 + boardOffsetX;
                double py = piece.getY() * 50 + 25 + boardOffsetY;
                double dist = Math.sqrt(Math.pow(ball.getX() - px, 2) + Math.pow(ball.getY() - py, 2));
                if (dist <= ball.getRadius() + 15) {
                    piece.setHealth(piece.getHealth() - 1);
                    ball.setVx(-ball.getVx());
                    if (!piece.isAlive()) {
                        board.removePiece(piece.getX(), piece.getY());
                    }
                }
            }
        }

        // Wall bounces (board boundaries)
        if (ball.getX() - ball.getRadius() <= 200 || ball.getX() + ball.getRadius() >= 600) {
            ball.setVx(-ball.getVx());
        }
        if (ball.getY() - ball.getRadius() <= 100 || ball.getY() + ball.getRadius() >= 500) {
            ball.setVy(-ball.getVy());
        }
    }
}

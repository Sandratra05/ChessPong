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
                double px = piece.getX() * 50 + boardOffsetX;
                double py = piece.getY() * 50 + boardOffsetY;
                double pw = piece.getWidth();
                double ph = piece.getHeight();
                if (ball.getX() + ball.getRadius() >= px && ball.getX() - ball.getRadius() <= px + pw &&
                    ball.getY() + ball.getRadius() >= py && ball.getY() - ball.getRadius() <= py + ph) {
                    piece.setHealth(piece.getHealth() - 1);
                    // Supprimer le rebond pour permettre à la balle de continuer et toucher les pièces derrière
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

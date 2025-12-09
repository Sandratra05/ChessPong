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
                // Compute closest point on rectangle to ball center
                double closestX = Math.max(px, Math.min(ball.getX(), px + pw));
                double closestY = Math.max(py, Math.min(ball.getY(), py + ph));
                double dx = ball.getX() - closestX;
                double dy = ball.getY() - closestY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance < ball.getRadius()) {
                    System.out.println("Collision with piece at (" + piece.getX() + ", " + piece.getY() + ")");

                    piece.setHealth(piece.getHealth() - 1);
                    System.out.println("Piece health: " + piece.getHealth());
                    // Collision detected
                    if (!piece.isAlive()) {
                        board.removePiece(piece.getX(), piece.getY());
                    }
                    // Bounce the ball
                    double nx = dx;
                    double ny = dy;
                    if (distance > 0) {
                        nx /= distance;
                        ny /= distance;
                    } else {
                        // If exactly at center, arbitrary normal (e.g., horizontal bounce)
                        nx = 0;
                        ny = 1;
                    }
                    double dot = ball.getVx() * nx + ball.getVy() * ny;
                    ball.setVx(ball.getVx() - 2 * dot * nx);
                    ball.setVy(ball.getVy() - 2 * dot * ny);
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

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
        // Compute visible board screen origin and start column so we can map piece (col,row) -> screen coords
        int start = (8 - board.getNumFiles()) / 2;
        double boardScreenLeft = 400.0 - (board.getNumFiles() * 50) / 2.0; // same centering used in GameController
        double boardOffsetY = 100.0;

        double prevX = ball.getPrevX();
        double prevY = ball.getPrevY();
        double curX = ball.getX();
        double curY = ball.getY();

        for (Piece piece : board.getAllPieces()) {
            if (!piece.isAlive()) continue;

            // If piece column is outside visible range, skip
            int col = piece.getX();
            if (col < start || col >= start + board.getNumFiles()) continue;

            double px = boardScreenLeft + (col - start) * 50.0;
            double py = boardOffsetY + piece.getY() * 50.0;
            double pw = 50.0;
            double ph = 50.0;

            // fast reject by segment AABB
            double segMinX = Math.min(prevX, curX) - ball.getRadius();
            double segMaxX = Math.max(prevX, curX) + ball.getRadius();
            double segMinY = Math.min(prevY, curY) - ball.getRadius();
            double segMaxY = Math.max(prevY, curY) + ball.getRadius();
            if (segMaxX < px || segMinX > px + pw || segMaxY < py || segMinY > py + ph) {
                continue;
            }

            // expanded rect to account for radius (swept circle approx)
            double expandedPx = px - ball.getRadius();
            double expandedPy = py - ball.getRadius();
            double expandedPw = pw + 2 * ball.getRadius();
            double expandedPh = ph + 2 * ball.getRadius();

            boolean hitDuringMove = segmentIntersectsAABB(prevX, prevY, curX, curY, expandedPx, expandedPy, expandedPw, expandedPh);
            boolean overlapNow = circleIntersectsAABB(curX, curY, ball.getRadius(), px, py, pw, ph);

            if (!hitDuringMove && !overlapNow) {
                continue;
            }

            // collision occurred
            System.out.println("Collision with piece at (" + piece.getX() + ", " + piece.getY() + ")");

            // decrease life and possibly remove
            piece.setHealth(piece.getHealth() - 1);
            System.out.println("Piece health: " + piece.getHealth());
            boolean stillAlive = piece.isAlive();
            if (!stillAlive) {
                board.removePiece(piece.getX(), piece.getY());
            }

            // Reflect the ball in all cases (even if piece died this hit)
            // determine closest point on rect to current center
            double closestX = clamp(curX, px, px + pw);
            double closestY = clamp(curY, py, py + ph);
            double dx = curX - closestX;
            double dy = curY - closestY;

            // If exactly centered on edge (rare) fall back to prev-based decision
            if (Math.abs(dx) < 1e-6 && Math.abs(dy) < 1e-6) {
                double centerX = px + pw / 2.0;
                double centerY = py + ph / 2.0;
                double toPrevX = prevX - centerX;
                double toPrevY = prevY - centerY;

                if (Math.abs(toPrevX) > Math.abs(toPrevY)) {
                    // horizontal impact
                    if (toPrevX < 0) ball.setX(px - ball.getRadius() - 0.1);
                    else ball.setX(px + pw + ball.getRadius() + 0.1);
                    ball.setVx(-ball.getVx());
                } else {
                    if (toPrevY < 0) ball.setY(py - ball.getRadius() - 0.1);
                    else ball.setY(py + ph + ball.getRadius() + 0.1);
                    ball.setVy(-ball.getVy());
                }
            } else {
                // reflect on the axis of larger penetration direction
                if (Math.abs(dx) > Math.abs(dy)) {
                    // reflect horizontal
                    if (dx < 0) ball.setX(closestX - ball.getRadius() - 0.1);
                    else ball.setX(closestX + ball.getRadius() + 0.1);
                    ball.setVx(-ball.getVx());
                } else {
                    // reflect vertical
                    if (dy < 0) ball.setY(closestY - ball.getRadius() - 0.1);
                    else ball.setY(closestY + ball.getRadius() + 0.1);
                    ball.setVy(-ball.getVy());
                }
            }

            // handle only one piece per frame to avoid double collisions
            break;
        }

        // Wall bounces (board boundaries) - visible board bounds
        double left = 400.0 - (board.getNumFiles() * 50) / 2.0;
        double right = left + board.getNumFiles() * 50.0;
        if (ball.getX() - ball.getRadius() <= left || ball.getX() + ball.getRadius() >= right) {
            ball.setVx(-ball.getVx());
        }
        if (ball.getY() - ball.getRadius() <= 100.0 || ball.getY() + ball.getRadius() >= 500.0) {
            ball.setVy(-ball.getVy());
        }
    }

    // Liang-Barsky clipping to test segment vs AABB
    private boolean segmentIntersectsAABB(double x1, double y1, double x2, double y2, double rx, double ry, double rw, double rh) {
        double t0 = 0.0, t1 = 1.0;
        double dx = x2 - x1;
        double dy = y2 - y1;
        double[] p = {-dx, dx, -dy, dy};
        double[] q = {x1 - rx, rx + rw - x1, y1 - ry, ry + rh - y1};

        for (int i = 0; i < 4; i++) {
            double pi = p[i];
            double qi = q[i];
            if (pi == 0) {
                if (qi < 0) return false;
            } else {
                double t = qi / pi;
                if (pi < 0) {
                    if (t > t1) return false;
                    if (t > t0) t0 = t;
                } else {
                    if (t < t0) return false;
                    if (t < t1) t1 = t;
                }
            }
        }
        return t0 <= t1;
    }

    private boolean circleIntersectsAABB(double cx, double cy, double r, double rx, double ry, double rw, double rh) {
        double closestX = clamp(cx, rx, rx + rw);
        double closestY = clamp(cy, ry, ry + rh);
        double dx = cx - closestX;
        double dy = cy - closestY;
        return dx * dx + dy * dy <= r * r;
    }

    private double clamp(double v, double a, double b) {
        if (v < a) return a;
        if (v > b) return b;
        return v;
    }
}

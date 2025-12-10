package com.chesspong.controller;

import com.chesspong.model.Ball;
import com.chesspong.model.Board;
import com.chesspong.model.Piece;
import com.chesspong.model.PongPaddle;

public class CollisionHandler {
    public void handleBallCollisions(Ball ball, Board board, PongPaddle paddle1, PongPaddle paddle2) {
        double prevX = ball.getPrevX();
        double prevY = ball.getPrevY();
        double curX = ball.getX();
        double curY = ball.getY();

        // Handle paddle collisions using the robust resolver; stop after first handled
        if (resolvePaddleCollision(ball, paddle1, prevX, prevY, curX, curY)) return;
        if (resolvePaddleCollision(ball, paddle2, prevX, prevY, curX, curY)) return;

        // Collision with pieces
        int start = (8 - board.getNumFiles()) / 2;
        double boardScreenLeft = 400.0 - (board.getNumFiles() * 70) / 2.0; // same centering used in GameController
        double boardOffsetY = 100.0;

        for (Piece piece : board.getAllPieces()) {
            if (!piece.isAlive()) continue;

            int col = piece.getX();
            if (col < start || col >= start + board.getNumFiles()) continue;

            double px = boardScreenLeft + (col - start) * 70.0;
            double py = boardOffsetY + piece.getY() * 70.0;
            double pw = 70.0;
            double ph = 70.0;

            double segMinX = Math.min(prevX, curX) - ball.getRadius();
            double segMaxX = Math.max(prevX, curX) + ball.getRadius();
            double segMinY = Math.min(prevY, curY) - ball.getRadius();
            double segMaxY = Math.max(prevY, curY) + ball.getRadius();
            if (segMaxX < px || segMinX > px + pw || segMaxY < py || segMinY > py + ph) {
                continue;
            }

            double expandedPx = px - ball.getRadius();
            double expandedPy = py - ball.getRadius();
            double expandedPw = pw + 2 * ball.getRadius();
            double expandedPh = ph + 2 * ball.getRadius();

            boolean hitDuringMove = segmentIntersectsAABB(prevX, prevY, curX, curY, expandedPx, expandedPy, expandedPw, expandedPh);
            boolean overlapNow = circleIntersectsAABB(curX, curY, ball.getRadius(), px, py, pw, ph);

            if (!hitDuringMove && !overlapNow) {
                continue;
            }

            System.out.println("Collision with piece at (" + piece.getX() + ", " + piece.getY() + ")");

            piece.setHealth(piece.getHealth() - 1);
            System.out.println("Piece health: " + piece.getHealth());
            boolean stillAlive = piece.isAlive();
            if (!stillAlive) {
                board.removePiece(piece.getX(), piece.getY());
            }

            double closestX = clamp(curX, px, px + pw);
            double closestY = clamp(curY, py, py + ph);
            double dx = curX - closestX;
            double dy = curY - closestY;

            if (Math.abs(dx) < 1e-6 && Math.abs(dy) < 1e-6) {
                double centerX = px + pw / 2.0;
                double centerY = py + ph / 2.0;
                double toPrevX = prevX - centerX;
                double toPrevY = prevY - centerY;

                if (Math.abs(toPrevX) > Math.abs(toPrevY)) {
                    if (toPrevX < 0) ball.setX(px - ball.getRadius() - 0.1);
                    else ball.setX(px + pw + ball.getRadius() + 0.1);
                    ball.setVx(-ball.getVx());
                } else {
                    if (toPrevY < 0) ball.setY(py - ball.getRadius() - 0.1);
                    else ball.setY(py + ph + ball.getRadius() + 0.1);
                    ball.setVy(-ball.getVy());
                }
            } else {
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx < 0) ball.setX(closestX - ball.getRadius() - 0.1);
                    else ball.setX(closestX + ball.getRadius() + 0.1);
                    ball.setVx(-ball.getVx());
                } else {
                    if (dy < 0) ball.setY(closestY - ball.getRadius() - 0.1);
                    else ball.setY(closestY + ball.getRadius() + 0.1);
                    ball.setVy(-ball.getVy());
                }
            }

            break;
        }

        // Wall bounces (board boundaries) - visible board bounds
        double left = 400.0 - (board.getNumFiles() * 70) / 2.0;
        double right = left + board.getNumFiles() * 70.0;
        if (ball.getX() - ball.getRadius() <= left || ball.getX() + ball.getRadius() >= right) {
            ball.setVx(-ball.getVx());
        }
        if (ball.getY() - ball.getRadius() <= 100.0 || ball.getY() + ball.getRadius() >= 660.0) {
            ball.setVy(-ball.getVy());
        }
    }

    // resolve paddle collision; returns true if a collision was handled
    private boolean resolvePaddleCollision(Ball ball, PongPaddle paddle, double prevX, double prevY, double curX, double curY) {
        double px = paddle.getX();
        double py = paddle.getY();
        double pw = paddle.getWidth();
        double ph = paddle.getHeight();

        double segMinX = Math.min(prevX, curX) - ball.getRadius();
        double segMaxX = Math.max(prevX, curX) + ball.getRadius();
        double segMinY = Math.min(prevY, curY) - ball.getRadius();
        double segMaxY = Math.max(prevY, curY) + ball.getRadius();
        if (segMaxX < px || segMinX > px + pw || segMaxY < py || segMinY > py + ph) {
            return false;
        }

        double expandedPx = px - ball.getRadius();
        double expandedPy = py - ball.getRadius();
        double expandedPw = pw + 2 * ball.getRadius();
        double expandedPh = ph + 2 * ball.getRadius();

        boolean hitDuringMove = segmentIntersectsAABB(prevX, prevY, curX, curY, expandedPx, expandedPy, expandedPw, expandedPh);
        boolean overlapNow = circleIntersectsAABB(curX, curY, ball.getRadius(), px, py, pw, ph);
        if (!hitDuringMove && !overlapNow) return false;

        double closestX = clamp(curX, px, px + pw);
        double closestY = clamp(curY, py, py + ph);
        double dx = curX - closestX;
        double dy = curY - closestY;

        if (Math.abs(dx) < 1e-6 && Math.abs(dy) < 1e-6) {
            double centerX = px + pw / 2.0;
            double centerY = py + ph / 2.0;
            double toPrevX = prevX - centerX;
            double toPrevY = prevY - centerY;

            if (Math.abs(toPrevX) > Math.abs(toPrevY)) {
                // horizontal impact
                if (toPrevX < 0) {
                    ball.setX(px - ball.getRadius() - 0.1);
                    ball.setVx(-Math.abs(ball.getVx()));
                } else {
                    ball.setX(px + pw + ball.getRadius() + 0.1);
                    ball.setVx(Math.abs(ball.getVx()));
                }
            } else {
                // vertical impact
                if (toPrevY < 0) {
                    ball.setY(py - ball.getRadius() - 0.1);
                    ball.setVy(-Math.abs(ball.getVy()));
                } else {
                    ball.setY(py + ph + ball.getRadius() + 0.1);
                    ball.setVy(Math.abs(ball.getVy()));
                }
            }
        } else {
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx < 0) {
                    ball.setX(closestX - ball.getRadius() - 0.1);
                    ball.setVx(-Math.abs(ball.getVx()));
                } else {
                    ball.setX(closestX + ball.getRadius() + 0.1);
                    ball.setVx(Math.abs(ball.getVx()));
                }
            } else {
                if (dy < 0) {
                    ball.setY(closestY - ball.getRadius() - 0.1);
                    ball.setVy(-Math.abs(ball.getVy()));
                } else {
                    ball.setY(closestY + ball.getRadius() + 0.1);
                    ball.setVy(Math.abs(ball.getVy()));
                }
            }
        }

        return true;
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

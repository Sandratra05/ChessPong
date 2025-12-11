package com.chesspong.network;

import java.io.Serializable;

public class BallData implements Serializable {
    private static final long serialVersionUID = 1L;

    private double x;
    private double y;
    private double vx;
    private double vy;

    public BallData(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
}
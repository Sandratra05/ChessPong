package com.chesspong.network;

import java.io.Serializable;

public class PaddleData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int playerId;
    private double x;
    private double y;

    public PaddleData(int playerId, double x, double y) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
    }

    public int getPlayerId() { return playerId; }
    public double getX() { return x; }
    public double getY() { return y; }
}
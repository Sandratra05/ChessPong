package com.chesspong.network;

import java.io.Serializable;

public class PieceData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private int health;
    private boolean alive;
    private String pieceType;

    public PieceData(int x, int y, int health, boolean alive, String pieceType) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.alive = alive;
        this.pieceType = pieceType;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public boolean isAlive() { return alive; }
    public String getPieceType() { return pieceType; }
}
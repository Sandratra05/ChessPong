package com.chesspong.model;

public abstract class Piece {
    protected Joueur owner;
    protected int health;
    protected int x, y;
    protected char symbol;

    public Piece(Joueur owner, int health, int x, int y, char symbol) {
        this.owner = owner;
        this.health = health;
        this.x = x;
        this.y = y;
        this.symbol = symbol;
    }

    public Joueur getOwner() {
        return owner;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public char getSymbol() {
        return symbol;
    }
}

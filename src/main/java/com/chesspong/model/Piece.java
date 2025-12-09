package com.chesspong.model;

public abstract class Piece {
    protected Joueur owner;
    protected int health;
    protected int x, y;
    protected char symbol;
    protected double width;
    protected double height;

    public Piece(Joueur owner, int health, int x, int y, char symbol, double width, double height) {
        this.owner = owner;
        this.health = health;
        this.x = x;
        this.y = y;
        this.symbol = symbol;
        this.width = width;
        this.height = height;
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

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}

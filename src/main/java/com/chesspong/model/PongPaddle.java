package com.chesspong.model;

public class PongPaddle {
    private Joueur owner;
    private double x; // movable
    private double y; // fixed
    private double width;
    private double height;

    public PongPaddle(Joueur owner, double x, double y, double width, double height) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Joueur getOwner() {
        return owner;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    // Setters ajoutés pour permettre d'ajuster la taille dynamiquement
    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}

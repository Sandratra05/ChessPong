package com.chesspong.model;

public class Cell {
    private int x, y;
    private Piece piece;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.piece = null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isEmpty() {
        return piece == null;
    }
}

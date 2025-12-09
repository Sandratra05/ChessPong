package com.chesspong.model;

public class Queen extends Piece {
    public Queen(Joueur owner, int health, int x, int y) {
        super(owner, health, x, y, 'Q', 35, 45);
    }
}

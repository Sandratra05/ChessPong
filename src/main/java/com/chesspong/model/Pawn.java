package com.chesspong.model;

public class Pawn extends Piece {
    public Pawn(Joueur owner, int health, int x, int y) {
        super(owner, health, x, y, 'P');
    }
}

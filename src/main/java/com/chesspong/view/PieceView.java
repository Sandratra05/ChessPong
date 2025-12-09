package com.chesspong.view;

import com.chesspong.model.Piece;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PieceView extends Circle {
    private Piece piece;

    public PieceView(Piece piece) {
        super(15);
        this.piece = piece;
        setFill(piece.getOwner().isWhite() ? Color.WHITE : Color.BLACK);
    }

    public Piece getPiece() {
        return piece;
    }
}

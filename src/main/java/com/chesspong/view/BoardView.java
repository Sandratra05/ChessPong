package com.chesspong.view;

import com.chesspong.model.Board;
import com.chesspong.model.Cell;
import com.chesspong.model.Piece;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoardView extends Canvas {
    private Board board;
    private double cellSize = 50;

    public BoardView(Board board) {
        super(400, 400);
        this.board = board;
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.BEIGE);
        gc.fillRect(0, 0, 400, 400);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Cell cell = board.getCell(x, y);
                if ((x + y) % 2 == 0) {
                    gc.setFill(Color.WHEAT);
                } else {
                    gc.setFill(Color.SADDLEBROWN);
                }
                gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

                if (!cell.isEmpty()) {
                    Piece piece = cell.getPiece();
                    drawPiece(gc, piece, x * cellSize + cellSize / 2, y * cellSize + cellSize / 2);
                }
            }
        }
    }

    private void drawPiece(GraphicsContext gc, Piece piece, double centerX, double centerY) {
        gc.setFont(javafx.scene.text.Font.font(20));
        if (piece.getOwner().isWhite()) {
            gc.setFill(Color.WHITE);
        } else {
            gc.setFill(Color.BLACK);
        }
        gc.fillText(String.valueOf(piece.getSymbol()), centerX - 10, centerY + 7);
    }
}

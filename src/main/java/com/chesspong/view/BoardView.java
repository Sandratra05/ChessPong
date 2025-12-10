package com.chesspong.view;

import com.chesspong.model.Board;
import com.chesspong.model.Cell;
import com.chesspong.model.Piece;
import com.chesspong.model.Pawn;
import com.chesspong.model.King;
import com.chesspong.model.Queen;
import com.chesspong.model.Bishop;
import com.chesspong.model.Knight;
import com.chesspong.model.Rook;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class BoardView extends Canvas {
    private Board board;
    private double cellSize = 50;
    private Map<String, Image> pieceImages;

    public BoardView(Board board) {
        super(board.getNumFiles() * 50, 400);
        this.board = board;
        loadPieceImages();
    }

    private void loadPieceImages() {
        pieceImages = new HashMap<>();
        // Assumer que les images sont dans src/main/resources/images/
        // Vous devez ajouter les fichiers PNG correspondants
        pieceImages.put("Pawn_white", new Image(getClass().getResourceAsStream("/images/pawn_white.png")));
        pieceImages.put("Pawn_black", new Image(getClass().getResourceAsStream("/images/pawn_black.png")));
        pieceImages.put("King_white", new Image(getClass().getResourceAsStream("/images/king_white.png")));
        pieceImages.put("King_black", new Image(getClass().getResourceAsStream("/images/king_black.png")));
        pieceImages.put("Queen_white", new Image(getClass().getResourceAsStream("/images/queen_white.png")));
        pieceImages.put("Queen_black", new Image(getClass().getResourceAsStream("/images/queen_black.png")));
        pieceImages.put("Bishop_white", new Image(getClass().getResourceAsStream("/images/bishop_white.png")));
        pieceImages.put("Bishop_black", new Image(getClass().getResourceAsStream("/images/bishop_black.png")));
        pieceImages.put("Knight_white", new Image(getClass().getResourceAsStream("/images/knight_white.png")));
        pieceImages.put("Knight_black", new Image(getClass().getResourceAsStream("/images/knight_black.png")));
        pieceImages.put("Rook_white", new Image(getClass().getResourceAsStream("/images/rook_white.png")));
        pieceImages.put("Rook_black", new Image(getClass().getResourceAsStream("/images/rook_black.png")));
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        double width = board.getNumFiles() * cellSize;
        double height = 8 * cellSize;
        gc.setFill(Color.BEIGE);
        gc.fillRect(0, 0, width, height);

        // Ajouter la bordure noire
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, width, height);

        int start = (8 - board.getNumFiles()) / 2;
        for (int x = start; x < start + board.getNumFiles(); x++) {
            for (int y = 0; y < 8; y++) {
                Cell cell = board.getCell(x, y);
                if ((x + y) % 2 == 0) {
                    gc.setFill(Color.WHEAT);
                } else {
                    gc.setFill(Color.SADDLEBROWN);
                }
                double drawX = (x - start) * cellSize;
                double drawY = y * cellSize;
                gc.fillRect(drawX, drawY, cellSize, cellSize);

                if (!cell.isEmpty()) {
                    Piece piece = cell.getPiece();
                    drawPiece(gc, piece, drawX + cellSize / 2, drawY + cellSize / 2);
                }
            }
        }
    }

    private void drawPiece(GraphicsContext gc, Piece piece, double centerX, double centerY) {
        double x = centerX - piece.getWidth() / 2;
        double y = centerY - piece.getHeight() / 2;
        String key = piece.getClass().getSimpleName() + "_" + (piece.getOwner().isWhite() ? "white" : "black");
        Image img = pieceImages.get(key);
        if (img != null) {
            gc.drawImage(img, x, y, piece.getWidth(), piece.getHeight());
        } else {
            // Fallback aux formes si l'image n'est pas trouvée
            if (piece.getOwner().isWhite()) {
                gc.setFill(Color.WHITE);
            } else {
                gc.setFill(Color.BLACK);
            }
            if (piece instanceof Pawn) {
                gc.fillRect(x, y, piece.getWidth(), piece.getHeight());
            } else if (piece instanceof King) {
                gc.fillOval(x, y, piece.getWidth(), piece.getHeight());
            } else if (piece instanceof Queen) {
                gc.fillRoundRect(x, y, piece.getWidth(), piece.getHeight(), 10, 10);
            } else if (piece instanceof Bishop) {
                gc.fillPolygon(new double[]{x + piece.getWidth()/2, x, x + piece.getWidth()}, new double[]{y, y + piece.getHeight(), y + piece.getHeight()}, 3);
            } else if (piece instanceof Knight) {
                gc.fillRect(x, y, piece.getWidth(), piece.getHeight());
                gc.setFill(Color.GRAY);
                gc.fillOval(x + 5, y + 5, piece.getWidth() - 10, piece.getHeight() - 10);
            } else if (piece instanceof Rook) {
                gc.fillRect(x, y, piece.getWidth(), piece.getHeight());
                gc.setFill(Color.DARKGRAY);
                gc.fillRect(x + 5, y + 5, piece.getWidth() - 10, piece.getHeight() - 10);
            }
        }
    }
}

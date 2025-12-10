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
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.Map;

public class BoardView extends Canvas {
    private Board board;
    private double cellSize = 70;
    private Map<String, Image> pieceImages;

    public BoardView(Board board) {
        super(board.getNumFiles() * 70, 560); // 8 * 70 = 560
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
                    // Dessiner les indicateurs de vie seulement si la pièce est vivante
                    if (piece.isAlive()) {
                        drawHealthIndicator(gc, piece, drawX + cellSize / 2, drawY + cellSize / 2);
                    }
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

    /**
     * Dessine l'indicateur de vie d'une pièce de manière esthétique.
     * Affiche un seul cœur avec le nombre de vies à côté.
     */
    private void drawHealthIndicator(GraphicsContext gc, Piece piece, double centerX, double centerY) {
        drawSingleHeartWithNumber(gc, piece, centerX, centerY);
    }

    /**
     * Dessine un seul cœur avec le nombre de vies affiché à côté.
     * Position selon l'équipe : haut-gauche pour les blancs, bas-droite pour les noirs.
     */
    private void drawSingleHeartWithNumber(GraphicsContext gc, Piece piece, double centerX, double centerY) {
        int health = piece.getHealth();
        if (health <= 0) return; // Ne rien dessiner si la pièce est morte

        double heartSize = 10;
        boolean isWhite = piece.getOwner().isWhite();

        // Position selon l'équipe
        double heartX, heartY, textX, textY;

        if (isWhite) {
            // Pièces blanches (en bas de l'échiquier) : indicateur en haut à gauche de la case
            heartX = centerX - cellSize/2 + 5;
            heartY = centerY - cellSize/2 + 5;
            textX = heartX + heartSize + 2;
            textY = heartY + heartSize/2 + 3;
        } else {
            // Pièces noires (en haut de l'échiquier) : indicateur en bas à droite de la case
            heartX = centerX + cellSize/2 - heartSize - 15;
            heartY = centerY + cellSize/2 - heartSize - 5;
            textX = heartX + heartSize + 2;
            textY = heartY + heartSize/2 + 3;
        }

        // Dessiner le cœur
        drawHeart(gc, heartX, heartY, heartSize, isWhite);

        // Dessiner le nombre de vies à côté du cœur
        gc.setFill(isWhite ? Color.DARKBLUE : Color.DARKRED);
        gc.setFont(Font.font(12));
        String healthText = String.valueOf(health);
        gc.fillText(healthText, textX, textY);
    }

    /**
     * Dessine des petits cœurs pour représenter la vie.
     */
    private void drawHealthHearts(GraphicsContext gc, Piece piece, double centerX, double centerY) {
        int health = piece.getHealth();
        if (health <= 0) return; // Ne rien dessiner si la pièce est morte

        // Paramètres pour l'affichage des cœurs
        double heartSize = 6;
        double heartSpacing = 8;
        double totalWidth = Math.max(1, health) * heartSpacing - (heartSpacing - heartSize);
        double startX = centerX - totalWidth / 2;
        double heartY = centerY - piece.getHeight() / 2 - 12; // Au-dessus de la pièce

        // Limiter l'affichage à un maximum de 10 cœurs pour ne pas encombrer
        int heartsToShow = Math.min(health, 10);
        boolean showNumber = health > 10; // Afficher le nombre si > 10 vies

        for (int i = 0; i < heartsToShow; i++) {
            double heartX = startX + i * heartSpacing;
            drawHeart(gc, heartX, heartY, heartSize, piece.getOwner().isWhite());
        }

        // Si plus de 10 vies, afficher le nombre à côté
        if (showNumber) {
            gc.setFill(Color.DARKBLUE);
            gc.fillText("x" + health, startX + heartsToShow * heartSpacing + 2, heartY + heartSize);
        }
    }

    /**
     * Dessine une barre de vie moderne au-dessus de la pièce.
     */
    private void drawHealthBar(GraphicsContext gc, Piece piece, double centerX, double centerY) {
        int health = piece.getHealth();
        if (health <= 0) return;

        // Supposons qu'une pièce a une vie maximum (on peut l'ajouter à la classe Piece plus tard)
        int maxHealth = getMaxHealthForPiece(piece);

        // Paramètres de la barre
        double barWidth = 30;
        double barHeight = 4;
        double barX = centerX - barWidth / 2;
        double barY = centerY - piece.getHeight() / 2 - 10;

        // Couleurs selon le pourcentage de vie
        double healthPercent = (double) health / maxHealth;
        Color barColor;
        if (healthPercent > 0.6) {
            barColor = Color.LIMEGREEN;
        } else if (healthPercent > 0.3) {
            barColor = Color.ORANGE;
        } else {
            barColor = Color.CRIMSON;
        }

        // Dessiner le fond de la barre (gris)
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRoundRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2, 3, 3);

        // Dessiner le contour noir
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5);
        gc.strokeRoundRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2, 3, 3);

        // Dessiner la barre de vie actuelle
        double currentBarWidth = barWidth * healthPercent;
        gc.setFill(barColor);
        gc.fillRoundRect(barX, barY, currentBarWidth, barHeight, 2, 2);

        // Afficher le nombre de vies si > 1
        if (health > 1) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(8));
            String healthText = String.valueOf(health);
            gc.fillText(healthText, centerX - 3, barY - 2);
        }
    }

    /**
     * Détermine la vie maximum d'une pièce selon son type.
     * Vous pouvez ajuster ces valeurs selon votre jeu.
     */
    private int getMaxHealthForPiece(Piece piece) {
        // Valeurs par défaut selon le type de pièce
        if (piece instanceof Pawn) return 3;
        if (piece instanceof Rook) return 5;
        if (piece instanceof Knight) return 4;
        if (piece instanceof Bishop) return 4;
        if (piece instanceof Queen) return 7;
        if (piece instanceof King) return 10;
        return 3; // valeur par défaut
    }

    /**
     * Dessine un petit cœur pour représenter une vie.
     */
    private void drawHeart(GraphicsContext gc, double x, double y, double size, boolean isWhitePlayer) {
        // Couleur différente selon le joueur
        Color heartColor = isWhitePlayer ? Color.CRIMSON : Color.DARKRED;
        Color borderColor = isWhitePlayer ? Color.DARKRED : Color.RED;

        // Dessiner le contour du cœur
        gc.setStroke(borderColor);
        gc.setLineWidth(0.5);

        // Dessiner un cœur simplifié avec deux cercles et un triangle
        double halfSize = size / 2;
        double quarterSize = size / 4;

        // Remplir le cœur
        gc.setFill(heartColor);

        // Dessiner deux petits cercles pour le haut du cœur
        gc.fillOval(x - quarterSize, y, halfSize, halfSize);
        gc.fillOval(x + quarterSize, y, halfSize, halfSize);

        // Dessiner le triangle pour le bas du cœur
        double[] triangleX = {x - quarterSize, x + size - quarterSize, x + halfSize};
        double[] triangleY = {y + quarterSize, y + quarterSize, y + size};
        gc.fillPolygon(triangleX, triangleY, 3);

        // Contours
        gc.strokeOval(x - quarterSize, y, halfSize, halfSize);
        gc.strokeOval(x + quarterSize, y, halfSize, halfSize);
        gc.strokePolygon(triangleX, triangleY, 3);
    }
}

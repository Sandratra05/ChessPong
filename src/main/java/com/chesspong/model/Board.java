package com.chesspong.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Cell[][] cells = new Cell[8][8];
    private int numFiles;
    private Joueur white, black;

    public Board(int numFiles, Joueur white, Joueur black) {
        this.numFiles = numFiles;
        this.white = white;
        this.black = black;
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }

        int startFile = numFiles;

        // Place pieces for white (bottom)
        placePiecesForPlayer(white, startFile, 0, 1);

        // Place pieces for black (top)
        placePiecesForPlayer(black, startFile, 7, 6);
    }

    private void placePiecesForPlayer(Joueur player, int numFiles, int majorRank, int pawnRank) {

        // Tableau standard des pièces d'échecs (colonne 0 → 7)
        Class<?>[] pieceOrder = new Class<?>[]{
                Rook.class, Knight.class, Bishop.class, Queen.class,
                King.class, Bishop.class, Knight.class, Rook.class
        };

        // Prix standard
        int[] pieceValues = new int[]{80, 60, 75, 100, 100, 75, 60, 80};

        // Début pour centrer les pièces réduites
        int start = (8 - numFiles) / 2;

        for (int i = 0; i < numFiles; i++) {
            int col = start + i;

            Class<?> pieceClass = pieceOrder[col];
            int value = pieceValues[col];

            Piece p = null;

            try {
                // création dynamique : new Rook(player, value, col, majorRank) etc.
                p = (Piece) pieceClass
                        .getConstructor(Joueur.class, int.class, int.class, int.class)
                        .newInstance(player, value, col, majorRank);

            } catch (Exception e) {
                e.printStackTrace();
            }

            cells[col][majorRank].setPiece(p);

            // Place les pions correspondant aux colonnes actives
            cells[col][pawnRank].setPiece(new Pawn(player, 50, col, pawnRank));
        }
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) return null;
        return cells[x][y];
    }

    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!cells[i][j].isEmpty()) {
                    pieces.add(cells[i][j].getPiece());
                }
            }
        }
        return pieces;
    }

    public void removePiece(int x, int y) {
        cells[x][y].setPiece(null);
    }
}

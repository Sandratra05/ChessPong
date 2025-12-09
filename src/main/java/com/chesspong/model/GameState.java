package com.chesspong.model;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private Board board;
    private Joueur player1, player2;
    private Joueur currentPlayer;
    private Ball ball;
    private PongPaddle paddle1, paddle2;
    private boolean gameOver;
    private Joueur winner;
    private int selectedPieceTypes; // Nouveau champ pour le nombre de types sélectionnés
    private Map<String, Integer> pieceLives; // Nouveau champ pour stocker les vies par type de pièce

    public GameState(int numFiles, Joueur player1, Joueur player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Board(numFiles, player1, player2);
        this.currentPlayer = player1; // or random
        // initialize ball and paddles
        this.ball = new Ball(400, 300, 1, 1, 10); // example
        this.paddle1 = new PongPaddle(player1, 350, 200, 100, 10); // in front of white pawns
        this.paddle2 = new PongPaddle(player2, 350, 390, 100, 10); // in front of black pawns
        this.gameOver = false;
        this.pieceLives = new HashMap<>();
        this.selectedPieceTypes = numFiles; // Utiliser numFiles comme nombre de types sélectionnés

        // Attribution des vies selon la sélection
        assignLives();

        // Mettre à jour la santé des pièces selon les vies choisies
        updatePieceHealths();
    }

    // Méthode pour attribuer les vies selon le nombre sélectionné
    private void assignLives() {
        // Collecter les types de pièces selon la sélection
        List<String> pieceTypes = new ArrayList<>();
        pieceTypes.add("Pawn");
        pieceTypes.add("King");
        pieceTypes.add("Queen");
        if (selectedPieceTypes >= 4) {
            pieceTypes.add("Bishop");
        }
        if (selectedPieceTypes >= 6) {
            pieceTypes.add("Knight");
        }
        if (selectedPieceTypes >= 8) {
            pieceTypes.add("Rook");
        }

        // Créer un panneau pour le formulaire
        JPanel panel = new JPanel(new GridLayout(pieceTypes.size(), 2, 5, 5));
        Map<String, JTextField> fields = new HashMap<>();
        for (String type : pieceTypes) {
            panel.add(new JLabel("Vie pour les " + type + "s :"));
            JTextField field = new JTextField("", 5);
            panel.add(field);
            fields.put(type, field);
        }

        // Afficher le formulaire
        int result = JOptionPane.showConfirmDialog(null, panel, "Configuration des vies", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            for (String type : pieceTypes) {
                JTextField field = fields.get(type);
                String input = field.getText().trim();
                try {
                    int life = Integer.parseInt(input);
                    pieceLives.put(type, life);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valeur invalide pour " + type + ". Utilisation de 1 par défaut.");
                    pieceLives.put(type, 1);
                }
            }
        } else {
            // Valeurs par défaut si annulé
            for (String type : pieceTypes) {
                pieceLives.put(type, 1);
            }
        }
    }

    // Nouvelle méthode pour mettre à jour la santé des pièces
    private void updatePieceHealths() {
        for (Map.Entry<String, Integer> entry : pieceLives.entrySet()) {
            String type = entry.getKey();
            int life = entry.getValue();

            // Mettre à jour la santé de chaque pièce sur le plateau
            board.getAllPieces().stream()
                    .filter(piece -> piece.getClass().getSimpleName().equals(type))
                    .forEach(piece -> piece.setHealth(life));
        }
    }

    // Getter pour les vies des pièces
    public Map<String, Integer> getPieceLives() {
        return pieceLives;
    }

    public Board getBoard() {
        return board;
    }

    public Joueur getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Joueur currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Ball getBall() {
        return ball;
    }

    public PongPaddle getPaddle1() {
        return paddle1;
    }

    public PongPaddle getPaddle2() {
        return paddle2;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Joueur getWinner() {
        return winner;
    }

    public void setWinner(Joueur winner) {
        this.winner = winner;
    }

    public void checkWinCondition() {
        boolean king1Present = board.getAllPieces().stream()
                .anyMatch(piece -> piece instanceof King && piece.getOwner().equals(player1));
        if (!king1Present) {
            System.out.println("Le roi de " + player1.getName() + " est mort !");
            gameOver = true;
            winner = player2;
            JOptionPane.showMessageDialog(null, "La partie est terminée ! " + player2.getName() + " a gagné.", "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Vérifier si le roi de player2 est absent de l'échiquier
        boolean king2Present = board.getAllPieces().stream()
                .anyMatch(piece -> piece instanceof King && piece.getOwner().equals(player2));
        if (!king2Present) {
            System.out.println("Le roi de " + player2.getName() + " est mort !");
            gameOver = true;
            winner = player1;
            JOptionPane.showMessageDialog(null, "La partie est terminée ! " + player1.getName() + " a gagné.", "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

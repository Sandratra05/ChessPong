package com.chesspong.model;

import javax.swing.*;

public class GameState {
    private Board board;
    private Joueur player1, player2;
    private Joueur currentPlayer;
    private Ball ball;
    private PongPaddle paddle1, paddle2;
    private boolean gameOver;
    private Joueur winner;

    public GameState(int numFiles, Joueur player1, Joueur player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Board(numFiles, player1, player2);
        this.currentPlayer = player1; // or random
        // initialize ball and paddles
        this.ball = new Ball(400, 300, 3, 3, 10); // example
        this.paddle1 = new PongPaddle(player1, 350, 200, 100, 10); // in front of white pawns
        this.paddle2 = new PongPaddle(player2, 350, 390, 100, 10); // in front of black pawns
        this.gameOver = false;
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

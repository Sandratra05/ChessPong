package com.chesspong.model;

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
        // check if a king has 0 health
        for (Piece piece : board.getAllPieces()) {
            if (piece instanceof King && piece.getHealth() <= 0) {
                System.out.println("Le roi est mort !");
                gameOver = true;
                winner = piece.getOwner() == player1 ? player2 : player1;
            }
        }
    }
}

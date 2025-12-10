package com.chesspong.network;

import java.io.Serializable;
import java.util.List;

public class GameStateData implements Serializable {
    private static final long serialVersionUID = 1L;

    private BallData ballData;
    private PaddleData paddle1Data;
    private PaddleData paddle2Data;
    private List<PieceData> piecesData;
    private int score1;
    private int score2;

    public GameStateData(BallData ballData, PaddleData paddle1Data, PaddleData paddle2Data,
                         List<PieceData> piecesData, int score1, int score2) {
        this.ballData = ballData;
        this.paddle1Data = paddle1Data;
        this.paddle2Data = paddle2Data;
        this.piecesData = piecesData;
        this.score1 = score1;
        this.score2 = score2;
    }

    public BallData getBallData() { return ballData; }
    public PaddleData getPaddle1Data() { return paddle1Data; }
    public PaddleData getPaddle2Data() { return paddle2Data; }
    public List<PieceData> getPiecesData() { return piecesData; }
    public int getScore1() { return score1; }
    public int getScore2() { return score2; }
}
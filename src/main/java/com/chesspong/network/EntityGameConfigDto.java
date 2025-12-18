package com.chesspong.network;

public class EntityGameConfigDto {
    private Long id;
    private Integer startFile;
    private Integer pawnLives;
    private Integer knightLives;
    private Integer bishopLives;
    private Integer rookLives;
    private Integer queenLives;
    private Integer kingLives;
    private Integer power;
    private Integer capacity;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStartFile() { return startFile; }
    public void setStartFile(Integer startFile) { this.startFile = startFile; }

    public Integer getPawnLives() { return pawnLives; }
    public void setPawnLives(Integer pawnLives) { this.pawnLives = pawnLives; }

    public Integer getKnightLives() { return knightLives; }
    public void setKnightLives(Integer knightLives) { this.knightLives = knightLives; }

    public Integer getBishopLives() { return bishopLives; }
    public void setBishopLives(Integer bishopLives) { this.bishopLives = bishopLives; }

    public Integer getRookLives() { return rookLives; }
    public void setRookLives(Integer rookLives) { this.rookLives = rookLives; }

    public Integer getQueenLives() { return queenLives; }
    public void setQueenLives(Integer queenLives) { this.queenLives = queenLives; }

    public Integer getKingLives() { return kingLives; }
    public void setKingLives(Integer kingLives) { this.kingLives = kingLives; }

    public Integer getPower() { return power; }
    public void setPower(Integer power) { this.power = power; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
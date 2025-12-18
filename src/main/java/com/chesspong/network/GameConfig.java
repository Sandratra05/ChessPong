package com.chesspong.network;

import java.io.Serializable;
import java.util.Map;

public class GameConfig implements Serializable {
    private static final long serialVersionUID = 1L; // Ajout pour la sérialisation
    private int numFiles;
    private Map<String, Integer> pieceLives;
    private Long id;
    private Integer capacity;
    private Integer power;

    public GameConfig(int numFiles, Map<String, Integer> pieceLives) {
        this.numFiles = numFiles;
        this.pieceLives = pieceLives;
    }

    public GameConfig(int numFiles, Map<String, Integer> pieceLives, Integer capacity) {
        this.numFiles = numFiles;
        this.pieceLives = pieceLives;
        this.capacity = capacity;
    }

    // Getters et setters existants...
    public int getNumFiles() { return numFiles; }
    public Map<String, Integer> getPieceLives() { return pieceLives; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getPower() { return power; }
    public void setPower(Integer power) { this.power = power; }
}
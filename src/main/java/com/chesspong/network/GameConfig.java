package com.chesspong.network;

import java.io.Serializable;
import java.util.Map;

public class GameConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numFiles;
    private final Map<String, Integer> pieceLives;

    public GameConfig(int numFiles, Map<String, Integer> pieceLives) {
        this.numFiles = numFiles;
        this.pieceLives = pieceLives;
    }

    public int getNumFiles() {
        return numFiles;
    }

    public Map<String, Integer> getPieceLives() {
        return pieceLives;
    }
}
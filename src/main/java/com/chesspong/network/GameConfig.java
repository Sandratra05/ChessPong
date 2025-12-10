package com.chesspong.network;

import java.io.Serializable;

public class GameConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numFiles;

    public GameConfig(int numFiles) {
        this.numFiles = numFiles;
    }

    public int getNumFiles() {
        return numFiles;
    }
}
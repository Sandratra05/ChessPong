package com.chesspong.network;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        PADDLE_UPDATE,
        BALL_UPDATE,
        PIECE_UPDATE,
        GAME_STATE,
        PLAYER_READY,
        GAME_START,
        GAME_OVER,
        PING,
        GAME_CONFIG
    }

    private MessageType type;
    private Object data;
    private long timestamp;

    public NetworkMessage(MessageType type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public MessageType getType() { return type; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }
}
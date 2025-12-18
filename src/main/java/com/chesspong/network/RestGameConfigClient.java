package com.chesspong.network;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RestGameConfigClient {
    private final String baseUrl;
    private final Gson gson = new Gson();

    public RestGameConfigClient(String baseUrl) { this.baseUrl = baseUrl; }

    // POST et retourne l'id créé
    public long postConfigAndReturnId(GameConfig config) throws IOException {
        EntityGameConfigDto dto = mapToDto(config);
        String json = gson.toJson(dto);
        URL url = new URL(baseUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int code = con.getResponseCode();
            if (code < 200 || code >= 300) {
                String resp = readStream(con.getErrorStream());
                throw new IOException("Erreur POST config : HTTP " + code + " - " + resp);
            }
            String body = readStream(con.getInputStream());
            EntityGameConfigDto saved = gson.fromJson(body, EntityGameConfigDto.class);
            return saved != null && saved.getId() != null ? saved.getId() : -1L;
        } finally {
            con.disconnect();
        }
    }

    // Compat: ancien nom
    public void postConfig(GameConfig config) throws IOException {
        postConfigAndReturnId(config);
    }

    public GameConfig fetchLatest() throws IOException {
        URL url = new URL(baseUrl + "/latest");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int code = con.getResponseCode();
            if (code == 404) return null;
            if (code < 200 || code >= 300) {
                String resp = readStream(con.getErrorStream());
                throw new IOException("Erreur GET latest : HTTP " + code + " - " + resp);
            }
            String body = readStream(con.getInputStream());
            EntityGameConfigDto dto = gson.fromJson(body, EntityGameConfigDto.class);
            return mapToNetworkConfig(dto);
        } finally {
            con.disconnect();
        }
    }

    public void updateCapacity(long id, int capacity) throws IOException {
        URL url = new URL(baseUrl + "/" + id + "/capacity");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            String json = "{\"capacity\":" + capacity + "}";
            try (OutputStream os = con.getOutputStream()) {
                os.write(json.getBytes("utf-8"));
            }
            int code = con.getResponseCode();
            if (code < 200 || code >= 300) {
                String resp = readStream(con.getErrorStream());
                throw new IOException("Erreur PUT capacity : HTTP " + code + " - " + resp);
            }
        } finally {
            con.disconnect();
        }
    }

    public void updatePower(long id, int power) throws IOException {
        URL url = new URL(baseUrl + "/" + id + "/power");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            String json = "{\"power\":" + power + "}";
            try (OutputStream os = con.getOutputStream()) {
                os.write(json.getBytes("utf-8"));
            }
            int code = con.getResponseCode();
            if (code < 200 || code >= 300) {
                String resp = readStream(con.getErrorStream());
                throw new IOException("Erreur PUT power : HTTP " + code + " - " + resp);
            }
        } finally {
            con.disconnect();
        }
    }

    private static String readStream(InputStream in) throws IOException {
        if (in == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private GameConfig mapToNetworkConfig(EntityGameConfigDto dto) {
        if (dto == null) return null;
        Map<String, Integer> pieceLives = new HashMap<>();
        if (dto.getPawnLives() != null) pieceLives.put("pawn", dto.getPawnLives());
        if (dto.getKnightLives() != null) pieceLives.put("knight", dto.getKnightLives());
        if (dto.getBishopLives() != null) pieceLives.put("bishop", dto.getBishopLives());
        if (dto.getRookLives() != null) pieceLives.put("rook", dto.getRookLives());
        if (dto.getQueenLives() != null) pieceLives.put("queen", dto.getQueenLives());
        if (dto.getKingLives() != null) pieceLives.put("king", dto.getKingLives());
        int numFiles = dto.getStartFile() != null ? dto.getStartFile() : 0;
        GameConfig cfg = new GameConfig(numFiles, pieceLives);
        cfg.setId(dto.getId());
        cfg.setCapacity(dto.getCapacity());
        cfg.setPower(dto.getPower());
        return cfg;
    }

    private EntityGameConfigDto mapToDto(GameConfig config) {
        EntityGameConfigDto dto = new EntityGameConfigDto();
        dto.setStartFile(config.getNumFiles());
        dto.setCapacity(config.getCapacity());
        dto.setPower(config.getPower());
        Map<String, Integer> map = config.getPieceLives();
        if (map == null) return dto;
        dto.setPawnLives(getIgnoreCase(map, "pawn", "PAWN", "Pion", "pawnLives"));
        dto.setKnightLives(getIgnoreCase(map, "knight", "KNIGHT", "Knight", "knightLives"));
        dto.setBishopLives(getIgnoreCase(map, "bishop", "BISHOP", "bishopLives"));
        dto.setRookLives(getIgnoreCase(map, "rook", "ROOK", "rookLives"));
        dto.setQueenLives(getIgnoreCase(map, "queen", "QUEEN", "queenLives"));
        dto.setKingLives(getIgnoreCase(map, "king", "KING", "kingLives"));
        return dto;
    }

    private Integer getIgnoreCase(Map<String, Integer> map, String... keys) {
        for (String k : keys) {
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                if (e.getKey().equalsIgnoreCase(k)) return e.getValue();
            }
        }
        return null;
    }
}
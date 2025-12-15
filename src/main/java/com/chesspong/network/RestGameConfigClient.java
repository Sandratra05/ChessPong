package com.chesspong.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RestGameConfigClient {
    private final String baseUrl; // ex: "http://localhost:8080/ChessPongConfigREST/api/configs"
    private final Gson gson = new Gson();

    public RestGameConfigClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // POST la configuration (utiliser depuis l'hôte)
    public void postConfig(GameConfig config) throws IOException {
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
        } finally {
            con.disconnect();
        }
    }

    // GET /latest -> renvoie com.chesspong.network.GameConfig (utiliser depuis le client)
    public GameConfig fetchLatest() throws IOException {
        URL url = new URL(baseUrl + "/latest");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int code = con.getResponseCode();
            if (code == 404) {
                return null;
            }
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

    private static String readStream(InputStream in) throws IOException {
        if (in == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    // mapping DTO -> network.GameConfig
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
        return new GameConfig(numFiles, pieceLives);
    }

    // mapping network.GameConfig -> DTO attendu par le REST EJB
    private EntityGameConfigDto mapToDto(GameConfig config) {
        EntityGameConfigDto dto = new EntityGameConfigDto();
        dto.setStartFile(config.getNumFiles());
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
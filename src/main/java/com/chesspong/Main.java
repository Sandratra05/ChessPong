package com.chesspong;

import com.chesspong.controller.GameController;
import com.chesspong.network.GameConfig;
import com.chesspong.network.NetworkManager;
import com.chesspong.network.RestGameConfigClient;
import com.chesspong.view.NetworkMenuView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private NetworkManager networkManager;
    private GameController gameController;
    private RestGameConfigClient restClient;
    private int numFiles = 8; // Configuration par défaut

    private static final String REST_SERVICE_PORT = "8084";
    private static final String REST_SERVICE_PATH = "/ChessPongConfigREST-1.0-SNAPSHOT/api/configs";


    @Override
    public void start(Stage primaryStage) {
        networkManager = new NetworkManager();

        NetworkMenuView menuView = new NetworkMenuView(primaryStage);
        menuView.setListener(new NetworkMenuView.NetworkMenuListener() {
            @Override
            public void onHostGame(int port) {
                // REST sur la machine qui héberge (local)
                ensureRestClient("127.0.0.1");

                // Configurer les paramètres avant d'héberger
                if (!configureGameParameters()) {
                    menuView.show(); // Retour au menu si annulation
                    return;
                }

                networkManager.startAsHost(port, () -> {
                    Platform.runLater(() -> {
                        showInfo("Client connecté! Envoi de la configuration...");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        showInfo("Configuration envoyée! Démarrage de la partie...");
                        startGame(primaryStage, true);
                        networkManager.sendGameConfig(numFiles, gameController.getGameState().getPieceLives());

                        // Persist via REST (appel non bloquant)
                        new Thread(() -> {
                            try {
                                GameConfig cfg = new GameConfig(numFiles, gameController.getGameState().getPieceLives());
                                restClient.postConfig(cfg);
                            } catch (IOException e) {
                                System.err.println("Échec de l'enregistrement REST de la configuration : " + e.getMessage());
                            }
                        }, "rest-game-config-uploader").start();
                    });
                });
                showInfo("Configuration: " + numFiles + " types de pièces\nEn attente d'un client sur le port " + port + "...");
            }

            @Override
            public void onJoinGame(String host, int port) {
                // LE CLIENT N'A PAS DE CONFIGURATION - il attend celle du serveur
                showInfo("Connexion au serveur...");

                setupClientNetworkListener(primaryStage);

                // s'assurer que le restClient pointe vers l'host réseau saisi par l'utilisateur
                ensureRestClient(host);

                networkManager.connectAsClient(host, port, () -> {
                    // tenter de récupérer la configuration persistée via REST (non bloquant)
                    new Thread(() -> {
                        try {
                            GameConfig cfg = restClient.fetchLatest();
                            if (cfg != null) {
                                Platform.runLater(() -> {
                                    numFiles = cfg.getNumFiles();
                                    showInfo("Configuration REST reçue: " + numFiles + " types de pièces\nDémarrage de la partie...");
                                    startGame(primaryStage, false);
                                    if (cfg.getPieceLives() != null) {
                                        gameController.getGameState().setPieceLives(cfg.getPieceLives());
                                    }
                                });
                                return;
                            }
                        } catch (IOException e) {
                            System.err.println("Erreur REST fetchLatest : " + e.getMessage());
                        }
                        // Si pas de config REST, on attend la config via le réseau comme avant
                        Platform.runLater(() -> {
                            showInfo("Connecté! En attente de la configuration de l'hôte...");
                        });
                    }, "rest-game-config-fetcher").start();
                });
            }

            @Override
            public void onPlayLocal() {
                // Configurer les paramètres avant de jouer en local
                if (!configureGameParameters()) {
                    menuView.show(); // Retour au menu si annulation
                    return;
                }

                // REST sur localhost pour jeu local
                ensureRestClient("127.0.0.1");

                // Persist via REST (appel non bloquant)
                new Thread(() -> {
                    try {
                        GameConfig cfg = new GameConfig(numFiles, gameController.getGameState().getPieceLives());
                        restClient.postConfig(cfg);
                    } catch (IOException e) {
                        System.err.println("Échec de l'enregistrement REST de la configuration : " + e.getMessage());
                    }
                }, "rest-game-config-uploader").start();

                System.out.println("----------- EN LOCAL OO ---------");
                startGame(primaryStage, null);
            }
        });

        menuView.show();
    }

    private boolean configureGameParameters() {
        String[] options = {"2", "4", "6", "8"};
        String selected = (String) JOptionPane.showInputDialog(
            null,
            "Choisissez le nombre de types de pièces :",
            "Configuration de la partie",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            String.valueOf(numFiles)
        );

        if (selected != null) {
            numFiles = Integer.parseInt(selected);
            return true;
        }
        return false; // L'utilisateur a annulé
    }

    private void startGame(Stage stage, Boolean isHost) {
        gameController = new GameController(stage, numFiles, networkManager, isHost);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public void stop() {
        if (networkManager != null) {
            networkManager.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // construit ou reconstruit restClient à partir d'un host/hostname/IP fourni
    private void ensureRestClient(String hostOrUrl) {
        if (hostOrUrl == null) return;
        String h = hostOrUrl.trim();
        if (h.isEmpty()) return;
        // enlever http(s):// et path/port s'ils existent
        if (h.startsWith("http://")) h = h.substring(7);
        if (h.startsWith("https://")) h = h.substring(8);
        int slash = h.indexOf('/');
        if (slash > -1) h = h.substring(0, slash);
        int colon = h.indexOf(':');
        if (colon > -1) h = h.substring(0, colon);
        String baseUrl = "http://" + h + ":" + REST_SERVICE_PORT + REST_SERVICE_PATH;
        restClient = new RestGameConfigClient(baseUrl);
        System.out.println("REST client configuré vers : " + baseUrl);
    }

    private void setupClientNetworkListener(Stage primaryStage) {
        networkManager.setUpdateListener(new NetworkManager.NetworkUpdateListener() {

            @Override
            public void onPaddleUpdate(int playerId, double x, double y) {
                // Sera géré par le GameController une fois créé
            }

            @Override
            public void onBallUpdate(double x, double y, double vx, double vy) {
                // Sera géré par le GameController une fois créé
            }

            @Override
            public void onGameStateUpdate(com.chesspong.network.GameStateData gameStateData) {
                // Sera géré par le GameController une fois créé
            }

            @Override
            public void onGameConfigReceived(com.chesspong.network.GameConfig gameConfig) {
                Platform.runLater(() -> {
                    numFiles = gameConfig.getNumFiles();
                    showInfo("Configuration reçue: " + numFiles + " types de pièces\nDémarrage de la partie...");
                    startGame(primaryStage, false);
                    // Appliquer immédiatement les vies envoyées par l’hôte
                    if (gameConfig.getPieceLives() != null) {
                        gameController.getGameState().setPieceLives(gameConfig.getPieceLives());
                    }
                });
            }
        });
    }

//    // Fenêtre de saisie des vies (retourne null si annulation)
//    private List<Integer> configurePieceHealths(int numFiles) {
//        List<Integer> healths = new ArrayList<>();
//        for (int i = 1; i <= numFiles; i++) {
//            String input = JOptionPane.showInputDialog(null, "Vie pour le type de pièce " + i + " :");
//            if (input == null) return null; // Annulation
//            try {
//                healths.add(Integer.parseInt(input));
//            } catch (NumberFormatException e) {
//                return null; // Erreur -> annuler pour simplicité
//            }
//        }
//        return healths;
//    }
}
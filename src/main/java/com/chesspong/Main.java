package com.chesspong;

import com.chesspong.controller.GameController;
import com.chesspong.network.NetworkManager;
import com.chesspong.view.NetworkMenuView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private NetworkManager networkManager;
    private GameController gameController;
    private int numFiles = 8; // Configuration par défaut

    @Override
    public void start(Stage primaryStage) {
        networkManager = new NetworkManager();

        NetworkMenuView menuView = new NetworkMenuView(primaryStage);
        menuView.setListener(new NetworkMenuView.NetworkMenuListener() {
            @Override
            public void onHostGame(int port) {
                // Configurer les paramètres avant d'héberger
                if (!configureGameParameters()) {
                    menuView.show(); // Retour au menu si annulation
                    return;
                }

                networkManager.startAsHost(port, () -> {
                    Platform.runLater(() -> {
                        showInfo("Client connecté! Envoi de la configuration...");
                        networkManager.sendGameConfig(numFiles);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        showInfo("Configuration envoyée! Démarrage de la partie...");
                        startGame(primaryStage, true);
                    });
                });
                showInfo("Configuration: " + numFiles + " types de pièces\nEn attente d'un client sur le port " + port + "...");
            }

            @Override
            public void onJoinGame(String host, int port) {
                // LE CLIENT N'A PAS DE CONFIGURATION - il attend celle du serveur
                showInfo("Connexion au serveur...");

                setupClientNetworkListener(primaryStage);

                networkManager.connectAsClient(host, port, () -> {
                    Platform.runLater(() -> {
                        showInfo("Connecté! En attente de la configuration de l'hôte...");
                    });
                });
            }

            @Override
            public void onPlayLocal() {
                // Configurer les paramètres avant de jouer en local
                if (!configureGameParameters()) {
                    menuView.show(); // Retour au menu si annulation
                    return;
                }

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
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

public class Main extends Application {
    private NetworkManager networkManager;
    private GameController gameController;
    private RestGameConfigClient restClient;
    private int numFiles = 8;
    private int capacity = 10; // nouvelle saisie
    private volatile Long currentConfigId;

    private static final String REST_SERVICE_PORT = "8084";
    private static final String REST_SERVICE_PATH = "/ChessPongConfigREST-1.0-SNAPSHOT/api/configs";

    @Override
    public void start(Stage primaryStage) {
        networkManager = new NetworkManager();

        NetworkMenuView menuView = new NetworkMenuView(primaryStage);
        menuView.setListener(new NetworkMenuView.NetworkMenuListener() {
            @Override
            public void onHostGame(int port) {
                ensureRestClient("127.0.0.1");

                if (!configureGameParameters()) {
                    menuView.show();
                    return;
                }

                networkManager.startAsHost(port, () -> {
                    Platform.runLater(() -> {
                        startGame(primaryStage, true);
                        // pousser la capacité dans le gameplay
                        gameController.applyCapacity(capacity);
                        // envoyer la config réseau uniquement APRES que les vies soient configurées
                        new Thread(() -> {
                            try {
                                int tries = 0;
                                while (tries < 200) { // ~10s max
                                    if (gameController.getGameState().isLivesConfigured()) break;
                                    Thread.sleep(50);
                                    tries++;
                                }
                            } catch (InterruptedException ignored) {}
                            Platform.runLater(() -> networkManager.sendGameConfig(numFiles, gameController.getGameState().getPieceLives()));
                        }, "send-config-after-lives").start();

                        // Persist via REST en récupérant l'id
                        new Thread(() -> {
                            try {
                                GameConfig cfg = new GameConfig(numFiles, gameController.getGameState().getPieceLives(), capacity);
                                long id = restClient.postConfigAndReturnId(cfg);
                                currentConfigId = id;
                                gameController.setConfigPersistence(restClient, id);
                            } catch (IOException e) {
                                System.err.println("Échec de l'enregistrement REST de la configuration : " + e.getMessage());
                            }
                        }, "rest-game-config-uploader").start();
                    });
                });
            }

            @Override
            public void onJoinGame(String host, int port) {
//                showInfo("Connexion au serveur...");
                setupClientNetworkListener(primaryStage);
                ensureRestClient(host);

                networkManager.connectAsClient(host, port, () -> {
                    Platform.runLater(() -> showInfo("Connecté au serveur. En attente de la configuration du serveur..."));
                });

            }

            @Override
            public void onPlayLocal() {
                if (!configureGameParameters()) {
                    menuView.show();
                    return;
                }
                ensureRestClient("127.0.0.1");

                startGame(primaryStage, null);
                gameController.applyCapacity(capacity);

                new Thread(() -> {
                    try {
                        GameConfig cfg = new GameConfig(numFiles, gameController.getGameState().getPieceLives(), capacity);
                        long id = restClient.postConfigAndReturnId(cfg);
                        currentConfigId = id;
                        gameController.setConfigPersistence(restClient, id);
                    } catch (IOException e) {
                        System.err.println("Échec de l'enregistrement REST de la configuration : " + e.getMessage());
                    }
                }, "rest-game-config-uploader").start();
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
        if (selected == null) return false;
        numFiles = Integer.parseInt(selected);

        String capStr = JOptionPane.showInputDialog(null, "Capacité de la jauge (entier > 0) :", String.valueOf(capacity));
        if (capStr == null) return false;
        try {
            capacity = Math.max(1, Integer.parseInt(capStr.trim()));
        } catch (NumberFormatException e) {
            capacity = 10;
        }
        return true;
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

    private void ensureRestClient(String hostOrUrl) {
        if (hostOrUrl == null) return;
        String h = hostOrUrl.trim();
        if (h.isEmpty()) return;
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
            @Override public void onPaddleUpdate(int playerId, double x, double y) {}
            @Override public void onBallUpdate(double x, double y, double vx, double vy) {}
            @Override public void onGameStateUpdate(com.chesspong.network.GameStateData gameStateData) {}
            @Override
            public void onGameConfigReceived(com.chesspong.network.GameConfig gameConfig) {
                System.out.println("[Client] GAME_CONFIG reçu via socket, vérification contenu...");
                // Assurer que la config contient bien les vies; sinon ignorer car incomplète
                if (gameConfig.getPieceLives() == null || gameConfig.getPieceLives().isEmpty()) {
                    System.out.println("[Client] GAME_CONFIG reçu mais pieceLives vide -> attente de la vraie config.");
                    return;
                }
                Platform.runLater(() -> {
                    System.out.println("[Client] GAME_CONFIG valide, ouverture de la fenêtre client.");
                    numFiles = gameConfig.getNumFiles();
                    startGame(primaryStage, false);
                    if (gameConfig.getPieceLives() != null) {
                        gameController.getGameState().setPieceLives(gameConfig.getPieceLives());
                    }
                    if (gameConfig.getCapacity() != null) {
                        gameController.applyCapacity(gameConfig.getCapacity());
                    }
                    if (gameConfig.getId() != null) {
                        gameController.setConfigPersistence(restClient, gameConfig.getId());
                    }
                });
            }
        });
    }
}

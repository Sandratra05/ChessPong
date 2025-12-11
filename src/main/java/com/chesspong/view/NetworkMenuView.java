package com.chesspong.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NetworkMenuView {
    private Stage stage;
    private NetworkMenuListener listener;

    public interface NetworkMenuListener {
        void onHostGame(int port);
        void onJoinGame(String host, int port);
        void onPlayLocal();
    }

    public NetworkMenuView(Stage stage) {
        this.stage = stage;
    }

    public void setListener(NetworkMenuListener listener) {
        this.listener = listener;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));

        Label title = new Label("ChessPong - Menu Réseau");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button hostButton = new Button("Héberger une partie");
        hostButton.setPrefWidth(200);
        hostButton.setOnAction(e -> showHostDialog());

        Button joinButton = new Button("Rejoindre une partie");
        joinButton.setPrefWidth(200);
        joinButton.setOnAction(e -> showJoinDialog());

        Button localButton = new Button("Jouer en local");
        localButton.setPrefWidth(200);
        localButton.setOnAction(e -> {
            if (listener != null) {
                listener.onPlayLocal();
            }
        });

        root.getChildren().addAll(title, hostButton, joinButton, localButton);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("ChessPong");
        stage.show();
    }

    private void showHostDialog() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Héberger une partie");
        dialog.setHeaderText("Entrez le port pour héberger le serveur");

        ButtonType hostButtonType = new ButtonType("Héberger", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(hostButtonType, ButtonType.CANCEL);

        TextField portField = new TextField("5555");
        portField.setPromptText("Port (ex: 5555)");

        VBox content = new VBox(10);
        content.getChildren().add(new Label("Port:"));
        content.getChildren().add(portField);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == hostButtonType) {
                try {
                    return Integer.parseInt(portField.getText());
                } catch (NumberFormatException e) {
                    return 5555;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(port -> {
            if (listener != null) {
                listener.onHostGame(port);
            }
        });
    }

    private void showJoinDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Rejoindre une partie");
        dialog.setHeaderText("Entrez l'adresse IP et le port du serveur");

        ButtonType joinButtonType = new ButtonType("Rejoindre", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(joinButtonType, ButtonType.CANCEL);

        TextField hostField = new TextField("localhost");
        hostField.setPromptText("Adresse IP (ex: 192.168.1.10)");

        TextField portField = new TextField("5555");
        portField.setPromptText("Port (ex: 5555)");

        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Adresse IP:"), hostField,
            new Label("Port:"), portField
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == joinButtonType) {
                return new String[]{hostField.getText(), portField.getText()};
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (listener != null) {
                try {
                    int port = Integer.parseInt(result[1]);
                    listener.onJoinGame(result[0], port);
                } catch (NumberFormatException e) {
                    showError("Port invalide");
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package com.chesspong;

import com.chesspong.controller.GameController;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Sélection du nombre de types de pièces (qui sera numFiles)
        String[] options = {"2", "4", "6", "8"};
        String selected = (String) JOptionPane.showInputDialog(null, "Choisissez le nombre de types de pièces :",
                "Configuration de la partie", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        int numFiles = 8; // Valeur par défaut
        if (selected != null) {
            numFiles = Integer.parseInt(selected);
        }
        GameController controller = new GameController(primaryStage, numFiles);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
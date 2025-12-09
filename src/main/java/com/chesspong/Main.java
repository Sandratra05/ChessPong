package com.chesspong;

import com.chesspong.controller.GameController;
import javafx.application.Application;
import javafx.stage.Stage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        int numFiles = 8; // Example: choose number of files
        GameController controller = new GameController(primaryStage, numFiles);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
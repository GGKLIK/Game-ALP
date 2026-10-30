package com.projectgunner;

import com.projectgunner.game.GameLoop;
import com.projectgunner.game.GameManager;
import com.projectgunner.input.InputHandler;
import com.projectgunner.util.GameConfig;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(GameConfig.WIDTH, GameConfig.HEIGHT);
        InputHandler input = new InputHandler();
        GameManager game = new GameManager(canvas.getGraphicsContext2D(), input);

        Scene scene = new Scene(new StackPane(canvas),
                GameConfig.WIDTH, GameConfig.HEIGHT, Color.BLACK);
        scene.setCursor(Cursor.NONE);
        input.attachTo(scene);
        stage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                input.clear();
            }
        });

        stage.setTitle(GameConfig.TITLE);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        new GameLoop(game::update, game::render).start();
    }
}
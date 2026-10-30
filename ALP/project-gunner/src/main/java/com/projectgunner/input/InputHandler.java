package com.projectgunner.input;

import com.projectgunner.util.GameConfig;
import com.projectgunner.util.Vector2;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.EnumSet;
import java.util.Set;

public class InputHandler {
    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);
    private double mouseX = GameConfig.ARENA_WIDTH * 0.75;
    private double mouseY = GameConfig.ARENA_HEIGHT * 0.5;
    private boolean mouseDown = false;

    public void attachTo(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> pressedKeys.add(e.getCode()));
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> pressedKeys.remove(e.getCode()));
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, this::updateMousePosition);
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::updateMousePosition);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            updateMousePosition(e);
            if (e.getButton() == MouseButton.PRIMARY) {
                mouseDown = true;
            }
        });
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            updateMousePosition(e);
            if (e.getButton() == MouseButton.PRIMARY) {
                mouseDown = false;
            }
        });
    }

    private void updateMousePosition(MouseEvent e) {
        mouseX = e.getSceneX();
        mouseY = e.getSceneY();
    }

    public boolean isDown(KeyCode code) {
        return pressedKeys.contains(code);
    }

    public void clear() {
        pressedKeys.clear();
        mouseDown = false;
    }

    public Vector2 getMovementDirection() {
        double dx = (isDown(KeyCode.D) ? 1 : 0) - (isDown(KeyCode.A) ? 1 : 0);
        double dy = (isDown(KeyCode.S) ? 1 : 0) - (isDown(KeyCode.W) ? 1 : 0);
        return new Vector2(dx, dy).normalized();
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public boolean isMouseDown() {
        return mouseDown;
    }
}
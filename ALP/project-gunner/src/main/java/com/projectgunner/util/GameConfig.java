package com.projectgunner.util;

public final class GameConfig {

    public static final String TITLE = "PROJECT GUNNER";
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 640;

    public static final double ARENA_WIDTH = WIDTH;
    public static final double ARENA_HEIGHT = HEIGHT;
    public static final int GRID_SIZE = 64;

    public static final double MAX_DELTA_SECONDS = 0.05;

    public static final double PLAYER_RADIUS = 16;
    public static final double PLAYER_SPEED = 260;

    private GameConfig() {
    }
}
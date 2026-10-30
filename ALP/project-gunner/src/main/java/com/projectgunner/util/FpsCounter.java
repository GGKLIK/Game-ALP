package com.projectgunner.util;

public class FpsCounter {

    private double accumulator = 0;
    private int frames = 0;
    private double fps = 0;

    public void update(double dt) {
        accumulator += dt;
        frames++;
        if (accumulator >= 0.5) {
            fps = frames / accumulator;
            accumulator = 0;
            frames = 0;
        }
    }

    public double getFps() {
        return fps;
    }
}
package com.projectgunner.game;

import com.projectgunner.util.GameConfig;
import javafx.animation.AnimationTimer;

import java.util.function.DoubleConsumer;

public class GameLoop extends AnimationTimer {

    private final DoubleConsumer updateCallback;
    private final Runnable renderCallback;
    private long lastNanos = 0;

    public GameLoop(DoubleConsumer updateCallback, Runnable renderCallback) {
        this.updateCallback = updateCallback;
        this.renderCallback = renderCallback;
    }

    @Override
    public void start() {
        lastNanos = 0;
        super.start();
    }

    @Override
    public void handle(long nowNanos) {
        if (lastNanos == 0) {
            lastNanos = nowNanos;
            return;
        }
        double dt = (nowNanos - lastNanos) / 1_000_000_000.0;
        lastNanos = nowNanos;
        dt = Math.min(dt, GameConfig.MAX_DELTA_SECONDS);

        updateCallback.accept(dt);
        renderCallback.run();
    }
}
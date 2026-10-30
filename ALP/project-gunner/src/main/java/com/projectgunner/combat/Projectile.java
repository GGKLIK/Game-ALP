package com.projectgunner.combat;

import com.projectgunner.entity.Entity;
import com.projectgunner.util.GameConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Projectile extends Entity {

    private static final double RADIUS = 5;
    private static final double TRAIL_SECONDS = 0.025;

    private final double vx;
    private final double vy;
    private final double damage;
    private boolean active = true;

    public Projectile(double x, double y, double angle, double speed, double damage) {
        super(x, y, RADIUS);
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
        this.damage = damage;
    }

    @Override
    public void update(double dt) {
        x += vx * dt;
        y += vy * dt;

        if (isOutsideArena()) {
            active = false;
        }
    }

    private boolean isOutsideArena() {
        return x + radius < 0 || x - radius > GameConfig.ARENA_WIDTH
                || y + radius < 0 || y - radius > GameConfig.ARENA_HEIGHT;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setStroke(Color.rgb(255, 216, 102, 0.45));
        gc.setLineWidth(radius * 1.4);
        gc.strokeLine(x - vx * TRAIL_SECONDS, y - vy * TRAIL_SECONDS, x, y);

        gc.setFill(Color.web("#ffd866"));
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public double getDamage() {
        return damage;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
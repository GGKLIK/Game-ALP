package com.projectgunner.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Musuh 1: mengejar pemain. HP dan damage rendah, kecepatan relatif tinggi. */
public class ChaserEnemy extends Enemy {

    // HP dmg speed radius xp score
    private static final EnemyStats STATS = new EnemyStats(30, 10, 120, 14, 10, 100);

    /** Berhenti saat sedikit saling tindih supaya sentuhan pasti terdeteksi. */
    private static final double CONTACT_OVERLAP = 0.9;

    public ChaserEnemy(double x, double y, Player target) {
        super(x, y, STATS, target);
    }

    @Override
    protected void updateBehavior(double dt) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.hypot(dx, dy);
        double stopDistance = (radius + target.getRadius()) * CONTACT_OVERLAP;

        if (distance <= stopDistance) {
            return; // sudah menempel: tidak perlu bergerak lagi
        }

        // Langkah tidak boleh melewati titik berhenti
        double step = Math.min(stats.speed() * dt, distance - stopDistance);
        x += dx / distance * step;
        y += dy / distance * step;
    }

    @Override
    protected void drawBody(GraphicsContext gc, boolean flashing) {
        gc.setFill(flashing ? Color.WHITE : Color.web("#e5534b"));
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        gc.setStroke(flashing ? Color.WHITE : Color.web("#ffa198"));
        gc.setLineWidth(2);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        if (!flashing) {
            gc.setFill(Color.web("#7a1f1b"));
            gc.fillOval(x - radius * 0.4, y - radius * 0.4, radius * 0.8, radius * 0.8);
        }
    }
}
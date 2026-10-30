package com.projectgunner.entity;

import javafx.scene.canvas.GraphicsContext;

/**
 * Induk semua objek di arena (pemain, musuh, peluru, boss).
 * Setiap entity punya posisi, radius collision, dan tahu cara
 * memperbarui serta menggambar dirinya sendiri (polymorphism).
 */
public abstract class Entity {

    protected double x;
    protected double y;
    protected final double radius;

    protected Entity(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    /** @param dt selisih waktu sejak frame sebelumnya, dalam detik */
    public abstract void update(double dt);

    public abstract void render(GraphicsContext gc);

    /** Menggeser posisi (dipakai saat entity saling didorong menjauh). */
    public void translate(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }
}
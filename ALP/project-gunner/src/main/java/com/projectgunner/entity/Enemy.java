package com.projectgunner.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Induk semua musuh. Mengurus HP, damage yang diterima, kilatan saat
 * terkena tembakan, dan bar HP. Subclass hanya menentukan perilaku
 * (updateBehavior) dan bentuk badan (drawBody).
 */
public abstract class Enemy extends Entity {

    private static final double HIT_FLASH_DURATION = 0.08; // detik

    protected final EnemyStats stats;
    protected final Player target;

    private double hp;
    private double hitFlashTimer = 0;

    protected Enemy(double x, double y, EnemyStats stats, Player target) {
        super(x, y, stats.radius());
        this.stats = stats;
        this.target = target;
        this.hp = stats.maxHp();
    }

    @Override
    public final void update(double dt) {
        if (hitFlashTimer > 0) {
            hitFlashTimer -= dt;
        }
        updateBehavior(dt);
    }

    /** Perilaku khusus tiap jenis musuh (mengejar, menembak, dll). */
    protected abstract void updateBehavior(double dt);

    public void takeDamage(double amount) {
        hp = Math.max(0, hp - amount);
        hitFlashTimer = HIT_FLASH_DURATION;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    @Override
    public final void render(GraphicsContext gc) {
        // Bayangan
        gc.setFill(Color.rgb(0, 0, 0, 0.35));
        gc.fillOval(x - radius, y - radius + 5, radius * 2, radius * 2);

        drawBody(gc, hitFlashTimer > 0);
        drawHealthBar(gc);
    }

    /** @param flashing true sesaat setelah terkena tembakan (digambar putih) */
    protected abstract void drawBody(GraphicsContext gc, boolean flashing);

    private void drawHealthBar(GraphicsContext gc) {
        if (hp >= stats.maxHp()) {
            return; // musuh yang masih penuh tidak perlu bar
        }
        double barWidth = radius * 2;
        double barHeight = 4;
        double barX = x - radius;
        double barY = y - radius - 10;

        gc.setFill(Color.web("#30363d"));
        gc.fillRect(barX, barY, barWidth, barHeight);
        gc.setFill(Color.web("#3fb950"));
        gc.fillRect(barX, barY, barWidth * (hp / stats.maxHp()), barHeight);
    }

    public double getContactDamage() {
        return stats.damage();
    }

    public int getXpReward() {
        return stats.xpReward();
    }

    public int getScoreReward() {
        return stats.scoreReward();
    }

    public double getHp() {
        return hp;
    }
}
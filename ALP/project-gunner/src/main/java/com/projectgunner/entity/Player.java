package com.projectgunner.entity;

import com.projectgunner.combat.Projectile;
import com.projectgunner.combat.RapidFireWeapon;
import com.projectgunner.combat.Weapon;
import com.projectgunner.util.GameConfig;
import com.projectgunner.util.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class Player extends Entity {

    private static final double BARREL_LENGTH = 14; // panjang laras di luar badan
    private static final double BARREL_WIDTH = 8;
    private static final double MUZZLE_FLASH_DURATION = 0.05; // detik

    private static final double MAX_HP = 100;
    private static final double INVULNERABILITY_DURATION = 0.8; // detik

    private final double speed;
    private Vector2 moveDirection = Vector2.ZERO;

    private final Weapon weapon = new RapidFireWeapon();
    private double aimTargetX;
    private double aimTargetY;
    private double aimAngle = 0; // radian; 0 = ke kanan, positif = searah jarum jam di layar
    private double muzzleFlashTimer = 0;

    private double hp = MAX_HP;
    private double invulnerabilityTimer = 0;

    public Player(double startX, double startY) {
        super(startX, startY, GameConfig.PLAYER_RADIUS);
        this.speed = GameConfig.PLAYER_SPEED;
        this.aimTargetX = startX + 100;
        this.aimTargetY = startY;
    }

    /** Player tidak tahu soal keyboard; ia hanya menerima arah gerak. */
    public void setMoveDirection(Vector2 direction) {
        this.moveDirection = (direction == null) ? Vector2.ZERO : direction;
    }

    /** Titik yang dibidik (posisi mouse dalam koordinat arena). */
    public void setAimTarget(double targetX, double targetY) {
        this.aimTargetX = targetX;
        this.aimTargetY = targetY;
    }

    @Override
    public void update(double dt) {
        // jarak = arah (panjang 0 atau 1) x kecepatan (px/detik) x waktu (detik)
        x += moveDirection.x() * speed * dt;
        y += moveDirection.y() * speed * dt;
        clampToArena();

        // Dihitung ulang tiap frame: pemain bisa bergerak walau mouse diam
        aimAngle = Math.atan2(aimTargetY - y, aimTargetX - x);

        weapon.update(dt);
        if (muzzleFlashTimer > 0) {
            muzzleFlashTimer -= dt;
        }
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= dt;
        }
    }

    /**
     * Mencoba menembak ke arah bidikan.
     * 
     * @return true jika peluru benar-benar keluar (cooldown sudah habis)
     */
    public boolean tryShoot(List<Projectile> projectiles) {
        double muzzleDistance = radius + BARREL_LENGTH;
        double muzzleX = x + Math.cos(aimAngle) * muzzleDistance;
        double muzzleY = y + Math.sin(aimAngle) * muzzleDistance;

        boolean fired = weapon.tryFire(muzzleX, muzzleY, aimAngle, projectiles);
        if (fired) {
            muzzleFlashTimer = MUZZLE_FLASH_DURATION;
        }
        return fired;
    }

    /**
     * Mengurangi HP, kecuali pemain sedang kebal atau sudah mati.
     * 
     * @return true jika damage benar-benar diterapkan
     */
    public boolean takeDamage(double amount) {
        if (invulnerabilityTimer > 0 || !isAlive()) {
            return false;
        }
        hp = Math.max(0, hp - amount);
        invulnerabilityTimer = INVULNERABILITY_DURATION;
        return true;
    }

    /** Menahan pemain agar seluruh lingkarannya tetap di dalam arena. */
    private void clampToArena() {
        x = Math.max(radius, Math.min(x, GameConfig.ARENA_WIDTH - radius));
        y = Math.max(radius, Math.min(y, GameConfig.ARENA_HEIGHT - radius));
    }

    @Override
    public void render(GraphicsContext gc) {
        // Berkedip selama kebal: bergantian transparan dan normal
        boolean blinkOff = invulnerabilityTimer > 0 && ((int) (invulnerabilityTimer * 20)) % 2 == 0;
        if (blinkOff) {
            gc.setGlobalAlpha(0.35);
        }

        // Bayangan
        gc.setFill(Color.rgb(0, 0, 0, 0.35));
        gc.fillOval(x - radius, y - radius + 5, radius * 2, radius * 2);

        // Semua digambar dalam sistem koordinat yang dipindah ke pemain
        // dan diputar sesuai bidikan: laras tinggal digambar menghadap ke kanan.
        gc.save();
        gc.translate(x, y);
        gc.rotate(Math.toDegrees(aimAngle));

        // Laras
        gc.setFill(Color.web("#8b949e"));
        gc.fillRoundRect(0, -BARREL_WIDTH / 2, radius + BARREL_LENGTH, BARREL_WIDTH, 4, 4);

        // Badan
        gc.setFill(Color.web("#2f81f7"));
        gc.fillOval(-radius, -radius, radius * 2, radius * 2);
        gc.setStroke(Color.web("#a5d6ff"));
        gc.setLineWidth(2);
        gc.strokeOval(-radius, -radius, radius * 2, radius * 2);

        // Muzzle flash di ujung laras
        if (muzzleFlashTimer > 0) {
            gc.setFill(Color.web("#ffd866"));
            gc.fillOval(radius + BARREL_LENGTH - 4, -9, 18, 18);
        }

        gc.restore();
        gc.setGlobalAlpha(1.0);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public double getHp() {
        return hp;
    }

    public double getMaxHp() {
        return MAX_HP;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public double getAimAngle() {
        return aimAngle;
    }
}
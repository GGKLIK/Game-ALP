package com.projectgunner.game;

import com.projectgunner.combat.CollisionSystem;
import com.projectgunner.combat.CombatSystem;
import com.projectgunner.combat.Projectile;
import com.projectgunner.entity.ChaserEnemy;
import com.projectgunner.entity.Enemy;
import com.projectgunner.entity.Player;
import com.projectgunner.input.InputHandler;
import com.projectgunner.util.FpsCounter;
import com.projectgunner.util.GameConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    private static final double TEST_SPAWN_INTERVAL = 1.5;
    private static final int TEST_MAX_ENEMIES = 8;
    private static final double SPAWN_MARGIN = 30;

    private final GraphicsContext gc;
    private final InputHandler input;
    private final CombatSystem combatSystem = new CombatSystem();
    private final FpsCounter fpsCounter = new FpsCounter();
    private final Random random = new Random();

    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private Player player;

    private int score;
    private int xpEarned;
    private int enemiesDefeated;
    private double spawnTimer;

    private double shotWindowTime;
    private int shotsInWindow;
    private double measuredFireRate;

    public GameManager(GraphicsContext gc, InputHandler input) {
        this.gc = gc;
        this.input = input;
        restart();
    }

    private void restart() {
        player = new Player(GameConfig.ARENA_WIDTH / 2, GameConfig.ARENA_HEIGHT / 2);
        projectiles.clear();
        enemies.clear();
        score = 0;
        xpEarned = 0;
        enemiesDefeated = 0;
        spawnTimer = 0;
        shotWindowTime = 0;
        shotsInWindow = 0;
        measuredFireRate = 0;
    }

    public void update(double dt) {
        fpsCounter.update(dt);

        if (!player.isAlive()) {
            if (input.isDown(KeyCode.R)) {
                restart();
            }
            return;
        }

        player.setMoveDirection(input.getMovementDirection());
        player.setAimTarget(input.getMouseX(), input.getMouseY());
        player.update(dt);

        boolean fired = false;
        if (input.isMouseDown()) {
            fired = player.tryShoot(projectiles);
        }
        updateFireRateStats(dt, fired);

        updateTestSpawner(dt);
        for (Enemy enemy : enemies) {
            enemy.update(dt);
        }
        CollisionSystem.separate(enemies);

        for (Projectile projectile : projectiles) {
            projectile.update(dt);
        }

        combatSystem.resolve(player, enemies, projectiles);
        collectRewardsAndCleanup();
    }

    private void collectRewardsAndCleanup() {
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                score += enemy.getScoreReward();
                xpEarned += enemy.getXpReward();
                enemiesDefeated++;
            }
        }
        enemies.removeIf(Enemy::isDead);
        projectiles.removeIf(p -> !p.isActive());
    }

    private void updateTestSpawner(double dt) {
        spawnTimer += dt;
        if (spawnTimer >= TEST_SPAWN_INTERVAL && enemies.size() < TEST_MAX_ENEMIES) {
            spawnTimer = 0;
            spawnTestChaser();
        }
    }

    private void spawnTestChaser() {
        double w = GameConfig.ARENA_WIDTH;
        double h = GameConfig.ARENA_HEIGHT;
        double spawnX;
        double spawnY;

        int side = random.nextInt(4);
        if (side == 0) { // atas
            spawnX = random.nextDouble() * w;
            spawnY = -SPAWN_MARGIN;
        } else if (side == 1) { // bawah
            spawnX = random.nextDouble() * w;
            spawnY = h + SPAWN_MARGIN;
        } else if (side == 2) { // kiri
            spawnX = -SPAWN_MARGIN;
            spawnY = random.nextDouble() * h;
        } else { // kanan
            spawnX = w + SPAWN_MARGIN;
            spawnY = random.nextDouble() * h;
        }
        enemies.add(new ChaserEnemy(spawnX, spawnY, player));
    }

    private void updateFireRateStats(double dt, boolean fired) {
        shotWindowTime += dt;
        if (fired) {
            shotsInWindow++;
        }
        if (shotWindowTime >= 1.0) {
            measuredFireRate = shotsInWindow / shotWindowTime;
            shotsInWindow = 0;
            shotWindowTime = 0;
        }
    }

    public void render() {
        drawArena();
        for (Enemy enemy : enemies) {
            enemy.render(gc);
        }
        for (Projectile projectile : projectiles) {
            projectile.render(gc);
        }
        player.render(gc);

        drawCrosshair();
        drawDebugInfo();
        drawPlayerHealthBar();

        if (!player.isAlive()) {
            drawGameOver();
        }
    }

    private void drawArena() {
        double w = GameConfig.ARENA_WIDTH;
        double h = GameConfig.ARENA_HEIGHT;

        gc.setFill(Color.web("#0d1117"));
        gc.fillRect(0, 0, w, h);

        gc.setStroke(Color.web("#1c2430"));
        gc.setLineWidth(1);
        for (double gx = 0; gx <= w; gx += GameConfig.GRID_SIZE) {
            gc.strokeLine(gx, 0, gx, h);
        }
        for (double gy = 0; gy <= h; gy += GameConfig.GRID_SIZE) {
            gc.strokeLine(0, gy, w, gy);
        }

        // Garis batas arena
        gc.setStroke(Color.web("#ff6b35"));
        gc.setLineWidth(4);
        gc.strokeRect(2, 2, w - 4, h - 4);
    }

    private void drawCrosshair() {
        double mx = input.getMouseX();
        double my = input.getMouseY();

        gc.setStroke(Color.web("#ffffff"));
        gc.setLineWidth(2);
        gc.strokeOval(mx - 9, my - 9, 18, 18);
        gc.strokeLine(mx - 15, my, mx - 5, my);
        gc.strokeLine(mx + 5, my, mx + 15, my);
        gc.strokeLine(mx, my - 15, mx, my - 5);
        gc.strokeLine(mx, my + 5, mx, my + 15);
    }

    private void drawPlayerHealthBar() {
        double barWidth = 220;
        double barHeight = 16;
        double barX = (GameConfig.ARENA_WIDTH - barWidth) / 2;
        double barY = 14;
        double ratio = player.getHp() / player.getMaxHp();

        gc.setFill(Color.web("#30363d"));
        gc.fillRect(barX, barY, barWidth, barHeight);
        gc.setFill(ratio > 0.3 ? Color.web("#3fb950") : Color.web("#f85149"));
        gc.fillRect(barX, barY, barWidth * ratio, barHeight);
        gc.setStroke(Color.web("#8b949e"));
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barWidth, barHeight);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setFill(Color.WHITE);
        gc.fillText(String.format("HP  %.0f / %.0f", player.getHp(), player.getMaxHp()),
                GameConfig.ARENA_WIDTH / 2, barY + 12);
    }

    private void drawDebugInfo() {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Consolas", 14));
        gc.setFill(Color.web("#58a6ff"));
        gc.fillText(String.format("FPS       : %.0f", fpsCounter.getFps()), 16, 28);
        gc.fillText(String.format("Senjata   : %s (%.0f tembakan/detik)",
                player.getWeapon().getName(), player.getWeapon().getFireRate()), 16, 48);
        gc.fillText(String.format("Tembakan  : %.1f /detik (terukur)", measuredFireRate), 16, 68);
        gc.fillText(String.format("Peluru    : %d aktif", projectiles.size()), 16, 88);
        gc.fillText(String.format("Musuh     : %d aktif", enemies.size()), 16, 108);
        gc.fillText(String.format("Dikalahkan: %d", enemiesDefeated), 16, 128);
        gc.fillText(String.format("Skor      : %d", score), 16, 148);
        gc.fillText(String.format("XP        : %d", xpEarned), 16, 168);

        gc.setFill(Color.web("#8b949e"));
        gc.fillText("WASD = bergerak   |   Mouse = bidik   |   Klik kiri (tahan) = tembak",
                16, GameConfig.ARENA_HEIGHT - 20);
    }

    private void drawGameOver() {
        double cx = GameConfig.ARENA_WIDTH / 2;
        double cy = GameConfig.ARENA_HEIGHT / 2;

        gc.setFill(Color.rgb(0, 0, 0, 0.65));
        gc.fillRect(0, 0, GameConfig.ARENA_WIDTH, GameConfig.ARENA_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.web("#f85149"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        gc.fillText("GAME OVER", cx, cy - 20);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 20));
        gc.fillText(String.format("Skor %d   |   Musuh dikalahkan %d", score, enemiesDefeated),
                cx, cy + 20);
        gc.fillText("Tekan R untuk mengulang", cx, cy + 55);
    }
}
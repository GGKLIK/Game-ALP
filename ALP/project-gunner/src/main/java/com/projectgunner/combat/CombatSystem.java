package com.projectgunner.combat;

import com.projectgunner.entity.Enemy;
import com.projectgunner.entity.Player;

import java.util.List;

public class CombatSystem {

    public void resolve(Player player, List<Enemy> enemies, List<Projectile> projectiles) {
        resolveProjectileHits(enemies, projectiles);
        resolveContactDamage(player, enemies);
    }

    private void resolveProjectileHits(List<Enemy> enemies, List<Projectile> projectiles) {
        for (Projectile projectile : projectiles) {
            if (!projectile.isActive()) {
                continue;
            }
            for (Enemy enemy : enemies) {
                if (enemy.isDead()) {
                    continue;
                }
                if (CollisionSystem.overlaps(projectile, enemy)) {
                    enemy.takeDamage(projectile.getDamage());
                    projectile.deactivate();
                    break;
                }
            }
        }
    }

    private void resolveContactDamage(Player player, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                continue;
            }
            if (CollisionSystem.overlaps(player, enemy)) {
                player.takeDamage(enemy.getContactDamage());
            }
        }
    }
}
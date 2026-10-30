package com.projectgunner.combat;

import com.projectgunner.util.GameConfig;

import java.util.List;

public abstract class Weapon {

    private final String name;
    private final double damage;
    private final double fireRate;
    private final double projectileSpeed;
    private final double interval;

    private double cooldown = 0;

    protected Weapon(String name, double damage, double fireRate, double projectileSpeed) {
        this.name = name;
        this.damage = damage;
        this.fireRate = fireRate;
        this.projectileSpeed = projectileSpeed;
        this.interval = 1.0 / fireRate;
    }

    public void update(double dt) {
        cooldown = Math.max(cooldown - dt, -GameConfig.MAX_DELTA_SECONDS);
    }

    public boolean tryFire(double originX, double originY, double angle, List<Projectile> output) {
        if (cooldown > 0) {
            return false;
        }
        createProjectiles(originX, originY, angle, output);
        cooldown += interval;
        return true;
    }

    protected abstract void createProjectiles(double originX, double originY,
            double angle, List<Projectile> output);

    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public double getFireRate() {
        return fireRate;
    }

    public double getProjectileSpeed() {
        return projectileSpeed;
    }
}
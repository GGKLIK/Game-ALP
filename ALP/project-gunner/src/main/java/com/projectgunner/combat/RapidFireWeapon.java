package com.projectgunner.combat;

import java.util.List;

public class RapidFireWeapon extends Weapon {

    public RapidFireWeapon() {
        super("Rapid Fire", 10, 8, 700);
    }

    @Override
    protected void createProjectiles(double originX, double originY,
            double angle, List<Projectile> output) {
        output.add(new Projectile(originX, originY, angle, getProjectileSpeed(), getDamage()));
    }
}
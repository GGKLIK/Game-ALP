package com.projectgunner.combat;

import com.projectgunner.entity.Entity;

import java.util.List;

/** Fungsi-fungsi geometri collision berbentuk lingkaran. */
public final class CollisionSystem {

    private CollisionSystem() {
        // Class utilitas: tidak boleh dibuat objeknya.
    }

    /**
     * Dua lingkaran bertabrakan jika jarak pusatnya < jumlah radius.
     * Kedua sisi dikuadratkan agar tidak perlu menghitung akar (sqrt).
     */
    public static boolean overlaps(Entity a, Entity b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double radiusSum = a.getRadius() + b.getRadius();
        return dx * dx + dy * dy < radiusSum * radiusSum;
    }

    /**
     * Mendorong entity yang saling tindih ke arah berlawanan agar tidak menumpuk.
     */
    public static void separate(List<? extends Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);

                double dx = b.getX() - a.getX();
                double dy = b.getY() - a.getY();
                double minDistance = a.getRadius() + b.getRadius();
                double distanceSquared = dx * dx + dy * dy;
                if (distanceSquared >= minDistance * minDistance) {
                    continue;
                }

                double distance = Math.sqrt(distanceSquared);
                double nx = 1; // arah dorongan; default jika posisi persis sama
                double ny = 0;
                if (distance > 0) {
                    nx = dx / distance;
                    ny = dy / distance;
                }

                double push = (minDistance - distance) / 2;
                a.translate(-nx * push, -ny * push);
                b.translate(nx * push, ny * push);
            }
        }
    }
}
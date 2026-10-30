package com.projectgunner.entity;

/**
 * Data angka sebuah musuh. Dipisah dari perilaku supaya musuh baru
 * (dan penyesuaian difficulty) cukup mengganti angka di sini.
 */
public record EnemyStats(
        double maxHp,
        double damage, // damage saat menyentuh pemain
        double speed, // piksel per detik
        double radius, // radius collision
        int xpReward,
        int scoreReward) {
}
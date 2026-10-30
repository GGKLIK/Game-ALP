package com.projectgunner.util;

public record Vector2(double x, double y) {

    public static final Vector2 ZERO = new Vector2(0, 0);

    public double length() {
        return Math.hypot(x, y);
    }

    public Vector2 normalized() {
        double len = length();
        if (len == 0) {
            return ZERO;
        }
        return new Vector2(x / len, y / len);
    }
}
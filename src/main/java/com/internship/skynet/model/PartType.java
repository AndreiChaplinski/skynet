package com.internship.skynet.model;

public enum PartType {
    HEAD,
    TORSO,
    HAND,
    FOOT;

    public static PartType getRandomPartType() {
        return values()[(int) (Math.random() * values().length)];
    }
}

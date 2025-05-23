package com.aquamarijn.petimprovements.util;

public enum BehaviorType {
    SIT,
    FOLLOW,
    WANDER;

    public BehaviorType next() {
        return values()[(ordinal() + 1) % values().length];
    }
}

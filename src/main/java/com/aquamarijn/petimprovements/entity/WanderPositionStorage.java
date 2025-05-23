package com.aquamarijn.petimprovements.entity;

import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;

import java.util.WeakHashMap;

public class WanderPositionStorage {
    private static final WeakHashMap<Object, BlockPos> wanderPositions = new WeakHashMap<>();
    private static final WeakHashMap<TameableEntity, WanderAroundPointGoal> wanderGoals = new WeakHashMap<>();

    //Goals storage
    public static void setWanderGoal(TameableEntity entity, WanderAroundPointGoal goal) {
        wanderGoals.put(entity, goal);
    }
    public static WanderAroundPointGoal getWanderGoal(TameableEntity entity) {
        return wanderGoals.get(entity);
    }

    public static void removeWanderGoal(TameableEntity entity) {
        wanderGoals.remove(entity);
    }


    //Position storage
    public static void setWanderPositions(Object entity, BlockPos pos) {
        wanderPositions.put(entity, pos);
    }

    public static BlockPos getWanderPosition(Object entity) {
        return wanderPositions.get(entity);
    }
}

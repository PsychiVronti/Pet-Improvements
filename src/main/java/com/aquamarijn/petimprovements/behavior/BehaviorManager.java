package com.aquamarijn.petimprovements.behavior;

import com.aquamarijn.petimprovements.config.ServerConfig;
import com.aquamarijn.petimprovements.entity.WanderAroundPointGoal;
import com.aquamarijn.petimprovements.entity.WanderPositionStorage;
import com.aquamarijn.petimprovements.mixin.MobEntityAccessor;
import com.aquamarijn.petimprovements.util.BehaviorType;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BehaviorManager {

    public static void applyBehavior(TameableEntity entity, BehaviorType type) {
        GoalSelector goals = ((MobEntityAccessor) entity).getGoalSelector();

        switch (type) {
            case SIT -> {
                entity.setInSittingPose(true);
                entity.setSitting(true);
                removeWanderGoal(entity, goals);
                removeFollowGoal(goals);
            }
            case FOLLOW -> {
                entity.setInSittingPose(false);
                entity.setSitting(false);
                WanderPositionStorage.setWanderPositions(entity, null);
                removeWanderGoal(entity, goals);
                removeFollowGoal(goals);
                goals.add(4, new FollowOwnerGoal(entity, 1.0, 5.0f, 2.0f));
                playSound(entity, type);
            }
            case WANDER -> {
                // Config to enable/disable pet wandering
                if (!ServerConfig.HANDLER.instance().enablePetWander) return;

                entity.setInSittingPose(false);
                entity.setSitting(false);
                BlockPos center = entity.getBlockPos();
                WanderPositionStorage.setWanderPositions(entity, center);

                removeFollowGoal(goals);
                removeWanderGoal(entity, goals);

                WanderAroundPointGoal wanderGoal = new WanderAroundPointGoal(entity, 1.0);
                WanderPositionStorage.setWanderGoal(entity, wanderGoal);
                goals.add(5, wanderGoal);

                playSound(entity, type);
            }
        }
    }

    private static void removeFollowGoal(GoalSelector goals) {
        goals.getGoals().removeIf(g -> g.getGoal() instanceof FollowOwnerGoal);
    }

    private static void removeWanderGoal(TameableEntity entity, GoalSelector goals) {
        WanderAroundPointGoal oldGoal = WanderPositionStorage.getWanderGoal(entity);
        if (oldGoal != null) {
            goals.getGoals().removeIf(g -> g.getGoal() == oldGoal);
            WanderPositionStorage.removeWanderGoal(entity);
        }
    }

    //Play a sound when set to Wander or Follow
    private static void playSound(TameableEntity entity, BehaviorType type) {
        if (!(entity.getWorld() instanceof World world)) return;

        if (type == BehaviorType.WANDER || type == BehaviorType.FOLLOW) {
            if (entity instanceof CatEntity) {
                world.playSound(
                        null,
                        entity.getBlockPos(),
                        SoundEvents.ENTITY_CAT_AMBIENT,
                        entity.getSoundCategory(),
                        1.0f,
                        1.0f
                );
            } else if (entity instanceof WolfEntity) {
                world.playSound(
                        null,
                        entity.getBlockPos(),
                        SoundEvents.ENTITY_WOLF_AMBIENT,
                        entity.getSoundCategory(),
                        1.0f,
                        1.0f
                );
            }
        }
    }
}
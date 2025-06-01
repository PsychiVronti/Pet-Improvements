package com.aquamarijn.petimprovements.entity;

import com.aquamarijn.petimprovements.config.ServerConfig;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class WanderAroundPointGoal extends Goal {
    private final PathAwareEntity entity;
    private final double speed;
    private final World world;
    private int cooldown;

    public WanderAroundPointGoal(PathAwareEntity entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.world = entity.getWorld();
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (entity instanceof CatEntity cat) {
            if (cat.isInSittingPose() || cat.isInSleepingPose()) {
                if (cooldown <= 0) {
                    cooldown = 20;
                }
                return false;
            }
        }
        return cooldown-- <= 0 && WanderPositionStorage.getWanderPosition(entity) != null;
    }

    @Override
    public void start() {
        BlockPos center = WanderPositionStorage.getWanderPosition(entity);
        int radius = ServerConfig.HANDLER.instance().wanderRadius;
        if (center != null) {
            int dx = center.getX() + (world.random.nextInt() * radius * 2) - radius;
            int dz = center.getZ() + (world.random.nextInt() * radius * 2) - radius;
            BlockPos target = new BlockPos(dx, center.getY(), dz);
            entity.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), speed);
            cooldown = 40 + world.random.nextInt(40);
        }
    }

    @Override
    public boolean shouldContinue() {
        return entity.getNavigation().isFollowingPath();
    }
}

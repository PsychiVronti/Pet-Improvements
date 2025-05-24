package com.aquamarijn.petimprovements.mixin;

import com.aquamarijn.petimprovements.entity.PetRespawnManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public abstract class TameableEntityDeathMixin {
    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("PetImprovements");

    @Inject(
            method = "onDeath",
            at = @At("HEAD"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        TameableEntity entity = (TameableEntity)(Object) this;
        if (!entity.getWorld().isClient() && entity.getWorld() instanceof ServerWorld serverWorld) {
            LOGGER.info("onDeath called for pet {}", entity.getUuid());
            PetRespawnManager.onPetDeath(entity, serverWorld);
        }
    }
}

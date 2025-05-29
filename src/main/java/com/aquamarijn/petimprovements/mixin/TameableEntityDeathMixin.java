package com.aquamarijn.petimprovements.mixin;

import com.aquamarijn.petimprovements.entity.PetRespawnManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.MinecraftServer;
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

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onPetDeath(net.minecraft.entity.damage.DamageSource source, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!self.getWorld().isClient && self instanceof TameableEntity tameable) {
            MinecraftServer server = self.getWorld().getServer();
            LOGGER.info("onPetDeath called for pet {}", self.getUuid());
            if (server != null) {
                PetRespawnManager.respawnPet(tameable, server);
            }
        }
    }
}

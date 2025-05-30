package com.aquamarijn.petimprovements.mixin;

import com.aquamarijn.petimprovements.entity.PetRespawnManager;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public abstract class TameableEntityDeathMixin {
    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeathCallback(CallbackInfo ci) {
        TameableEntity pet = (TameableEntity) (Object)this;
        World world = pet.getWorld();

        if (!world.isClient && pet.isTamed() && world instanceof ServerWorld serverWorld) {
            BlockPos lastBedPos = PetRespawnManager.getRespawnData(pet) != null
                    ? PetRespawnManager.getRespawnData(pet).pos()
                    : null;

            if (lastBedPos != null) {
                PetRespawnManager.storeRespawnData(pet, serverWorld, pet.getBlockPos(), true);
            }
        }
    }
}

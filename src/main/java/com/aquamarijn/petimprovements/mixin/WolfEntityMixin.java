package com.aquamarijn.petimprovements.mixin;

import com.aquamarijn.petimprovements.behavior.BehaviorManager;
import com.aquamarijn.petimprovements.config.ServerConfig;
import com.aquamarijn.petimprovements.util.BehaviorType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin {
    @Unique
    private BehaviorType behaviorType = BehaviorType.FOLLOW;

    @Inject(
            method = "interactMob",
            at = @At("HEAD"),
            cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        WolfEntity entity = (WolfEntity)(Object)this;
        ItemStack stack = player.getStackInHand(hand);

        if (entity.isTamed() && entity.getOwner() == player && stack.isEmpty()) {
            BehaviorType newType = behaviorType.next();

            if (newType == BehaviorType.WANDER && !ServerConfig.HANDLER.instance().enablePetWander) {
                newType = newType.next(); // Skip to FOLLOW
            } else {
                player.sendMessage(Text.literal("Behavior set to: " + newType.name()), true);
            }

            behaviorType = newType;
            BehaviorManager.applyBehavior(entity, newType);

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    // Make Wolf immune to player attacks when in combat against non-players
    @Inject(
            method = "damage",
            at = @At("HEAD"),
            cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        WolfEntity entity = (WolfEntity)(Object)this;

        if (ServerConfig.HANDLER.instance().wolfImmuneToPlayer
                && entity.isTamed()
                && entity.getTarget() != null
                && source.getAttacker() instanceof PlayerEntity
                && !(entity.getTarget() instanceof  PlayerEntity)) {
            // Exit damage method if the above conditional is true
            // Returns true to still apply attacks but do not modify health of tamed wolf (sweeping edge compat)
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "writeCustomDataToNbt",
            at = @At("HEAD"))
    public void saveBehavior(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("CustomBehavior", behaviorType.ordinal());
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void loadBehavior(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("CustomBehavior")) {
            behaviorType = BehaviorType.values()[nbt.getInt("CustomBehavior")];
        }
    }
}

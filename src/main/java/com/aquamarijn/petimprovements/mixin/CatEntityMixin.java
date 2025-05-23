package com.aquamarijn.petimprovements.mixin;

import com.aquamarijn.petimprovements.behavior.BehaviorManager;
import com.aquamarijn.petimprovements.util.BehaviorType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin {
    private BehaviorType behaviorType = BehaviorType.FOLLOW;


    @Inject(
            method = "interactMob",
            at = @At("HEAD"),
            cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        CatEntity entity = (CatEntity)(Object)this;
        ItemStack stack = player.getStackInHand(hand);

        if (entity.isTamed() && entity.getOwner() == player && stack.isEmpty()) {
            BehaviorType newType = behaviorType.next();
            behaviorType = newType;

            BehaviorManager.applyBehavior(entity, newType);

            //Overlay feedback
            if (!player.getWorld().isClient()) {
                player.sendMessage(Text.literal("Behavior set to: " + newType.name()), true);
            }

            cir.setReturnValue(ActionResult.SUCCESS);
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

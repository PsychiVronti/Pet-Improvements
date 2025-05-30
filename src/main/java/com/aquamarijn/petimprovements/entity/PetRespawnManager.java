
package com.aquamarijn.petimprovements.entity;

import com.aquamarijn.petimprovements.block.ModBlocks;
import com.aquamarijn.petimprovements.util.PetRespawnState;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PetRespawnManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("PetImprovements");


    public static boolean bindPetIfNew(TameableEntity pet, BlockPos pos) {
        if (pet.getWorld().isClient) return false;

        ServerWorld world = (ServerWorld) pet.getWorld();
        PetRespawnState state = world.getPersistentStateManager()
                .getOrCreate(PetRespawnState.TYPE, PetRespawnState.NAME);
        UUID uuid = pet.getUuid();
        PetRespawnState.PetRespawnLocation currentLocation = state.getRespawnLocation(uuid);


        //Check if bed is unbroken
        if (currentLocation != null
                && currentLocation.pos().equals(pos)
                && currentLocation.dimension().equals(world.getRegistryKey())) {
            LOGGER.info("bed is unbroken, bindPetIfNew returns false");
            return false;
        }

        state.setRespawnLocation(uuid, pos, world.getRegistryKey());
        LOGGER.info("bindPetIfNew returns true");
        state.markDirty();

        return true;
    }

    public static boolean respawnPet(TameableEntity originalPet, MinecraftServer server) {
        //Defining original pet data
        UUID uuid = originalPet.getUuid();
        ServerWorld overworld = server.getOverworld();
        PetRespawnState state = overworld.getPersistentStateManager()
                .getOrCreate(PetRespawnState.TYPE, PetRespawnState.NAME);

        PetRespawnState.PetRespawnLocation currentData = state.getRespawnLocation(uuid);
        if (currentData == null) return false;

        ServerWorld targetWorld = server.getWorld(currentData.dimension());
        if (targetWorld == null) return false;

        //Checks if pet bed is unbroken
        Block bedBlock = targetWorld.getBlockState(currentData.pos()).getBlock();
        if (!ModBlocks.PET_BED_BLOCKS.contains(bedBlock)) {
            return false;
        }

        //Respawn pet by copying dead pet
        try {
            Entity entity = originalPet.getType().create(targetWorld);
            if (entity instanceof TameableEntity newPet) {
                newPet.setPosition(
                        currentData.pos().getX() + 0.5,
                        currentData.pos().getY() + 0.5,
                        currentData.pos().getZ() + 0.5);
                newPet.setTamed(true, false);
                newPet.setOwnerUuid(originalPet.getOwnerUuid());
                newPet.setCustomName(originalPet.getCustomName());
                newPet.setHealth(originalPet.getMaxHealth());
                if (entity instanceof CatEntity newCat && originalPet instanceof CatEntity originalCat) {
                    newCat.setVariant(originalCat.getVariant());
                }
                if (entity instanceof WolfEntity newWolf && originalPet instanceof WolfEntity originalWolf) {
                    newWolf.setVariant(originalWolf.getVariant());
                    //Copying over collar color using NBT data
                    NbtCompound originalNbt = new NbtCompound();
                    originalWolf.writeNbt(originalNbt);
                    if (originalNbt.contains("CollarColor", NbtElement.BYTE_TYPE)) {
                        //Get collar color
                        byte collarColor = originalNbt.getByte("CollarColor");
                        //Apply collar color
                        NbtCompound newNbt = new NbtCompound();
                        newWolf.writeNbt(newNbt);
                        newNbt.putByte("CollarColor", collarColor);
                        newWolf.readNbt(newNbt);
                    }
                }
                if (entity instanceof ParrotEntity newParrot && originalPet instanceof ParrotEntity originalParrot) {
                    newParrot.setVariant(originalParrot.getVariant());
                }

                boolean success = targetWorld.spawnEntity(newPet);
                if (success) {
                    targetWorld.spawnParticles(
                            ParticleTypes.HEART,
                            currentData.pos().getX() + 0.5,
                            currentData.pos().getY() + 0.5,
                            currentData.pos().getZ() + 0.5,
                            8, 0.5, 0.5, 0.5, 0.1
                    );
                    if (newPet.getOwner() instanceof net.minecraft.entity.player.PlayerEntity player) {
                        player.sendMessage(Text.translatable("text.petimprovements.pet_respawned", newPet.getName()), false);
                    }
                }
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

}

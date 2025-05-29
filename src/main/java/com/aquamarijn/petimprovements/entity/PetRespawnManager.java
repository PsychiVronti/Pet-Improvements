
package com.aquamarijn.petimprovements.entity;

import com.aquamarijn.petimprovements.block.ModBlocks;
import com.aquamarijn.petimprovements.util.PetRespawnState;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;
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
        PetRespawnState.PetRespawnLocation current = state.getRespawnPoint(uuid);


        //Check if bed is unbroken
        if (current != null
                && current.pos.equals(pos)
                && current.dimension.equals(world.getRegistryKey())) {
            LOGGER.info("bed is unbroken, bindPetIfNew returns false");
            return false;
        }

        state.setRespawnPoints(uuid, pos, world.getRegistryKey());
        LOGGER.info("bindPetIfNew returns true");
        state.markDirty();

        return true;
    }

    public static boolean respawnPet(TameableEntity originalPet, MinecraftServer server) {
        UUID uuid = originalPet.getUuid();

        ServerWorld overworld = server.getOverworld();
        PetRespawnState state = overworld.getPersistentStateManager()
                .getOrCreate(PetRespawnState.TYPE, PetRespawnState.NAME);

        PetRespawnState.PetRespawnLocation location = state.getRespawnPoint(uuid);
        if (location == null) return false;

        ServerWorld targetWorld = server.getWorld(location.dimension);
        if (targetWorld == null) return false;

        //Checks if pet bed is unbroken
        Block bedBlock = targetWorld.getBlockState(location.pos).getBlock();
        if (!ModBlocks.PET_BED_BLOCKS.contains(bedBlock)) {
            return false;
        }

        try {
            Entity entity = originalPet.getType().create(targetWorld);
            if (entity instanceof TameableEntity newPet) {
                newPet.setPosition(
                        location.pos.getX() + 0.5,
                        location.pos.getY() + 0.5,
                        location.pos.getZ() + 0.5);
                newPet.setTamed(true, false);
                newPet.setOwnerUuid(originalPet.getOwnerUuid());
                newPet.setCustomName(originalPet.getCustomName());
                newPet.setHealth(originalPet.getMaxHealth());
                if (entity instanceof CatEntity newCat && originalPet instanceof CatEntity originalCat) {
                    newCat.setVariant(originalCat.getVariant());
                }

                boolean success = targetWorld.spawnEntity(newPet);
                if (success) {
                    targetWorld.spawnParticles(
                            ParticleTypes.HEART,
                            location.pos.getX() + 0.5,
                            location.pos.getY() + 0.5,
                            location.pos.getZ() + 0.5,
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

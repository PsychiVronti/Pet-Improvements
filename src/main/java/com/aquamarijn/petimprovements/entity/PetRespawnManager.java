
package com.aquamarijn.petimprovements.entity;

import com.aquamarijn.petimprovements.block.ModBlocks;
import com.aquamarijn.petimprovements.util.PetRespawnState;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PetRespawnManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("PetImprovements");

    //Pet binding method
    public static boolean bindPetToBed(TameableEntity pet, BlockPos pos) {
        if (pet.getWorld().isClient) return false;

        ServerWorld world = (ServerWorld) pet.getWorld();
        PetRespawnState state = world.getPersistentStateManager()
                .getOrCreate(PetRespawnState.TYPE, PetRespawnState.NAME);
        UUID petId = pet.getUuid();
        PetRespawnState.PetRespawnLocation currentLocation = state.getRespawnLocation(petId);


        //Checks to see if data needs to be rewritten
        if (currentLocation != null
                && currentLocation.pos().equals(pos)
                && currentLocation.dimension().equals(world.getRegistryKey())) {
            LOGGER.info("bed is unbroken, bindPetIfNew returns false");
            return false;
        }

        state.setRespawnLocation(petId, pos, world.getRegistryKey());
        LOGGER.info("bindPetIfNew returns true");

        return true;
    }

    //Store respawn data method
    public static void storeRespawnData(TameableEntity pet, ServerWorld world, BlockPos pos, boolean isDead) {
        if (pet.getWorld().isClient) return;

        PetRespawnState state = world.getPersistentStateManager()
                .getOrCreate(PetRespawnState.TYPE, PetRespawnState.NAME);
        UUID petId = pet.getUuid();
        PetRespawnState.PetRespawnLocation current = state.getRespawnLocation(petId);

        if (current != null) {
            NbtCompound nbt = new NbtCompound();
            if (pet.saveSelfNbt(nbt)) {
                state.setPetData(petId, nbt);
            }
        }
    }

    //Get respawn data method
    public static PetRespawnState.PetRespawnLocation getRespawnData(Entity entity) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            PetRespawnState state = serverWorld.getPersistentStateManager()
                    .getOrCreate(PetRespawnState.TYPE, PetRespawnState.NAME);
            return state.getRespawnLocation(entity.getUuid());
        }
        return null;
    }

    //Respawn method using NBT
    public static boolean respawnPet(ServerWorld world, UUID uuid, NbtCompound petData, BlockPos pos) {
        Entity entity = EntityType.loadEntityWithPassengers(petData, world, e -> e);
        if (entity instanceof TameableEntity newPet) {
            newPet.setPosition(Vec3d.ofCenter(pos).add(0, 0.5, 0));
            newPet.setHealth(newPet.getMaxHealth());
            boolean success = world.spawnEntity(newPet);

            if (success) {
                world.spawnParticles(ParticleTypes.HEART,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        8, 0.5, 0.5, 0.5, 0.1
                        );
                if (newPet.getOwner() instanceof PlayerEntity player) {
                    player.sendMessage(Text.translatable("text.petimprovements.pet_respawned", newPet.getName()),
                            false);
                }
            }
            return success;
        }
        return false;
    }

    //Respawn all pets method
    public static void respawnAllPendingPets(ServerWorld world) {
        PetRespawnState state = world.getPersistentStateManager()
                .getOrCreate(PetRespawnState.TYPE, PetRespawnState.NAME);
        List<UUID> toRespawn = new ArrayList<>();

        for (Map.Entry<UUID, PetRespawnState.PetRespawnLocation> entry : state.getAllRespawnLocations().entrySet()) {
            UUID uuid = entry.getKey();
            PetRespawnState.PetRespawnLocation location = entry.getValue();

            if (location.dimension().equals(world.getRegistryKey())) {
                NbtCompound petData = state.getPetData(uuid);
                if (petData != null) {
                 boolean success = respawnPet(world, uuid, petData, location.pos());
                 if (success) {
                     toRespawn.add(uuid);
                 }
                }
            }
        }
        for (UUID uuid : toRespawn) {
            state.remove(uuid);
        }
    }

}

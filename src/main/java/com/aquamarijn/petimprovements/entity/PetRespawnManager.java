
package com.aquamarijn.petimprovements.entity;

import com.aquamarijn.petimprovements.util.PetRespawnState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PetRespawnManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("PetImprovements");

    public static boolean bindPetIfNew(TameableEntity pet, BlockPos bedPos) {
        ServerWorld world = (ServerWorld) pet.getWorld();
        PetRespawnState state = PetRespawnState.get(world);
        UUID uuid = pet.getUuid();
        BlockPos previous = state.getBed(uuid);
        LOGGER.info("bindPetIfNew initialized");
        if (previous != null && previous.equals(bedPos)) {
            return false;
        }
        state.bindBed(uuid, bedPos);
        LOGGER.info("bindPetIfNew called for {}", pet.getUuid());
        return true;
    }

    public static void onPetDeath(TameableEntity pet, ServerWorld world) {
        PetRespawnState state = PetRespawnState.get(world);
        UUID uuid = pet.getUuid();
        NbtCompound data = pet.writeNbt(new NbtCompound());
        data.putString("id", EntityType.getId(pet.getType()).toString());
        state.addPet(uuid, data);
        state.markDirty();
        LOGGER.info("onPetDeath: Pet {} queued for respawn", uuid);
    }

    public static void tick(ServerWorld world) {
        PetRespawnState state = PetRespawnState.get(world);
        if (world.getTimeOfDay() % 24000 != 0) return;

        Iterator<Map.Entry<UUID, NbtCompound>> iterator = state.getPendingRespawns().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, NbtCompound> entry = iterator.next();
            UUID uuid = entry.getKey();
            NbtCompound nbt = entry.getValue();

            BlockPos bedPos = state.getBed(uuid);
            if (bedPos == null) continue;

            String id = nbt.getString("id");
            EntityType<?> type = Registries.ENTITY_TYPE.get(Identifier.of(id));
            if (type == null) continue;

            Entity entity = type.create(world);
            if (!(entity instanceof TameableEntity pet)) continue;

            nbt.remove("UUID");
            nbt.remove("Pos");
            nbt.remove("Motion");
            nbt.remove("Rotation");

            pet.readNbt(nbt);
            pet.setHealth(pet.getMaxHealth());
            pet.setInvulnerable(false);
            pet.refreshPositionAndAngles(
                    bedPos.getX() + 0.5,
                    bedPos.getY() + 1.0,
                    bedPos.getZ() + 0.5,
                    0.0f,
                    0.0f
            );

            boolean success = world.spawnEntity(pet);
            if (success) {
                world.spawnParticles(
                        ParticleTypes.HEART,
                        bedPos.getX() + 0.5,
                        bedPos.getY() + 1.0,
                        bedPos.getZ() + 0.5,
                        5, 0.3, 0.3, 0.3, 0.1
                );
                if (pet.getOwner() instanceof net.minecraft.entity.player.PlayerEntity player) {
                    player.sendMessage(
                            Text.translatable("text.petimprovements.pet_respawned", pet.getName()),
                            false);
                }
                iterator.remove();
                state.markDirty();
            }
        }
    }
}

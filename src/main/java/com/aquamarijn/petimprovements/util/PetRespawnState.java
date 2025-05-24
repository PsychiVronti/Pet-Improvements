package com.aquamarijn.petimprovements.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetRespawnState extends PersistentState {
    private final Map<UUID, NbtCompound> petsToRespawn = new HashMap<>();
    private final Map<UUID, BlockPos> bedBindings = new HashMap<>();
    public static final Type<PetRespawnState> TYPE =
            new Type<>(
                    PetRespawnState::new,
                    (nbt, registryLookup) -> PetRespawnState.fromNbt(nbt),
                    null
            );

    private static final String KEY = "petimprovements_respawn_data";

    public static PetRespawnState get(ServerWorld world) {
        return world.getServer().getOverworld()
                .getPersistentStateManager()
                .getOrCreate(TYPE, KEY);
    }

    public void addPet(UUID uuid, NbtCompound nbt) {
        petsToRespawn.put(uuid, nbt);
        markDirty();
    }

    public void bindBed(UUID uuid, BlockPos pos) {
        bedBindings.put(uuid, pos);
        markDirty();
    }

    public BlockPos getBed(UUID uuid) {
        return bedBindings.get(uuid);
    }

    public Map<UUID, NbtCompound> getPendingRespawns() {
        return petsToRespawn;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList petsList = new NbtList();
        for (var entry : petsToRespawn.entrySet()) {
            NbtCompound petData = new NbtCompound();
            petData.putUuid("Uuid", entry.getKey());
            petData.put("Data", entry.getValue());
            petsList.add(petData);
        }
        nbt.put("PetsToRespawn", petsList);

        NbtList bedsList = new NbtList();
        for (var entry : bedBindings.entrySet()) {
            NbtCompound bedData = new NbtCompound();
            bedData.putUuid("Uuid", entry.getKey());
            bedData.putInt("X", entry.getValue().getX());
            bedData.putInt("Y", entry.getValue().getY());
            bedData.putInt("Z", entry.getValue().getZ());
            bedsList.add(bedData);
        }
        nbt.put("BedBindings", bedsList);

        return nbt;
    }

    public static PetRespawnState fromNbt(NbtCompound nbt) {
        PetRespawnState state = new PetRespawnState();

        NbtList petsList = nbt.getList("PetsToRespawn", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < petsList.size(); i++) {
            NbtCompound entry = petsList.getCompound(i);
            UUID uuid = entry.getUuid("Uuid");
            NbtCompound data = entry.getCompound("Data");
            state.petsToRespawn.put(uuid, data);
        }

        NbtList bedsList = nbt.getList("BedBindings", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < bedsList.size(); i++) {
            NbtCompound entry = bedsList.getCompound(i);
            UUID uuid = entry.getUuid("Uuid");
            BlockPos pos = new BlockPos(entry.getInt("X"), entry.getInt("Y"), entry.getInt("Z"));
            state.bedBindings.put(uuid, pos);
        }

        return state;
    }

}


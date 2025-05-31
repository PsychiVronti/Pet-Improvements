package com.aquamarijn.petimprovements.util;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PetRespawnState extends PersistentState {
    public static final String NAME = "pet_respawn_state";
    public static final Type<PetRespawnState> TYPE = new Type<>(
            PetRespawnState::new,
            (nbt, ops) ->PetRespawnState.fromNbt(nbt),
            DataFixTypes.LEVEL
    );

    //Pet respawn location management
    public record PetRespawnLocation(BlockPos pos, RegistryKey<World> dimension) {
    }
    //Setter and getter for pet bed location data
    private final Map<UUID, PetRespawnLocation> respawnPoints = new HashMap<>();
    public void setRespawnLocation(UUID uuid, BlockPos pos, RegistryKey<World> dimension) {
        respawnPoints.put(uuid, new PetRespawnLocation(pos, dimension));
        markDirty();
    }
    public PetRespawnLocation getRespawnLocation(UUID uuid) {
        return respawnPoints.get(uuid);
    }
    public Map<UUID, PetRespawnLocation> getAllRespawnLocations() {
        return respawnPoints;
    }

    //Pet data management
    private final Map<UUID, NbtCompound> petData = new HashMap<>();
    public void setPetData(UUID uuid, NbtCompound data) {
        petData.put(uuid, data);
        markDirty();
    }
    public NbtCompound getPetData(UUID uuid) {
        return petData.get(uuid);
    }
    public void remove(UUID uuid) {
        petData.remove(uuid);
        respawnPoints.remove(uuid);
        markDirty();
    }




    //Save Nbt data for persistent storage
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for (Map.Entry<UUID, PetRespawnLocation> entry : respawnPoints.entrySet()) {
            NbtCompound tag = new NbtCompound();
            tag.putUuid("Uuid", entry.getKey());
            BlockPos pos = entry.getValue().pos;
            tag.putIntArray("Pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
            tag.putString("Dimension", entry.getValue().dimension.getValue().toString());
            list.add(tag);
        }
        nbt.put("RespawnPoints", list);
        return nbt;
    }
    //Recall Nbt data
    public static PetRespawnState fromNbt(NbtCompound nbt) {
        PetRespawnState state = new PetRespawnState();
        NbtList list = nbt.getList("RespawnPoints", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : list) {
            NbtCompound tag = (NbtCompound) element;
            UUID uuid = tag.getUuid("Uuid");
            Optional<BlockPos> optionalPos = NbtHelper.toBlockPos(tag, "Pos");
            if (optionalPos.isEmpty()) continue;
            BlockPos pos = optionalPos.get();
            RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(tag.getString("Dimension")));
            state.respawnPoints.put(uuid, new PetRespawnLocation(pos, dimension));
        }
        return state;
    }
}


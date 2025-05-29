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
    public static class PetRespawnLocation {
        public final BlockPos pos;
        public final RegistryKey<World> dimension;

        public PetRespawnLocation(BlockPos pos, RegistryKey<World> dimension) {
            this.pos = pos;
            this.dimension = dimension;
        }
    }




    //Setter and getter
    private final Map<UUID, PetRespawnLocation> respawnPoints = new HashMap<>();
    public void setRespawnPoints(UUID uuid, BlockPos pos, RegistryKey<World> dimension) {
        respawnPoints.put(uuid, new PetRespawnLocation(pos, dimension));
        markDirty();
    }
    public PetRespawnLocation getRespawnPoint(UUID uuid) {
        return respawnPoints.get(uuid);
    }





    //Save Nbt data
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


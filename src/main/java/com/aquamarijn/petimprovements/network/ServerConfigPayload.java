package com.aquamarijn.petimprovements.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ServerConfigPayload implements CustomPayload {

    public static final CustomPayload.Id<ServerConfigPayload> ID =
            new CustomPayload.Id<>(Identifier.of("petimprovements", "sync_config"));

    public static final PacketCodec<RegistryByteBuf, ServerConfigPayload> CODEC =
            PacketCodec.of(
                    (payload, buf) -> {
                        SyncedPetConfig synced = payload.getSyncedConfig();
                        buf.writeBoolean(synced.enablePetRespawn());
                        buf.writeInt(synced.timeOfDay());
                        buf.writeBoolean(synced.enablePetWander());
                        buf.writeInt(synced.wanderRadius());
                        buf.writeBoolean(synced.wolfImmuneToPlayer());
                    },
                    buf -> new ServerConfigPayload(new SyncedPetConfig(
                            buf.readBoolean(),
                            buf.readInt(),
                            buf.readBoolean(),
                            buf.readInt(),
                            buf.readBoolean()
                    ))
                    );
    private final SyncedPetConfig config;

    public ServerConfigPayload(SyncedPetConfig config) {
        this.config = config;
    }

    public SyncedPetConfig getSyncedConfig() {
        return config;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

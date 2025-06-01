package com.aquamarijn.petimprovements.config;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ServerConfigPayload(boolean enablePetRespawn, int wanderRadius, int timeofDay) implements CustomPayload {

    public static final Id<ServerConfigPayload> ID = new Id<>(Identifier.of("petimprovements", "sync_config"));

    // Packet Codec defined
    public static final PacketCodec<PacketByteBuf, ServerConfigPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ServerConfigPayload::enablePetRespawn,
            PacketCodecs.VAR_INT, ServerConfigPayload::wanderRadius,
            PacketCodecs.VAR_INT, ServerConfigPayload::timeofDay,
            ServerConfigPayload::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

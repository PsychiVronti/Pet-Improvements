package com.aquamarijn.petimprovements.config;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerConfigSyncSender {
    public static void send(ServerPlayerEntity player, ServerConfig config) {
        ServerConfigPayload payload = new ServerConfigPayload(
                config.enablePetRespawn,
                config.wanderRadius,
                config.timeOfDay
        );
        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(payload));
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            send(handler.player, ServerConfig.HANDLER.instance());
        }));
    }
}

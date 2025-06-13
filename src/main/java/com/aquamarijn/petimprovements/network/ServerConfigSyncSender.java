package com.aquamarijn.petimprovements.network;

import com.aquamarijn.petimprovements.PetImprovements;
import com.aquamarijn.petimprovements.config.ServerConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerConfigSyncSender {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            send(handler.getPlayer());
        });
    }

    public static void send(ServerPlayerEntity player) {
        SyncedPetConfig synced = SyncedPetConfig.from(ServerConfig.HANDLER.instance());
        ServerConfigPayload payload = new ServerConfigPayload(synced);

        PetImprovements.LOGGER.info("Sending ServerConfigPayLoad to {}", player.getName().getString());
        PetImprovements.LOGGER.info("Sending payload using: {}", ServerConfigPayload.class.getClassLoader());

        ServerPlayNetworking.send(player, payload);
        }
}

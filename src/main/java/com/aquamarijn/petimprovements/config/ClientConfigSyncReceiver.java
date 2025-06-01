package com.aquamarijn.petimprovements.config;

import com.aquamarijn.petimprovements.PetImprovements;
import com.aquamarijn.petimprovements.screen.ConfigScreenBuilder;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ClientConfigSyncReceiver {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
                ServerConfigPayload.ID,
                (payload, context) -> {
                    ClientConfig config = ClientConfig.HANDLER.instance();
                    config.enablePetRespawn = payload.enablePetRespawn();
                    config.wanderRadius = payload.wanderRadius();
                    config.timeOfDay = payload.timeofDay();
                    config.isServerSynced = true;

                    PetImprovements.LOGGER.info("[Client] Server config received. Sync enabled.");
                }
        );
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            ClientConfig.HANDLER.instance().isServerSynced = false;
        }));
    }
}

package com.aquamarijn.petimprovements.config;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ServerConfigPayloadRegistry {
    // Defensive register guard due to double calling somehow
    private static boolean registered = false;


    public static void register() {
        if (registered) return;
        registered = true;

        PayloadTypeRegistry.playS2C().register(
                ServerConfigPayload.ID,
                ServerConfigPayload.CODEC
        );
    }
}

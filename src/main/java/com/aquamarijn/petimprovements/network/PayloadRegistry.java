package com.aquamarijn.petimprovements.network;

import com.aquamarijn.petimprovements.PetImprovements;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PayloadRegistry {

    private static boolean clientBoundRegistered = false;

    public static void registerCommon() {
        PayloadTypeRegistry.playS2C().register(ServerConfigPayload.ID, ServerConfigPayload.CODEC);
        clientBoundRegistered = true;
        PetImprovements.LOGGER.info("[Common] Registered ServerConfigPayload for S2C.");

    }


    @Environment(EnvType.CLIENT)
    public static void registerClientBound() {
        if (clientBoundRegistered) {
            PetImprovements.LOGGER.warn("[Client] Skipping re-registration of ServerConfigPayload.");
            return;
        }

        PetImprovements.LOGGER.info("Client registering payload using: {}", ServerConfigPayload.class.getClassLoader());
        PayloadTypeRegistry.playS2C().register(ServerConfigPayload.ID, ServerConfigPayload.CODEC);
        clientBoundRegistered = true;
        PetImprovements.LOGGER.info("[Client] Registered ServerConfigPayload for S2C.");

    }

}

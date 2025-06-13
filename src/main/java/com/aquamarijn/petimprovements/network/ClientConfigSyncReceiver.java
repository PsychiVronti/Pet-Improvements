package com.aquamarijn.petimprovements.network;

import com.aquamarijn.petimprovements.PetImprovements;
import com.aquamarijn.petimprovements.config.ClientConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientConfigSyncReceiver {

    private static ClientConfig cachedClientConfig = null;

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(
                ServerConfigPayload.ID,
                (payload, context) -> {
                    ClientConfig clientConfig = ClientConfig.HANDLER.instance();
                    // Backup current config
                    cachedClientConfig = clientConfig.copy();

                    // Apply synced config
                    payload.getSyncedConfig().applyTo(clientConfig);
                    clientConfig.isServerSynced = true;
                    PetImprovements.LOGGER.info("[Client] Server config received. Sync enabled.");
                }
        );
        // Restore config on disconnect
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            ClientConfig clientConfig = ClientConfig.HANDLER.instance();

            if (cachedClientConfig != null) {
                cachedClientConfig.applyTo(clientConfig);
                PetImprovements.LOGGER.info("[Client] Restored client config after disconnect");
            } else {
                PetImprovements.LOGGER.warn("[Client] No cached config found to restore.");
            }


            cachedClientConfig = null;
            clientConfig.isServerSynced = false;
        }));
    }
}

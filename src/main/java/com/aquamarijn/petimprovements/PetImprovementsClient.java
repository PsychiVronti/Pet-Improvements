package com.aquamarijn.petimprovements;

import com.aquamarijn.petimprovements.config.ClientConfig;
import com.aquamarijn.petimprovements.config.ClientConfigSyncReceiver;
import com.aquamarijn.petimprovements.config.ServerConfigPayloadRegistry;
import net.fabricmc.api.ClientModInitializer;

public class PetImprovementsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        // Configs
        ClientConfig.HANDLER.load();
        ServerConfigPayloadRegistry.register();
        ClientConfigSyncReceiver.register();
    }
}

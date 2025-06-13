package com.aquamarijn.petimprovements;

import com.aquamarijn.petimprovements.config.ClientConfig;
import com.aquamarijn.petimprovements.network.ClientConfigSyncReceiver;
import com.aquamarijn.petimprovements.network.PayloadRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PetImprovementsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        // Configs
        PayloadRegistry.registerClientBound();
        ClientConfig.HANDLER.load();
        ClientConfigSyncReceiver.init();

        PetImprovements.LOGGER.info("Pet Improvements client-side loaded successfully!");
    }
}

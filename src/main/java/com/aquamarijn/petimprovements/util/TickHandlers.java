package com.aquamarijn.petimprovements.util;

import com.aquamarijn.petimprovements.config.ServerConfig;
import com.aquamarijn.petimprovements.entity.PetRespawnManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TickHandlers {

    public static void registerTickHandlers() {

        ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
            if (!serverWorld.isClient) {
                // Config variable
                int timeOfDay = ServerConfig.HANDLER.instance().timeOfDay;
                if (serverWorld.getTimeOfDay() % 24000 == timeOfDay) {
                    PetRespawnManager.respawnAllPendingPets(serverWorld);
                }
            }
        });
    }

}

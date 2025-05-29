package com.aquamarijn.petimprovements.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PetBindThrottle {
    private static final Set<UUID> processedPets = new HashSet<>();

    public static boolean shouldProcess(UUID petId) {
        return processedPets.add(petId); //return true if processed for the first time this tick
    }
    public static void registerPetBindThrottle() {
        ServerTickEvents.END_WORLD_TICK.register((ServerWorld world) -> {
            processedPets.clear(); //clear set once per tick
        });
    }
}

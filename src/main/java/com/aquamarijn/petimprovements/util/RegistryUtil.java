package com.aquamarijn.petimprovements.util;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;

import java.util.stream.Stream;

public class RegistryUtil {
    private static RegistryWrapper.WrapperLookup cachedLookup = null;

    //Cache the WrapperLookup
    public static RegistryWrapper.WrapperLookup getOrCreateRegistryLookup(MinecraftServer server) {
        if (cachedLookup == null) {
            Stream<RegistryWrapper.Impl<?>> wrappers = server.getRegistryManager()
                    .streamAllRegistries()
                    .map(DynamicRegistryManager.Entry::value)
                    .map(Registry::getReadOnlyWrapper);

            cachedLookup = RegistryWrapper.WrapperLookup.of(wrappers);
        }
        return cachedLookup;
    }

     // Clears the cached wrapper (optional, in case data packs are reloaded).
    public static void invalidateLookupCache() {
        cachedLookup = null;
    }
}

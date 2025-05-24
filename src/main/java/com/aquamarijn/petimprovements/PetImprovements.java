package com.aquamarijn.petimprovements;

import com.aquamarijn.petimprovements.block.ModBlocks;
import com.aquamarijn.petimprovements.block.ModItemGroups;
import com.aquamarijn.petimprovements.entity.PetRespawnManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetImprovements implements ModInitializer {
	public static final String MOD_ID = "petimprovements";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Logger LOGGER2 = LoggerFactory.getLogger("PetImprovements");

	@Override
	public void onInitialize() {
		LOGGER2.info("Pet Improvements mod initializing");
		ModBlocks.registerModBlocks();
		ModItemGroups.registerItemGroups();

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world instanceof ServerWorld serverWorld) {
				PetRespawnManager.tick(serverWorld);
			}
		});

		LOGGER.info("Pet Improvements loaded successfully!");
	}
}
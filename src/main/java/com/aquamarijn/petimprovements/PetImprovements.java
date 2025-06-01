package com.aquamarijn.petimprovements;

import com.aquamarijn.petimprovements.block.ModBlocks;
import com.aquamarijn.petimprovements.block.ModItemGroups;
import com.aquamarijn.petimprovements.config.ServerConfig;
import com.aquamarijn.petimprovements.config.ServerConfigPayloadRegistry;
import com.aquamarijn.petimprovements.config.ServerConfigSyncSender;
import com.aquamarijn.petimprovements.util.PetBindThrottle;
import com.aquamarijn.petimprovements.util.TickHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;

public class PetImprovements implements ModInitializer {
	public static final String MOD_ID = "petimprovements";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Pet Improvements mod initializing");

		// Blocks and Items
		ModBlocks.registerModBlocks();
		ModItemGroups.registerItemGroups();

		// Utils
		PetBindThrottle.registerPetBindThrottle();
		TickHandlers.registerTickHandlers();

		// Configs
		ServerConfig.HANDLER.load();
		ServerConfig.saveDefaultIfMissing();
		ServerConfigPayloadRegistry.register();
		ServerConfigSyncSender.register();


		LOGGER.info("Pet Improvements loaded successfully!");
	}
}
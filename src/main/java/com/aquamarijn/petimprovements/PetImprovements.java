package com.aquamarijn.petimprovements;

import com.aquamarijn.petimprovements.block.ModBlocks;
import com.aquamarijn.petimprovements.block.ModItemGroups;
import com.aquamarijn.petimprovements.util.PetBindThrottle;
import net.fabricmc.api.ModInitializer;

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
		PetBindThrottle.registerPetBindThrottle();

		LOGGER.info("Pet Improvements loaded successfully!");
	}
}
package com.aquamarijn.petimprovements;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetImprovements implements ModInitializer {
	public static final String MOD_ID = "petimprovements";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Pet Improvements loaded successfully!");
	}
}
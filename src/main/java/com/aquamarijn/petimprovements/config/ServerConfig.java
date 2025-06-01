package com.aquamarijn.petimprovements.config;

import com.aquamarijn.petimprovements.PetImprovements;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;

public class ServerConfig {
    //Making options
    @SerialEntry(comment = "Enable/Disable the pet respawning mechanics entirely. Pet Bed block will become decorative only.")
    public boolean enablePetRespawn = true; // enabled

    @SerialEntry(comment = "Change pet wander radius, integers represent blocks")
    public int wanderRadius = 8; // 8 blocks

    @SerialEntry(comment = "Set time of day for when dead pets bound to pet beds will respawn. Integers represent ticks (24000 ticks in a Minecraft day)")
    public int timeOfDay = 0; // 6AM


    // Config file path
    public static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("petimprovements.json5");

    // Save a default config file if missing
    public static void saveDefaultIfMissing() {
        if (!Files.exists(CONFIG_PATH)) {
            HANDLER.save();
            PetImprovements.LOGGER.info("Default server config written to: " + CONFIG_PATH);
        }
    }

    //Setting up config json file
    public static ConfigClassHandler<ServerConfig> HANDLER = ConfigClassHandler.createBuilder(ServerConfig.class)
            .id(Identifier.of("petimprovements", "server_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(CONFIG_PATH)
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();
}


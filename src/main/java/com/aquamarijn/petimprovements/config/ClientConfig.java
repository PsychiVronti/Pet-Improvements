package com.aquamarijn.petimprovements.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class ClientConfig {
    public boolean enablePetRespawn = true; // enabled
    public int timeOfDay = 0; // 6AM
    public boolean enablePetWander = true; // enabled
    public int wanderRadius = 8; // 8 blocks
    public boolean wolfImmuneToPlayer = true; // enabled

    public boolean isServerSynced = false;




    public ClientConfig copy() {
        ClientConfig copy = new ClientConfig();
        copy.enablePetRespawn = this.enablePetRespawn;
        copy.timeOfDay = this.timeOfDay;
        copy.enablePetWander = this.enablePetWander;
        copy.wanderRadius = this.wanderRadius;
        copy.wolfImmuneToPlayer = this.wolfImmuneToPlayer;
        copy.isServerSynced = this.isServerSynced;
        return copy;
    }

    public void applyTo(ClientConfig target) {
        target.enablePetRespawn = this.enablePetRespawn;
        target.timeOfDay = this.timeOfDay;
        target.enablePetWander = this.enablePetWander;
        target.wanderRadius = this.wanderRadius;
        target.wolfImmuneToPlayer = this.wolfImmuneToPlayer;
    }

    //Setting up config json file for saving and loading
    public static ConfigClassHandler<ClientConfig> HANDLER = ConfigClassHandler.createBuilder(ClientConfig.class)
            .id(Identifier.of("petimprovements", "client_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("petimprovements.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();
}

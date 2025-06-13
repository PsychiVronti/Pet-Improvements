package com.aquamarijn.petimprovements.network;

import com.aquamarijn.petimprovements.config.ClientConfig;
import com.aquamarijn.petimprovements.config.ServerConfig;

public record SyncedPetConfig(
        // Field declarations
        boolean enablePetRespawn,
        int timeOfDay,
        boolean enablePetWander,
        int wanderRadius,
        boolean wolfImmuneToPlayer
) {
    public void applyTo(ClientConfig config) {
        config.enablePetRespawn = this.enablePetRespawn();
        config.timeOfDay = this.timeOfDay();
        config.enablePetWander = this.enablePetWander();
        config.wanderRadius = this.wanderRadius();
        config.wolfImmuneToPlayer = this.wolfImmuneToPlayer;
        config.isServerSynced = true;
    }

    public static SyncedPetConfig from(ServerConfig config) {
        return new SyncedPetConfig(
                config.enablePetRespawn,
                config.timeOfDay,
                config.enablePetWander,
                config.wanderRadius,
                config.wolfImmuneToPlayer
        );
    }

    public static SyncedPetConfig from(ClientConfig config) {
        return new SyncedPetConfig(
                config.enablePetRespawn,
                config.timeOfDay,
                config.enablePetWander,
                config.wanderRadius,
                config.wolfImmuneToPlayer
        );
    }
}


/*
    // Primary constructor
    public SyncedPetConfig(boolean enablePetRespawn, int timeOfDay, boolean enablePetWander, int wanderRadius, boolean wolfImmuneToPlayer) {
        this.enablePetRespawn = enablePetRespawn;
        this.timeOfDay = timeOfDay;
        this.enablePetWander = enablePetWander;
        this.wanderRadius = wanderRadius;
        this.wolfImmuneToPlayer = wolfImmuneToPlayer;
    }

    public static SyncedPetConfig from(ServerConfig serverConfig) {
        return new SyncedPetConfig(
                serverConfig.enablePetRespawn,
                serverConfig.timeOfDay,
                serverConfig.enablePetWander,
                serverConfig.wanderRadius,
                serverConfig.wolfImmuneToPlayer
        );
    }

    public static SyncedPetConfig from(ServerConfigPayload payload) {
        return new SyncedPetConfig(
                payload.enablePetRespawn(),
                payload.timeOfDay(),
                payload.enablePetWander(),
                payload.wanderRadius(),
                payload.wolfImmuneToPlayer()
        );
    }

    // Apply payload configs to client for syncing
    public void applyTo(ClientConfig clientConfig) {
        clientConfig.enablePetRespawn = enablePetRespawn;
        clientConfig.timeOfDay = timeOfDay;
        clientConfig.enablePetWander = enablePetWander;
        clientConfig.wanderRadius = wanderRadius;
        clientConfig.wolfImmuneToPlayer = wolfImmuneToPlayer;
        clientConfig.isServerSynced = true;
    }

    // Convert to payload for use in sync
    public ServerConfigPayload toPayload() {
        return new ServerConfigPayload(
                this.enablePetRespawn,
                this.timeOfDay,
                this.enablePetWander,
                this.wanderRadius,
                this.wolfImmuneToPlayer
        );
    }

 */

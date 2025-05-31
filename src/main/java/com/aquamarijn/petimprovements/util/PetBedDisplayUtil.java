package com.aquamarijn.petimprovements.util;

import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PetBedDisplayUtil {
    public static void showBoundPets(ServerWorld world, BlockPos pos, PlayerEntity player) {
        PetRespawnState stateData = world.getServer()
                .getOverworld()
                .getPersistentStateManager()
                .getOrCreate(PetRespawnState.TYPE, PetRespawnState.NAME);

        List<String> lines = new ArrayList<>();
        for (Map.Entry<UUID, PetRespawnState.PetRespawnLocation> entry : stateData.getAllRespawnLocations().entrySet()) {

            //Check pet bed's data
            UUID uuid = entry.getKey();
            PetRespawnState.PetRespawnLocation location = entry.getValue();

            if (!location.dimension().equals(world.getRegistryKey())) continue;
            if (!location.pos().equals(pos)) continue;

            //Finding live pet to update data
            TameableEntity livePet = world.getEntitiesByClass(
                    TameableEntity.class,
                    new Box(pos).expand(16),
                    e -> e.getUuid().equals(uuid)
            ).stream().findFirst().orElse(null);

            UUID ownerUuid = null;
            String name = "Unnamed";
            String type;
            float health = -1.0f;


            if (livePet != null) {

                //Read pet owner if live pet(s) found
                ownerUuid = livePet.getOwnerUuid();
                if (ownerUuid == null || !ownerUuid.equals(player.getUuid())) continue;

                name = livePet.getName().getString();
                type = livePet.getType().toString();
                health = livePet.getHealth();

            } else {

                //Fallback to onDeath mixin for data
                NbtCompound petNbt = stateData.getPetData(uuid);
                if (petNbt == null) continue;

                //Get pet owner
                if (petNbt.containsUuid("Owner")) {
                    ownerUuid = petNbt.getUuid("Owner");
                } else if (petNbt.contains("Owner", NbtElement.INT_ARRAY_TYPE)) {
                    int[] arr = petNbt.getIntArray("Owner");
                    if (arr.length == 4) {
                        long most = ((long) arr[0] << 32) | (arr[1] & 0xFFFFFFFFL);
                        long least = ((long) arr[2] << 32) | (arr[3] & 0xFFFFFFFFL);
                        ownerUuid = new UUID(most, least);
                    }
                }
                if (ownerUuid == null || !ownerUuid.equals(player.getUuid())) continue;

                //Get name
                if (petNbt.contains("CustomName", NbtElement.STRING_TYPE)) {
                    String raw = petNbt.getString("CustomName");
                    try {
                        RegistryWrapper.WrapperLookup lookup = RegistryUtil.getOrCreateRegistryLookup(world.getServer());
                        MutableText parsed = Text.Serialization.fromLenientJson(raw, lookup);
                        name = parsed != null ? parsed.getString() : raw;
                    } catch (Exception e) {
                        name = raw;
                    }
                }

                //Get health
                type = petNbt.getString("id").replace("minecraft:", "");
                if (petNbt.contains("Health")) {
                    health = petNbt.getFloat("Health");
                }
            }

            //Send message with list of bound pets
            String color;
            switch (type.toLowerCase()) {
                case "wolf", "entity.minecraft.wolf" -> color = "§a"; //Green
                case "cat", "entity.minecraft.cat" -> color = "§9"; //Blue
                case "parrot", "entity.minecraft.parrot" -> color = "§d"; //Light Purple
                default -> color = "§7"; //Gray
            }
            lines.add(color + name + " - §c" + health + " HP");
        }

        if (lines.isEmpty()) {
            player.sendMessage(Text.literal("§7No bound pets."), false);
        } else {
            player.sendMessage(Text.literal("§6Pets bound to this bed:"), false);
            player.sendMessage(Text.literal("§7Key: §aWolves §7| §9Cats §7| §dParrots"), false);
            for (String line : lines) {
                player.sendMessage(Text.literal(line), false);
            }
        }
    }
}

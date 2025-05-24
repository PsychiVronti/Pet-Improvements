package com.aquamarijn.petimprovements.datagen;

import com.aquamarijn.petimprovements.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    //Constructor
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.WHITE_PET_BED);
        addDrop(ModBlocks.ORANGE_PET_BED);
        addDrop(ModBlocks.PINK_PET_BED);
        addDrop(ModBlocks.PURPLE_PET_BED);
        addDrop(ModBlocks.BLACK_PET_BED);
        addDrop(ModBlocks.BLUE_PET_BED);
        addDrop(ModBlocks.BROWN_PET_BED);
        addDrop(ModBlocks.CYAN_PET_BED);
        addDrop(ModBlocks.LIGHT_BLUE_PET_BED);
        addDrop(ModBlocks.GRAY_PET_BED);
        addDrop(ModBlocks.LIME_PET_BED);
        addDrop(ModBlocks.GREEN_PET_BED);
        addDrop(ModBlocks.LIGHT_GRAY_PET_BED);
        addDrop(ModBlocks.MAGENTA_PET_BED);
        addDrop(ModBlocks.YELLOW__PET_BED);
        addDrop(ModBlocks.RED_PET_BED);
    }
}

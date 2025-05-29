package com.aquamarijn.petimprovements.block;

import com.aquamarijn.petimprovements.PetImprovements;
import com.aquamarijn.petimprovements.block.custom.PetBedBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ModBlocks {

    // Map for dynamic retrieval
    public static final Map<String, Block> PET_BEDS;
    public static final Set<Block> PET_BED_BLOCKS = new HashSet<>();
    static {
        PET_BEDS = new HashMap<>();
    }




    //Adding custom simple blocks
    public static final Block WHITE_PET_BED = registerBlock("white_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block ORANGE_PET_BED = registerBlock("orange_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block MAGENTA_PET_BED = registerBlock("magenta_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIGHT_BLUE_PET_BED = registerBlock("light_blue_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block YELLOW__PET_BED = registerBlock("yellow_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIME_PET_BED = registerBlock("lime_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block PINK_PET_BED = registerBlock("pink_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block GRAY_PET_BED = registerBlock("gray_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIGHT_GRAY_PET_BED = registerBlock("light_gray_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block CYAN_PET_BED = registerBlock("cyan_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block PURPLE_PET_BED = registerBlock("purple_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block BLUE_PET_BED = registerBlock("blue_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block BROWN_PET_BED = registerBlock("brown_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block GREEN_PET_BED = registerBlock("green_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block RED_PET_BED = registerBlock("red_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));
    public static final Block BLACK_PET_BED = registerBlock("black_pet_bed",
            new PetBedBlock(AbstractBlock.Settings.create().strength(0.8f).sounds(BlockSoundGroup.WOOL)));

    //Adding all registered pet beds to set for easier recall
    static {
        PET_BED_BLOCKS.addAll(PET_BEDS.values());
    }

    //Helper methods to register custom blocks
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        Block registered = Registry.register(Registries.BLOCK, Identifier.of(PetImprovements.MOD_ID, name), block);
        PET_BEDS.put(name, registered);
        return registered;
    }
    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(PetImprovements.MOD_ID, name),
                new BlockItem(block, new Item.Settings()))
;    }
    //Register the custom blocks to mod initialization
    public static void registerModBlocks() {
        PetImprovements.LOGGER.info("Registering Mod Blocks for " + PetImprovements.MOD_ID);
    }
    //Get method for Pet Beds
    public static Block getPetBed(String colorName) {
        return PET_BEDS.get(colorName + "_pet_bed");
    }
}

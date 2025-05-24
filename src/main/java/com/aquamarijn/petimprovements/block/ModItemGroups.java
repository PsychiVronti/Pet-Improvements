package com.aquamarijn.petimprovements.block;

import com.aquamarijn.petimprovements.PetImprovements;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    //Adding group
    public static final ItemGroup PET_IMPROVEMENTS_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(PetImprovements.MOD_ID, "pet_improvements_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.WHITE_PET_BED))
                    .displayName(Text.translatable("itemgroup.petimprovements.pet_improvements_items"))
                    .entries((displayContext, entries) -> {
                        //Pet bed entries
                        entries.add(ModBlocks.WHITE_PET_BED);
                        entries.add(ModBlocks.ORANGE_PET_BED);
                        entries.add(ModBlocks.MAGENTA_PET_BED);
                        entries.add(ModBlocks.LIGHT_BLUE_PET_BED);
                        entries.add(ModBlocks.YELLOW__PET_BED);
                        entries.add(ModBlocks.LIME_PET_BED);
                        entries.add(ModBlocks.PINK_PET_BED);
                        entries.add(ModBlocks.GRAY_PET_BED);
                        entries.add(ModBlocks.LIGHT_GRAY_PET_BED);
                        entries.add(ModBlocks.CYAN_PET_BED);
                        entries.add(ModBlocks.PURPLE_PET_BED);
                        entries.add(ModBlocks.BLUE_PET_BED);
                        entries.add(ModBlocks.BROWN_PET_BED);
                        entries.add(ModBlocks.GREEN_PET_BED);
                        entries.add(ModBlocks.RED_PET_BED);
                        entries.add(ModBlocks.BLACK_PET_BED);
                    }).build());




    //Registering item groups
    public static void registerItemGroups() {
        PetImprovements.LOGGER.info("Registering item group for " + PetImprovements.MOD_ID);
    }
}

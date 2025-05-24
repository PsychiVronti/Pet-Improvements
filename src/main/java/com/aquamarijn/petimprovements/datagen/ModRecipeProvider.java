package com.aquamarijn.petimprovements.datagen;

import com.aquamarijn.petimprovements.block.ModBlocks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    //Constructor
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        //Recipes for all pet bed variants
        List<String> woolColors = List.of(
                "white", "orange", "magenta", "light_blue", "yellow", "lime",
                "pink", "gray", "light_gray", "cyan", "purple", "blue",
                "brown", "green", "red", "black"
        );
        for (String colorName : woolColors) {
            Item carpetItem = Registries.ITEM.get(Identifier.of(colorName + "_carpet"));
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.getPetBed(colorName))
                    .pattern("CCC")
                    .input('C', Ingredient.ofItems(carpetItem))
                    .criterion("has carpet", conditionsFromItem(Items.WHITE_CARPET))
                    .offerTo(recipeExporter, Identifier.of("petimprovements", colorName + "_pet_bed"));
        }

        //Recipe to allow different color carpets to be used to make standard white pet bed
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.WHITE_PET_BED)
                .pattern("CCC")
                .input('C', Ingredient.fromTag(ItemTags.WOOL_CARPETS))
                .criterion("has_any_carpet", conditionsFromTag(ItemTags.WOOL_CARPETS))
                .offerTo(recipeExporter, Identifier.of("petimprovements", "white_pet_bed_from_any_carpet"));

        // Additional shapeless dye recipes
        List.of(
                Map.entry("orange", Items.ORANGE_DYE),
                Map.entry("magenta", Items.MAGENTA_DYE),
                Map.entry("light_blue", Items.LIGHT_BLUE_DYE),
                Map.entry("yellow", Items.YELLOW_DYE),
                Map.entry("lime", Items.LIME_DYE),
                Map.entry("pink", Items.PINK_DYE),
                Map.entry("gray", Items.GRAY_DYE),
                Map.entry("light_gray", Items.LIGHT_GRAY_DYE),
                Map.entry("cyan", Items.CYAN_DYE),
                Map.entry("purple", Items.PURPLE_DYE),
                Map.entry("blue", Items.BLUE_DYE),
                Map.entry("brown", Items.BROWN_DYE),
                Map.entry("green", Items.GREEN_DYE),
                Map.entry("red", Items.RED_DYE),
                Map.entry("black", Items.BLACK_DYE)
        ).forEach(entry -> {
            String color = entry.getKey();
            Item dye = entry.getValue();
            ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.getPetBed(color))
                    .input(ModBlocks.WHITE_PET_BED)
                    .input(dye)
                    .criterion("has_white_pet_bed", conditionsFromItem(ModBlocks.WHITE_PET_BED))
                    .criterion("has_dye", conditionsFromItem(dye))
                    .offerTo(recipeExporter, Identifier.of("petimprovements", color + "_pet_bed_dye"));
        });
    }
}

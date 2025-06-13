package com.aquamarijn.petimprovements.screen;

import com.aquamarijn.petimprovements.config.ClientConfig;
import com.aquamarijn.petimprovements.config.ServerConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfigScreenBuilder {
    private static ClientConfig cfg() {
        return ClientConfig.HANDLER.instance();
    }

    public static Screen create(Screen parentScreen) {
        boolean serverSynced = cfg().isServerSynced;

        // Label Option
        Text syncedLabel = serverSynced
                ? Text.translatable("config.petimprovements.serversynced")
                    .formatted(Formatting.GRAY, Formatting.ITALIC)
                : Text.empty();

        return YetAnotherConfigLib.createBuilder()
                // TITLE
                .title(Text.translatable("config.petimprovements.title"))

                // CATEGORY: Pet Respawn
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("config.petimprovements.category.petrespawn"))
                        .tooltip(Text.translatable("config.petimprovements.categorydescription.petrespawn"))

                        // OPTION: Add labeloption for server sync notice
                        .option(LabelOption.create(syncedLabel))

                        // OPTION: Enable/Disable pet respawning
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("config.petimprovements.option.petrespawn"))
                                .description(OptionDescription.of(Text.translatable("config.petimprovements.optiondescription.petrespawn")))
                                .binding(true,
                                        () -> cfg().enablePetRespawn,
                                        newValue -> {
                                    if (!serverSynced) cfg().enablePetRespawn = newValue;
                                        })
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .formatValue(value -> value ? Text.literal("Enabled") : Text.literal("Disabled"))
                                        .coloured(true))
                                .build())

                        // OPTION: Change time of day for pets to respawn
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("config.petimprovements.option.petrespawntime"))
                                .description(OptionDescription.of
                                        (Text.translatable("config.petimprovements.categorydescription.petrespawntime.line1").append("\n\n")
                                        .append("\n").append(Text.translatable("config.petimprovements.categorydescription.petrespawntime.line2")
                                                        .copy().setStyle(Style.EMPTY.withUnderline(true))).append("\n")
                                        .append("\n").append(Text.translatable("config.petimprovements.categorydescription.petrespawntime.line3"))
                                        .append("\n").append(Text.translatable("config.petimprovements.categorydescription.petrespawntime.line4"))
                                        .append("\n").append(Text.translatable("config.petimprovements.categorydescription.petrespawntime.line5"))
                                        .append("\n").append(Text.translatable("config.petimprovements.categorydescription.petrespawntime.line6"))))
                                .binding(0,
                                        () -> cfg().timeOfDay,
                                        newValue ->  {
                                            if (!serverSynced) cfg().timeOfDay = newValue;
                                        })
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 24000)
                                        .step(1)
                                        .formatValue(value -> Text.literal(value + "")))
                                .build())

                        .build())


                // CATEGORY: Pet Behavior
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("config.petimprovements.category.petbehavior"))
                        .tooltip(Text.translatable("config.petimprovements.categorydescription.petbehavior"))

                        // OPTION: Add labeloption for server sync notice
                        .option(LabelOption.create(syncedLabel))

                        // OPTION: Enable/Disable
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("config.petimprovements.option.petwander"))
                                .description(OptionDescription.of(Text.translatable("config.petimprovements.optiondescription.petwander")))
                                .binding(true,
                                        () -> cfg().enablePetWander,
                                        newValue -> {
                                            if (!serverSynced) cfg().enablePetWander = newValue;
                                        })
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .formatValue(value -> value ? Text.literal("Enabled") : Text.literal("Disabled"))
                                        .coloured(true))
                                .build())

                        // OPTION: Change pet wandering radius
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("config.petimprovements.option.petwanderradius"))
                                .description(OptionDescription.of(Text.translatable("config.petimprovements.optiondescription.petwanderradius")))
                                .binding(8,
                                    () -> cfg().wanderRadius,
                                        newValue -> {
                                            if (!serverSynced) cfg().wanderRadius = newValue;
                                        })
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(3, 64)
                                        .step(1)
                                        .formatValue(value -> Text.literal(value + " blocks")))
                                .build())

                        // OPTION: Enable/Disable sound feedback when changing behavior (client-side)

                        // OPTION: Enable/Disable wolf immunity to player attacks
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("config.petimprovements.option.wolfimmunity"))
                                .description(OptionDescription.of(Text.translatable("config.petimprovements.optiondescription.wolfimmunity")))
                                .binding(true,
                                        () -> cfg().wolfImmuneToPlayer,
                                        newValue -> {
                                            if (!serverSynced) cfg().wolfImmuneToPlayer = newValue;
                                        })
                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                        .formatValue(value -> value ? Text.literal("Enabled") : Text.literal("Disabled"))
                                        .coloured(true))
                                .build())

                        .build())



                .save(() -> {
                    if (!serverSynced) ServerConfig.HANDLER.save();
                        })
                .build()
                .generateScreen(parentScreen);
    }
}

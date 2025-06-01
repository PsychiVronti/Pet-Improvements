package com.aquamarijn.petimprovements.block.custom;

import com.aquamarijn.petimprovements.config.ServerConfig;
import com.aquamarijn.petimprovements.entity.PetRespawnManager;
import com.aquamarijn.petimprovements.util.PetBedDisplayUtil;
import com.aquamarijn.petimprovements.util.PetBindThrottle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PetBedBlock extends Block {

    private static final Logger LOGGER = LoggerFactory.getLogger("PetImprovements");


    //Block outline/hitbox building
    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    private static final VoxelShape N_WALL_SHAPE = Block.createCuboidShape(0, 1, 0, 16, 2.5, 2);
    private static final VoxelShape S_WALL_SHAPE = Block.createCuboidShape(0, 1, 14, 16, 2.5, 16);
    private static final VoxelShape E_WALL_SHAPE = Block.createCuboidShape(14, 1, 2, 16, 2.5, 14);
    private static final VoxelShape W_WALL_SHAPE = Block.createCuboidShape(0, 1, 2, 2, 2.5, 14);
    private static final VoxelShape OUTLINE_SHAPE = VoxelShapes.union(BASE_SHAPE, N_WALL_SHAPE, S_WALL_SHAPE, E_WALL_SHAPE, W_WALL_SHAPE);
    //Block outline
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }
    //Block collision
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0, 0, 0, 16, 3.5, 16);
    }

    //Constructor
    public PetBedBlock(Settings settings) {
        super(settings);
    }

    //Allowed pet types
    private boolean isAllowedPetType(TameableEntity entity) {
        return entity instanceof WolfEntity
                || entity instanceof CatEntity
                || entity instanceof ParrotEntity;
    }

    //Bind pet to pet bed for respawn location
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        // Config: Pet Respawn enable/disable
        if (!ServerConfig.HANDLER.instance().enablePetRespawn) {
            return;
        }

        if (!world.isClient() && entity instanceof TameableEntity tameable && tameable.isTamed()) {
            //Process pet binding once per tick only
            UUID petId = tameable.getUuid();
            if (!PetBindThrottle.shouldProcess(petId)) {
                return;
            }

            //Check if pet type is allowed
            if (!isAllowedPetType(tameable)) return;

            boolean wasBound = PetRespawnManager.bindPetToBed(tameable, pos);
            if (wasBound) {
                Entity owner = tameable.getOwner();
                world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1.0f, 1.2f);
                if (owner instanceof ServerPlayerEntity player) {
                    ServerWorld serverWorld = (ServerWorld) tameable.getWorld();
                    player.sendMessage(
                            Text.translatable("text.petimprovements.pet_spawn_set", tameable.getName()), false);
                    PetBedDisplayUtil.showBoundPets(serverWorld, pos, player);

                } else {
                    LOGGER.warn("Unable to send spawn message");
                }
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    //Show bound pets
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        // Config: Pet Respawn enable/disable
        if (!ServerConfig.HANDLER.instance().enablePetRespawn) {
            return ActionResult.PASS;
        }

        if (world.isClient) return ActionResult.SUCCESS;

        if (player instanceof PlayerEntity serverPlayer) {
            PetBedDisplayUtil.showBoundPets((ServerWorld) world, pos, serverPlayer);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        // Config: Pet Respawn enable/disable
        if (!ServerConfig.HANDLER.instance().enablePetRespawn) {
            tooltip.add(Text.translatable("tooltip.petimprovements.petrespawndisabled.line1"));
            tooltip.add(Text.translatable("tooltip.petimprovements.petrespawndisabled.line2"));
            return;
        }
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.petimprovements.pet_bed.shift_down_line1"));
            tooltip.add(Text.translatable("tooltip.petimprovements.pet_bed.shift_down_line2"));
            tooltip.add(Text.translatable("tooltip.petimprovements.pet_bed.shift_down_line3"));
        } else {
            tooltip.add(Text.translatable("tooltip.petimprovements.pet_bed"));
        }

        super.appendTooltip(stack, context, tooltip, options);
    }

}

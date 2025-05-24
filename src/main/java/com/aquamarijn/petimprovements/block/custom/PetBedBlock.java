package com.aquamarijn.petimprovements.block.custom;

import com.aquamarijn.petimprovements.entity.PetRespawnManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class PetBedBlock extends Block {

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
        return Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    }

    //Constructor
    public PetBedBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient() && entity instanceof TameableEntity tameable && tameable.isTamed()) {
            boolean changed = PetRespawnManager.bindPetIfNew(tameable, pos);
            if (changed && tameable.getOwner() instanceof PlayerEntity player) {
                player.sendMessage(
                        Text.translatable("text.petimprovements.pet_spawn_set", tameable.getName()),
                        false);
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.petimprovements.pet_bed.shift_down_line1"));
            tooltip.add(Text.translatable("tooltip.petimprovements.pet_bed.shift_down_line2"));
        } else {
            tooltip.add(Text.translatable("tooltip.petimprovements.pet_bed"));
        }

        super.appendTooltip(stack, context, tooltip, options);
    }

}

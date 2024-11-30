package com.yupno.culinary_wizardry.block.custom.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseFoodAltarBlock extends BaseEntityBlock {
    protected BaseFoodAltarBlock(Properties pProperties) {
        super(pProperties);
    }

    /**
     * CUSTOM MODEL
     */
    private static final VoxelShape blockBox = box(1, 0, 1, 15, 13, 15);
    private static final VoxelShape blockCutout = box(3, 3, 3, 13, 13, 13);
    private static final VoxelShape SHAPE = Shapes.join(blockBox, blockCutout, BooleanOp.ONLY_FIRST);

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add();
    }

    /**
     * BLOCK ENTITY
     */

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return null;
    }
}

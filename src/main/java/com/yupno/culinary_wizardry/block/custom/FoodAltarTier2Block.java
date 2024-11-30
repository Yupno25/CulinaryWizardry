package com.yupno.culinary_wizardry.block.custom;

import com.yupno.culinary_wizardry.block.custom.base.BaseFoodAltarBlock;
import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.block.entity.custom.FoodAltarTier2BE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoodAltarTier2Block extends BaseFoodAltarBlock {
    public FoodAltarTier2Block(Properties pProperties) {
        super(pProperties);
    }


    /**
     * BLOCK ENTITY
     */

    @Override
    public void onRemove(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof FoodAltarTier2BE) {
                ((FoodAltarTier2BE) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos,
                                          @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);

            if (entity instanceof FoodAltarTier2BE) {
                if (!((FoodAltarTier2BE) entity).isFullAltar())
                    return InteractionResult.FAIL;

                NetworkHooks.openGui(((ServerPlayer) pPlayer), (FoodAltarTier2BE) entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new FoodAltarTier2BE(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) {
            return createTickerHelper(pBlockEntityType, ModBlockEntities.FOOD_ALTAR_TIER2_ENTITY.get(),
                    FoodAltarTier2BE::clientTick);
        } else {
            return createTickerHelper(pBlockEntityType, ModBlockEntities.FOOD_ALTAR_TIER2_ENTITY.get(),
                    FoodAltarTier2BE::serverTick);
        }
    }
}

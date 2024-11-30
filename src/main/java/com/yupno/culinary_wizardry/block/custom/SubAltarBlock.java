package com.yupno.culinary_wizardry.block.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.block.entity.custom.SubAltarBlockEntity;
import com.yupno.culinary_wizardry.utils.FoodType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubAltarBlock extends BaseEntityBlock {
    private final FoodType type;
    private final int tier;

    public SubAltarBlock(Properties pProperties, FoodType type, int tier) {
        super(pProperties);

        this.type = type;
        this.tier = tier;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add();
    }

    private static final VoxelShape blockBox = box(3, 0, 3, 13, 8, 13);
    private static final VoxelShape blockCutout = box(5, 4, 5, 11, 8, 11);
    private static final VoxelShape SHAPE = Shapes.join(blockBox, blockCutout, BooleanOp.ONLY_FIRST);


    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SHAPE;
    }

    /**
     * BLOCK ENTITY
     */

    public FoodType getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SubAltarBlockEntity) {
                ((SubAltarBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos,
                                          @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);

            if (entity instanceof SubAltarBlockEntity) {
                inputLogic(pPlayer, (SubAltarBlockEntity) entity, pHand);
            } else {
                throw new IllegalStateException("Block has wrong Block Entity!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    public void inputLogic(@NotNull Player pPlayer, @NotNull SubAltarBlockEntity entity, @NotNull InteractionHand pHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pHand);
        ItemStack itemInAltar = entity.getItemFromSlot();

        if (itemInHand.isEdible()) {
            // ItemInHand is edible, altar and hand item are the same and altar has space left
            if (itemInAltar.getItem().toString().equals(itemInHand.getItem().toString()) &&
                    (itemInAltar.getCount()) < itemInHand.getMaxStackSize()) {
                int totalCount = itemInHand.getCount() + itemInAltar.getCount();
                int spaceLeft = itemInAltar.getMaxStackSize() - itemInAltar.getCount();
                int itemsInAltar;

                if (itemInAltar.getMaxStackSize() > totalCount) {
                    itemsInAltar = totalCount;
                    pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                } else {
                    itemsInAltar = itemInAltar.getMaxStackSize();
                    pPlayer.setItemInHand(pHand, new ItemStack(itemInHand.getItem(), itemInHand.getCount() - spaceLeft));
                }

                entity.setStackInSlot(new ItemStack(itemInAltar.getItem(), itemsInAltar));
            }

            // ItemInHand is edible and altar is empty
            if (itemInAltar.isEmpty()) {
                entity.setStackInSlot(itemInHand);
                pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
            }

            // Hand is empty
        } else if (itemInHand.isEmpty()) {
            pPlayer.setItemInHand(pHand, itemInAltar);
            entity.setStackInSlot(ItemStack.EMPTY);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new SubAltarBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) {
            return createTickerHelper(pBlockEntityType, ModBlockEntities.SUB_ALTAR_BLOCK_ENTITY.get(),
                    SubAltarBlockEntity::clientTick);
        } else {
            return createTickerHelper(pBlockEntityType, ModBlockEntities.SUB_ALTAR_BLOCK_ENTITY.get(),
                    SubAltarBlockEntity::serverTick);
        }
    }
}

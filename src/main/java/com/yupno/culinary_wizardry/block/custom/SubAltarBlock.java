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
import net.minecraftforge.items.ItemStackHandler;
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
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
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SubAltarBlockEntity) {
                ((SubAltarBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);

            if (entity instanceof SubAltarBlockEntity) {
                ItemStack itemInHand = pPlayer.getItemInHand(pHand);
                ItemStack iteminAltar = ((SubAltarBlockEntity) entity).getItemFromSlot();

                if (itemInHand.isEdible()) {
                    // ItemInHand is edible, altar and hand item are the same and altar has space left
                    if(iteminAltar.getItem().toString().equals(itemInHand.getItem().toString()) &&
                            (iteminAltar.getCount()) < itemInHand.getMaxStackSize()){
                        int totalCount = itemInHand.getCount() + iteminAltar.getCount();
                        int spaceLeft = iteminAltar.getMaxStackSize() - iteminAltar.getCount();
                        int itemsInAltar;

                        if(iteminAltar.getMaxStackSize() > totalCount){
                            itemsInAltar = totalCount;
                            pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                        }else {
                            itemsInAltar = iteminAltar.getMaxStackSize();
                            pPlayer.setItemInHand(pHand, new ItemStack(itemInHand.getItem(), itemInHand.getCount() -  spaceLeft));
                        }

                        ((SubAltarBlockEntity) entity).setStackInSlot(new ItemStack(iteminAltar.getItem(), itemsInAltar));
                    }

                    // ItemInHand is edible and altar is empty
                    if(iteminAltar.isEmpty()){
                        ((SubAltarBlockEntity) entity).setStackInSlot(itemInHand);
                        pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                    }

                // Hand is empty
                } else if (itemInHand.isEmpty()) {
                    pPlayer.setItemInHand(pHand, iteminAltar);
                    ((SubAltarBlockEntity) entity).setStackInSlot(ItemStack.EMPTY);
                }
            } else {
                throw new IllegalStateException("Block has wrong Block Entity!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SubAltarBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide){
            return createTickerHelper(pBlockEntityType, ModBlockEntities.SUB_ALTAR_BLOCK_ENTITY.get(),
                    SubAltarBlockEntity::clientTick);
        }else {
            return createTickerHelper(pBlockEntityType, ModBlockEntities.SUB_ALTAR_BLOCK_ENTITY.get(),
                    SubAltarBlockEntity::serverTick);
        }
    }
}

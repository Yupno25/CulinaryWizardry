package com.yupno.culinary_wizardry.block.entity.custom;

import com.google.common.base.Predicates;
import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.screen.FoodAltarTier1Menu;
import com.yupno.culinary_wizardry.utils.SimpleFoodContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public class FoodAltarTier1BlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(6){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 5 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final ContainerData data;
    private int eatingProgress = 0;
    private int maxEatingProgress = 28;
    private final int tier = 1;

    /**
     *  SUBALTARSHIFT, HEIGHT, ALTARSHAPE, THISALTARHEIGHT AND ALTARLEVELS ARE CLOSELY CONNECTED
     *  Height matches the number of Arrays in altarLevels
     *
     *  A layer of only Air/Any Blocks doesn't work
     *  A layer with only a center block and other than that Any Block doesn't work
     * */
    private final int height = 6; // Height of the whole structure
    private final int thisAltarHeight = 1; // Height of this altar relative to the structure as a whole
    private final int blocksToCenter = 1; // How many blocks it takes to get to the center of the layer
    private final String[][] altarLevels = new String[][]{
                                            {"bBb", "B?B", "bBb"},
                                            {"w?w", "?f?", "w?w"},
                                            {"tAt", "A?A", "tAt"},
                                            {"AwA", "wuw", "AwA"},
                                            {"A?A", "?S?", "A?A"},
                                            {"AwA", "wdw", "AwA"}};
    private BlockPattern[] altarShapes = new BlockPattern[height];
    private final Vec3i subaltarShift = new Vec3i(0, 3, 0);
    private boolean isFullAltarShape = false;
    private int internalTicks = 0;
    private SubAltarBlockEntity subAltarBlockEntity = null;
    private int currentCulinaryEssenceCost = 0;
    private int remainingCulinaryEssenceCost = 0;
    private float currentCulinaryEssenceOverflow = 0;
    private final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;

    public FoodAltarTier1BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER1_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return FoodAltarTier1BlockEntity.this.eatingProgress;
                    case 1: return FoodAltarTier1BlockEntity.this.maxEatingProgress;
                    case 2: return FoodAltarTier1BlockEntity.this.getSaveCulinaryEssence();
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: FoodAltarTier1BlockEntity.this.eatingProgress = value; break;
                    case 1: FoodAltarTier1BlockEntity.this.maxEatingProgress = value; break;
                    case 2: break;
                }
            }

            public int getCount() {
                return 3;
            }
        };

        for (int i = 0; i < height; i++) {
            altarShapes[i] = BlockPatternBuilder.start()
                    .aisle(altarLevels[i][0], altarLevels[i][1], altarLevels[i][2])
                    .where('?', BlockInWorld.hasState(BlockStatePredicate.ANY))
                    .where('A', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.AIR)))
                    .where('b', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICKS)))
                    .where('B', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.CHISELED_STONE_BRICKS)))
                    .where('t', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SOUL_TORCH)))
                    .where('u', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_SLAB).where(TYPE, Predicates.equalTo(SlabType.TOP))))
                    .where('d', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_SLAB).where(TYPE, Predicates.equalTo(SlabType.BOTTOM))))
                    .where('w', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_WALL)))
                    .where('f', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.FOOD_ALTAR_TIER1.get())))
                    .where('S', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.LOW_SUB_ALTAR_CULINARY.get()))).build();
            //public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
            //.where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_STAIRS).where(FACING, Predicates.equalTo(Direction.NORTH))))
            //.where('v', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_STAIRS).where(FACING, Predicates.equalTo(Direction.SOUTH))))
            //.where('<', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_STAIRS).where(FACING, Predicates.equalTo(Direction.WEST))))
            //.where('>', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_STAIRS).where(FACING, Predicates.equalTo(Direction.EAST))))
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", eatingProgress);
        tag.putInt("internalTicks", internalTicks);
        tag.putBoolean("isComplete", isFullAltarShape);
        tag.putInt("currentCulinary", currentCulinaryEssenceCost);
        tag.putInt("remainingCulinary", remainingCulinaryEssenceCost);
        tag.putFloat("currentOverflow", currentCulinaryEssenceOverflow);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        eatingProgress = nbt.getInt("progress");
        internalTicks = nbt.getInt("internalTicks");
        isFullAltarShape = nbt.getBoolean("isComplete");
        currentCulinaryEssenceCost = nbt.getInt("currentCulinary");
        remainingCulinaryEssenceCost = nbt.getInt("remainingCulinary");
        currentCulinaryEssenceOverflow = nbt.getFloat("currentOverflow");
    }

    private void update(){
        // Check for complete Altar Shape
        for (int i = 0; i < altarShapes.length; i++) {
            if(level == null){
                failedUpdate();
                return;
            }

            BlockPattern.BlockPatternMatch layerIsCorrect = altarShapes[i].find(level, worldPosition.offset(0, i - thisAltarHeight,0));
            if(layerIsCorrect == null){
                failedUpdate();
                return;
            }

            // Checks if this Block is actually the center Block of this layer
            if(!layerIsCorrect.getFrontTopLeft().offset(-blocksToCenter, 0, -blocksToCenter).equals(worldPosition.offset(0, i - thisAltarHeight,0))){
                failedUpdate();
                return;
            }
        }

        isFullAltarShape = true;

        // Cache SubAltarBlockEntity
        subAltarBlockEntity = ((SubAltarBlockEntity)(level.getBlockEntity(new BlockPos(worldPosition.offset(subaltarShift)))));
    }

    private void failedUpdate(){
        isFullAltarShape = false;
        subAltarBlockEntity = null;
    }

    public boolean isFullAltarShape(){
        return isFullAltarShape;
    }

    public SubAltarBlockEntity getSubAltar(){
        return subAltarBlockEntity;
    }

    public int getTier(){
        return tier;
    }

    private int getSaveCulinaryEssence(){
        if(subAltarBlockEntity == null)
            return 0;

        return subAltarBlockEntity.getCulinaryEssence();
    }

    /**
     * RECIPE STUFF
     * */
    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier1BlockEntity entity) {
        entity.internalTicks++;
        if(entity.internalTicks % 20 == 0){
            entity.internalTicks = 0;
            entity.update();
        }


        if(hasRecipe(entity)) {
            entity.eatingProgress++;

            SubAltarBlockEntity subAltarBlockEntity = entity.getSubAltar();

            if(subAltarBlockEntity == null)
                return;

            setChanged(pLevel, pPos, pState);
            if(entity.eatingProgress > entity.maxEatingProgress) {
                craftItem(entity);
                return;
            }

            // Gets 1/maxProgress of the essence so that the essence drains slowly and not all at once
            // Needs to round since the division is almost certainly not an int
            // The overflow from that is saved to ensure that the full cost will be paid
            float temp = (float) entity.currentCulinaryEssenceCost / entity.maxEatingProgress;
            int temp2 = Math.round(temp + entity.currentCulinaryEssenceOverflow);
            if(temp2 == Math.round(temp)){
                entity.currentCulinaryEssenceOverflow += temp - temp2;
            }else {
                entity.currentCulinaryEssenceOverflow -= temp2 - temp;
            }


            subAltarBlockEntity.setCulinaryEssence(subAltarBlockEntity.getCulinaryEssence() - temp2);
            entity.remainingCulinaryEssenceCost -= temp2;
        } else {
            entity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean hasRecipe(FoodAltarTier1BlockEntity entity) {
        SubAltarBlockEntity subAltarBlockEntity = entity.getSubAltar();

        if(subAltarBlockEntity == null)
            return false;

        Level level = entity.level;
        SimpleFoodContainer inventory;

        if(entity.currentCulinaryEssenceCost != 0 && (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost) == 0){
            inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), subAltarBlockEntity.getCulinaryEssence()
                    + entity.currentCulinaryEssenceCost);
        }else {
            inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), subAltarBlockEntity.getCulinaryEssence()
                    + (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost));
        }

        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<FoodAltarRecipe> match = level.getRecipeManager()
                .getRecipeFor(FoodAltarRecipe.Type.INSTANCE, inventory, level);

        if(match.isPresent() && canInsertAmountIntoOutputSlot(inventory) && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem())){
            entity.currentCulinaryEssenceCost = match.get().getPureCulinaryEssenceCost();
            if(entity.remainingCulinaryEssenceCost == 0){
                entity.remainingCulinaryEssenceCost = entity.currentCulinaryEssenceCost;
            }
            return true;
        }
        return false;
    }

    private static void craftItem(FoodAltarTier1BlockEntity entity) {
        SubAltarBlockEntity subAltarBlockEntity = entity.getSubAltar();

        if(subAltarBlockEntity == null)
            return;

        Level level = entity.level;
        SimpleFoodContainer inventory;

        if(entity.currentCulinaryEssenceCost != 0 && (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost) == 0){
            inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), subAltarBlockEntity.getCulinaryEssence()
                    + entity.currentCulinaryEssenceCost);
        }else {
            inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), subAltarBlockEntity.getCulinaryEssence()
                    + (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost));
        }


        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<FoodAltarRecipe> match = level.getRecipeManager()
                .getRecipeFor(FoodAltarRecipe.Type.INSTANCE, inventory, level);

        if(match.isPresent()) {
            entity.itemHandler.extractItem(0,1, false);

            entity.itemHandler.setStackInSlot(1, new ItemStack(match.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(1).getCount() + 1));

            entity.resetProgress();
        }
    }

    private void resetProgress() {
        this.eatingProgress = 0;
        this.currentCulinaryEssenceCost = 0;
        this.currentCulinaryEssenceOverflow = 0;
        this.remainingCulinaryEssenceCost = 0;
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleFoodContainer inventory, ItemStack output) {
        return inventory.getItem(1).getItem() == output.getItem() || inventory.getItem(1).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleFoodContainer inventory) {
        return inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
    }

    /**
     * Servertick stuff
     * */

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    /**
     * Basic stuff
     * */

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) {
                return lazyItemHandler.cast();
            }

            if(isFullAltarShape()){
                if (side == Direction.DOWN)
                    return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 5, (index, stack) -> false)).cast();
                else
                    return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> false, (index, stack) -> itemHandler.isItemValid(0, stack))).cast();
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier1");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory playerInventory, Player pPlayer) {
        return new FoodAltarTier1Menu(pContainerId, playerInventory, this, this.data);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}

package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.screen.FoodAltarTier0Menu;
import com.yupno.culinary_wizardry.utils.EssenceCalculation;
import com.yupno.culinary_wizardry.utils.SimpleFoodContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FoodAltarTier0BlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 1 -> false;
                case 2 -> stack.getItem().isEdible();
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final ContainerData data;
    private int currentCulinaryEssenceCost = 0;
    private int remainingCulinaryEssenceCost = 0;
    private float currentCulinaryEssenceOverflow = 0;
    private int progress = 0;
    private int maxProgress = 72;
    private int eatingProgress = 0;
    private int maxEatingProgress = 28;
    private int culinaryEssence = 0;
    private int maxCulinaryEssence = 1000;
    private final int tier = 0;

    public FoodAltarTier0BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER0_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return FoodAltarTier0BlockEntity.this.progress;
                    case 1: return FoodAltarTier0BlockEntity.this.maxProgress;
                    case 2: return FoodAltarTier0BlockEntity.this.eatingProgress;
                    case 3: return FoodAltarTier0BlockEntity.this.maxEatingProgress;
                    case 4: return FoodAltarTier0BlockEntity.this.culinaryEssence;
                    case 5: return FoodAltarTier0BlockEntity.this.maxCulinaryEssence;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: FoodAltarTier0BlockEntity.this.progress = value; break;
                    case 1: FoodAltarTier0BlockEntity.this.maxProgress = value; break;
                    case 2: FoodAltarTier0BlockEntity.this.eatingProgress = value; break;
                    case 3: FoodAltarTier0BlockEntity.this.maxEatingProgress = value; break;
                    case 4: FoodAltarTier0BlockEntity.this.culinaryEssence = value; break;
                    case 5: FoodAltarTier0BlockEntity.this.maxCulinaryEssence = value; break;
                }
            }

            public int getCount() {
                return 6;
            }
        };
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("culinaryEssence", culinaryEssence);
        tag.putInt("progress", progress);
        tag.putInt("eatingProgress", eatingProgress);
        tag.putInt("currentCulinary", currentCulinaryEssenceCost);
        tag.putInt("remainingCulinary", remainingCulinaryEssenceCost);
        tag.putFloat("currentOverflow", currentCulinaryEssenceOverflow);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        culinaryEssence = nbt.getInt("culinaryEssence");
        progress = nbt.getInt("progress");
        eatingProgress = nbt.getInt("eatingProgress");
        currentCulinaryEssenceCost = nbt.getInt("currentCulinary");
        remainingCulinaryEssenceCost = nbt.getInt("remainingCulinary");
        currentCulinaryEssenceOverflow = nbt.getFloat("currentOverflow");
    }

    /**
     * RECIPE STUFF
     * */

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier0BlockEntity entity) {
        if(entity.itemHandler.getStackInSlot(2).isEdible() && entity.culinaryEssence < entity.maxCulinaryEssence){
            entity.eatingProgress++;
            setChanged(pLevel, pPos, pState);

            if(entity.eatingProgress > entity.maxEatingProgress){
                entity.culinaryEssence = Math.min(entity.culinaryEssence +
                        EssenceCalculation.calculateCulinaryFoodEssence(entity.itemHandler.getStackInSlot(2), 0), entity.maxCulinaryEssence);

                entity.itemHandler.extractItem(2, 1, false);
                entity.resetFoodProgress();
            }
        }else {
            entity.resetFoodProgress();
            setChanged(pLevel, pPos, pState);
        }

        if(hasRecipe(entity)) {
            entity.progress++;


            setChanged(pLevel, pPos, pState);
            if(entity.progress > entity.maxProgress) {
                craftItem(entity);
                return;
            }

            // Gets 1/maxProgress of the essence so that the essence drains slowly and not all at once
            // Needs to round since the division is almost certainly not an int
            // The overflow from that is saved to ensure that the full cost will be paid
            float temp = (float) entity.currentCulinaryEssenceCost / entity.maxProgress;
            int temp2 = Math.round(temp + entity.currentCulinaryEssenceOverflow);
            if(temp2 == Math.round(temp)){
                entity.currentCulinaryEssenceOverflow += temp - temp2;
            }else {
                entity.currentCulinaryEssenceOverflow -= temp2 - temp;
            }

            entity.culinaryEssence -= temp2;
            entity.remainingCulinaryEssenceCost -= temp2;
        } else {
            entity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean hasRecipe(FoodAltarTier0BlockEntity entity) {
        Level level = entity.level;
        SimpleFoodContainer inventory;

        if(entity.currentCulinaryEssenceCost != 0 && (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost) == 0){
            inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), entity.culinaryEssence
                    + entity.currentCulinaryEssenceCost, 0, 0, 0, 0, 0, entity.tier);
        }else {
            inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), entity.culinaryEssence
                    + (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost), 0,
                    0, 0, 0, 0, entity.tier);
        }



        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        List<FoodAltarRecipe> recipes = level.getRecipeManager().getAllRecipesFor(FoodAltarRecipe.Type.INSTANCE);
        Optional<FoodAltarRecipe> match = recipes.stream().sorted(Comparator.comparingInt(recipe -> ((FoodAltarRecipe) recipe).getRecipeList().size()).reversed())
                .filter(recipe -> recipe.matches(inventory, level)).findFirst();

        if(match.isPresent() && canInsertAmountIntoOutputSlot(inventory) && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem())){
            entity.currentCulinaryEssenceCost = match.get().getCulinaryEssenceCost();
            if(entity.remainingCulinaryEssenceCost == 0){
                entity.remainingCulinaryEssenceCost = entity.currentCulinaryEssenceCost;
            }
            return true;
        }
        return false;
    }

    private static void craftItem(FoodAltarTier0BlockEntity entity) {
        Level level = entity.level;
        SimpleFoodContainer inventory;

        if(entity.currentCulinaryEssenceCost != 0 && (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost) == 0){
            inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), entity.culinaryEssence
                    + entity.currentCulinaryEssenceCost, 0, 0, 0, 0, 0, entity.tier);
        }else {
            inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), entity.culinaryEssence
                    + (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost), 0,
                    0, 0, 0, 0, entity.tier);
        }

        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        List<FoodAltarRecipe> recipes = level.getRecipeManager().getAllRecipesFor(FoodAltarRecipe.Type.INSTANCE);
        Optional<FoodAltarRecipe> match = recipes.stream().sorted(Comparator.comparingInt(recipe -> ((FoodAltarRecipe) recipe).getRecipeList().size()).reversed())
                .filter(recipe -> recipe.matches(inventory, level)).findFirst();

        if(match.isPresent()) {
            entity.itemHandler.extractItem(0,1, false);

            entity.itemHandler.setStackInSlot(1, new ItemStack(match.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(1).getCount() + 1));

            entity.resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.currentCulinaryEssenceCost = 0;
        this.currentCulinaryEssenceOverflow = 0;
        this.remainingCulinaryEssenceCost = 0;
    }

    private void resetFoodProgress() {
        this.eatingProgress = 0;
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

            if (side == Direction.UP)
                return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> false, (index, stack) -> itemHandler.isItemValid(0, stack) && index == 0)).cast();
            else if (side == Direction.DOWN)
                return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 1, (index, stack) -> false)).cast();
            else
                return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> false, (index, stack) -> itemHandler.isItemValid(2, stack) && index == 2)).cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier0");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory playerInventory, Player pPlayer) {
        return new FoodAltarTier0Menu(pContainerId, playerInventory, this, this.data);
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

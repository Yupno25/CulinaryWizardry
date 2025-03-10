package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.block.entity.custom.base.WrappedHandler;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.screen.FoodAltarTier0Menu;
import com.yupno.culinary_wizardry.utils.EssenceCalculation;
import com.yupno.culinary_wizardry.utils.FoodType;
import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
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
import java.util.Optional;

public class FoodAltarTier0BE extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
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
    private final int maxProgress = 72;
    private int eatingProgress = 0;
    private final int maxEatingProgress = 28;
    private int culinaryEssence = 0;
    private final int maxCulinaryEssence;
    private final int tier = 0;

    public FoodAltarTier0BE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER0_ENTITY.get(), pPos, pBlockState);
        maxCulinaryEssence = EssenceCalculation.calculateMaxEssence(tier);

        this.data = new ContainerData() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> FoodAltarTier0BE.this.progress;
                    case 1 -> FoodAltarTier0BE.this.maxProgress;
                    case 2 -> FoodAltarTier0BE.this.eatingProgress;
                    case 3 -> FoodAltarTier0BE.this.maxEatingProgress;
                    case 4 -> FoodAltarTier0BE.this.culinaryEssence;
                    case 5 -> FoodAltarTier0BE.this.maxCulinaryEssence;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> FoodAltarTier0BE.this.progress = value;
                    case 2 -> FoodAltarTier0BE.this.eatingProgress = value;
                    case 4 -> FoodAltarTier0BE.this.culinaryEssence = value;
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
    public void load(@NotNull CompoundTag nbt) {
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
     */

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier0BE entity) {
        culinaryEssenceConversion(pLevel, pPos, pState, entity);
        craftingLogic(pLevel, pPos, pState, entity);
    }

    private static void culinaryEssenceConversion(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier0BE entity) {
        if (entity.itemHandler.getStackInSlot(2).isEdible() && entity.culinaryEssence < entity.maxCulinaryEssence) {
            entity.eatingProgress++;
            setChanged(pLevel, pPos, pState);

            if (entity.eatingProgress > entity.maxEatingProgress) {
                entity.culinaryEssence = Math.min(entity.culinaryEssence +
                        EssenceCalculation.calculateFoodEssence(entity.itemHandler.getStackInSlot(2), entity.getTier(), FoodType.CULINARY), entity.maxCulinaryEssence);

                entity.itemHandler.extractItem(2, 1, false);
                entity.resetFoodProgress();
            }
        } else {
            entity.resetFoodProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static void craftingLogic(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier0BE entity) {
        if (checkOrCraftItem(entity, false)) {
            entity.progress++;

            setChanged(pLevel, pPos, pState);
            if (entity.progress > entity.maxProgress) {
                checkOrCraftItem(entity, true);
                return;
            }

            // Gets 1/maxProgress of the essence so that the essence drains slowly and not all at once
            // Needs to round since the division is almost certainly not an int
            // The overflow from that is saved to ensure that the full cost will be paid
            float temp = (float) entity.currentCulinaryEssenceCost / entity.maxProgress;
            int temp2 = Math.round(temp + entity.currentCulinaryEssenceOverflow);
            if (temp2 == Math.round(temp)) {
                entity.currentCulinaryEssenceOverflow += temp - temp2;
            } else {
                entity.currentCulinaryEssenceOverflow -= temp2 - temp;
            }

            entity.culinaryEssence -= temp2;
            entity.remainingCulinaryEssenceCost -= temp2;
        } else {
            entity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean checkOrCraftItem(FoodAltarTier0BE entity, boolean craft) {
        Level level = entity.level;
        SimpleEssenceContainer inventory;

        if (entity.currentCulinaryEssenceCost != 0 && (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost) == 0) {
            inventory = new SimpleEssenceContainer(entity.itemHandler.getSlots(), entity.culinaryEssence
                    + entity.currentCulinaryEssenceCost, 0, 0, 0, 0, 0, entity.getTier());
        } else {
            inventory = new SimpleEssenceContainer(entity.itemHandler.getSlots(), entity.culinaryEssence
                    + (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost), 0,
                    0, 0, 0, 0, entity.getTier());
        }

        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<FoodAltarRecipe> match = level.getRecipeManager().getRecipeFor(FoodAltarRecipe.Type.INSTANCE, inventory, level);

        if (match.isPresent() && canInsertAmountIntoOutputSlot(inventory) && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem())) {
            if (craft) {
                entity.itemHandler.extractItem(0, 1, false);
                entity.itemHandler.setStackInSlot(1, new ItemStack(match.get().getResultItem().getItem(),
                        entity.itemHandler.getStackInSlot(1).getCount() + 1));

                entity.resetProgress();
            } else {
                entity.currentCulinaryEssenceCost = match.get().getCulinaryEssenceCost();
                if (entity.remainingCulinaryEssenceCost == 0) {
                    entity.remainingCulinaryEssenceCost = entity.currentCulinaryEssenceCost;
                }
                return true;
            }
        }

        return false;
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

    private static boolean canInsertItemIntoOutputSlot(SimpleEssenceContainer inventory, ItemStack output) {
        return inventory.getItem(1).getItem() == output.getItem() || inventory.getItem(1).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleEssenceContainer inventory) {
        return inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
    }

    /**
     * Particles
     */
    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier0BE pBlockEntity) {

    }


    /**
     * Server Sync stuff
     */

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    /**
     * Input / Output Logic (for Hoppers and the like)
     */

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
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

    /**
     * Basic stuff
     */
    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier0");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory playerInventory, @NotNull Player pPlayer) {
        return new FoodAltarTier0Menu(pContainerId, playerInventory, this, this.data);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
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

    /**
     * Getter and Setter Methods
     */

    public int getTier() {
        return tier;
    }
}

package com.yupno.culinary_wizardry.block.entity.custom.base;

import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CoreFoodAltarBE extends BlockEntity {
    public final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 5 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };
    public LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private boolean isFullAltar = false;
    public int internalTicks = 0;
    public int[] usedItemSlots = {0, 1, 2, 3, 4};
    public int craftingProgress = 0;
    public final int maxCraftingProgress = 28;

    public final ContainerData data;
    private final int tier;

    public CoreFoodAltarBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int tier) {
        super(pType, pPos, pBlockState);
        this.tier = tier;

        this.data = new ContainerData() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> CoreFoodAltarBE.this.craftingProgress;
                    case 1 -> CoreFoodAltarBE.this.maxCraftingProgress;
                    case 2 -> CoreFoodAltarBE.this.usedItemSlots[0];
                    case 3 -> CoreFoodAltarBE.this.usedItemSlots[1];
                    case 4 -> CoreFoodAltarBE.this.usedItemSlots[2];
                    case 5 -> CoreFoodAltarBE.this.usedItemSlots[3];
                    case 6 -> CoreFoodAltarBE.this.usedItemSlots[4];
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CoreFoodAltarBE.this.craftingProgress = value;
                    case 2 -> CoreFoodAltarBE.this.usedItemSlots[0] = value;
                    case 3 -> CoreFoodAltarBE.this.usedItemSlots[1] = value;
                    case 4 -> CoreFoodAltarBE.this.usedItemSlots[2] = value;
                    case 5 -> CoreFoodAltarBE.this.usedItemSlots[3] = value;
                    case 6 -> CoreFoodAltarBE.this.usedItemSlots[4] = value;
                }
            }

            public int getCount() {
                return 7;
            }
        };
    }

    public static boolean canInsertItemIntoOutputSlot(SimpleEssenceContainer inventory, ItemStack output) {
        return inventory.getItem(5).getItem() == output.getItem() || inventory.getItem(5).isEmpty();
    }

    public static boolean canInsertAmountIntoOutputSlot(SimpleEssenceContainer inventory) {
        return inventory.getItem(5).getMaxStackSize() > inventory.getItem(5).getCount();
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

            if (isFullAltar()) {
                return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 5, (index, stack) -> itemHandler.isItemValid(0, stack))).cast();
            }
        }

        return super.getCapability(cap, side);
    }

    /**
     * Basic stuff
     */

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
    public boolean isFullAltar() {
        return isFullAltar;
    }

    public void setFullAltar(boolean isFullAltarShape) {
        this.isFullAltar = isFullAltarShape;
    }

    public int getTier() {
        return tier;
    }
}

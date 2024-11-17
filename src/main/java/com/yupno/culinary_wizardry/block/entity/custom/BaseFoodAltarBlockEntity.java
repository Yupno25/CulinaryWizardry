package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.utils.FoodType;
import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
import com.yupno.culinary_wizardry.utils.SubAltarContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
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
import java.util.Map;

public class BaseFoodAltarBlockEntity extends BlockEntity {
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

    public BaseFoodAltarBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private boolean isFullAltar = false;
    public int internalTicks = 0;
    public int[] usedItemSlots = {0, 1, 2, 3, 4};

    public boolean isFullAltar() {
        return isFullAltar;
    }

    public void setFullAltar(boolean isFullAltarShape) {
        this.isFullAltar = isFullAltarShape;
    }

    public static boolean canInsertItemIntoOutputSlot(SimpleEssenceContainer inventory, ItemStack output) {
        return inventory.getItem(5).getItem() == output.getItem() || inventory.getItem(5).isEmpty();
    }

    public static boolean canInsertAmountIntoOutputSlot(SimpleEssenceContainer inventory) {
        return inventory.getItem(5).getMaxStackSize() > inventory.getItem(5).getCount();
    }

    /**
     * Servertick stuff
     */

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
     * ONLY FOR TIER 2 ALTAR AND ABOVE
     */

    public final Vec3i[] xSubAltarShifts = new Vec3i[]{
            new Vec3i(0, 0, 3),
            new Vec3i(0, 0, -3),
            new Vec3i(3, 0, 2),
            new Vec3i(3, 0, -2),
            new Vec3i(-3, 0, 2),
            new Vec3i(-3, 0, -2),
    };

    public final Vec3i[] zSubAltarShifts = new Vec3i[]{
            new Vec3i(3, 0, 0),
            new Vec3i(-3, 0, 0),
            new Vec3i(2, 0, 3),
            new Vec3i(-2, 0, 3),
            new Vec3i(2, 0, -3),
            new Vec3i(-2, 0, -3),
    };

    public final Map<FoodType, SubAltarContainer> subAltars = Map.of(
            FoodType.CULINARY, new SubAltarContainer(FoodType.CULINARY),
            FoodType.FRUITS, new SubAltarContainer(FoodType.FRUITS),
            FoodType.GRAINS, new SubAltarContainer(FoodType.GRAINS),
            FoodType.PROTEINS, new SubAltarContainer(FoodType.PROTEINS),
            FoodType.SUGARS, new SubAltarContainer(FoodType.SUGARS),
            FoodType.VEGETABLES, new SubAltarContainer(FoodType.VEGETABLES)
    );

    public static int calculateEssence(BlockEntity entity, FoodType foodType) {
        SubAltarContainer subAltar = null;
        if (entity instanceof BaseFoodAltarBlockEntity) {
            subAltar = ((BaseFoodAltarBlockEntity) entity).subAltars.get(foodType);
        }

        if (subAltar.getCurrentEssenceCost() != 0 && (subAltar.getCurrentEssenceCost() - subAltar.getRemainingEssenceCost()) == 0) {
            return subAltar.getSubAltarBlockEntity().getEssence() + subAltar.getCurrentEssenceCost();
        } else {
            return subAltar.getSubAltarBlockEntity().getEssence() + (subAltar.getCurrentEssenceCost() - subAltar.getRemainingEssenceCost());
        }
    }
}

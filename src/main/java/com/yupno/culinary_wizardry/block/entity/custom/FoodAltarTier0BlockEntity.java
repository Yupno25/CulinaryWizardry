package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.item.ModItems;
import com.yupno.culinary_wizardry.screen.FoodAltarTier0Menu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class FoodAltarTier0BlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public FoodAltarTier0BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER0_ENTITY.get(), pPos, pBlockState);
    }

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private static int foodEssence = 0;

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier0");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory playerInventory, Player pPlayer) {
        return new FoodAltarTier0Menu(pContainerId, playerInventory, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
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

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("foodEssence", foodEssence);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        foodEssence = nbt.getInt("foodEssence");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }


    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier0BlockEntity pBlockEntity) {
        if(pBlockEntity.itemHandler.getStackInSlot(2).isEdible() && foodEssence < 100){
            foodEssence += pBlockEntity.itemHandler.getStackInSlot(2).getFoodProperties(null).getNutrition();
            foodEssence = Math.min(foodEssence, 100);
            pBlockEntity.itemHandler.extractItem(2, 1, false);
        }

        if(hasRecipe(pBlockEntity) && hasNotReachedStackLimit(pBlockEntity) && foodEssence >= 10) {
            craftItem(pBlockEntity);
        }
    }

    private static void craftItem(FoodAltarTier0BlockEntity entity) {
        entity.itemHandler.extractItem(0, 1, false);
        foodEssence -= 10;

        entity.itemHandler.setStackInSlot(1, new ItemStack(ModItems.EXAMPLE_ITEM.get(),
                entity.itemHandler.getStackInSlot(1).getCount() + 1));
    }

    private static boolean hasRecipe(FoodAltarTier0BlockEntity entity) {
        return entity.itemHandler.getStackInSlot(0).getItem() == Items.DIAMOND;
    }

    private static boolean hasNotReachedStackLimit(FoodAltarTier0BlockEntity entity) {
        return entity.itemHandler.getStackInSlot(1).getCount() < entity.itemHandler.getStackInSlot(1).getMaxStackSize();
    }
}

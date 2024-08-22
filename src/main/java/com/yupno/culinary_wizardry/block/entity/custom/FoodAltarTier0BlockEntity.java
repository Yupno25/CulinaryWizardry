package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.item.ModItems;
import com.yupno.culinary_wizardry.recipe.FoodAltarTier0Recipe;
import com.yupno.culinary_wizardry.screen.FoodAltarTier0Menu;
import com.yupno.culinary_wizardry.utils.SimpleFoodContainer;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
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

public class FoodAltarTier0BlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private int foodEssence = 0;
    private int maxFoodEssence = 100;

    public FoodAltarTier0BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER0_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return FoodAltarTier0BlockEntity.this.progress;
                    case 1: return FoodAltarTier0BlockEntity.this.maxProgress;
                    case 2: return FoodAltarTier0BlockEntity.this.foodEssence;
                    case 3: return FoodAltarTier0BlockEntity.this.maxFoodEssence;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: FoodAltarTier0BlockEntity.this.progress = value; break;
                    case 1: FoodAltarTier0BlockEntity.this.maxProgress = value; break;
                    case 2: FoodAltarTier0BlockEntity.this.foodEssence = value; break;
                    case 3: FoodAltarTier0BlockEntity.this.maxFoodEssence = value; break;
                }
            }

            public int getCount() {
                return 4;
            }
        };
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
        tag.putInt("progress", progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        foodEssence = nbt.getInt("foodEssence");
        progress = nbt.getInt("progress");
    }

    public void drops() {
        SimpleFoodContainer inventory = new SimpleFoodContainer(itemHandler.getSlots(), foodEssence);
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier0BlockEntity pBlockEntity) {
        if(hasRecipe(pBlockEntity)) {
            pBlockEntity.progress++;
            setChanged(pLevel, pPos, pState);
            if(pBlockEntity.progress > pBlockEntity.maxProgress) {
            craftItem(pBlockEntity);
            }
        } else {
        pBlockEntity.resetProgress();
        setChanged(pLevel, pPos, pState);
        }

    if(pBlockEntity.itemHandler.getStackInSlot(2).isEdible() && pBlockEntity.foodEssence < pBlockEntity.maxFoodEssence){
        pBlockEntity.foodEssence = Math.min(pBlockEntity.foodEssence + pBlockEntity.itemHandler.getStackInSlot(2).getFoodProperties(null).getNutrition(),
                pBlockEntity.maxFoodEssence);
        pBlockEntity.itemHandler.extractItem(2, 1, false);
        }
    }

    private static boolean hasRecipe(FoodAltarTier0BlockEntity entity) {
        Level level = entity.level;
        SimpleFoodContainer inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), entity.foodEssence);
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<FoodAltarTier0Recipe> match = level.getRecipeManager()
                .getRecipeFor(FoodAltarTier0Recipe.Type.INSTANCE, inventory, level);

        return match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem());
    }

    private static void craftItem(FoodAltarTier0BlockEntity entity) {
        Level level = entity.level;
        SimpleFoodContainer inventory = new SimpleFoodContainer(entity.itemHandler.getSlots(), entity.foodEssence);
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<FoodAltarTier0Recipe> match = level.getRecipeManager()
                .getRecipeFor(FoodAltarTier0Recipe.Type.INSTANCE, inventory, level);

        if(match.isPresent()) {
            entity.itemHandler.extractItem(0,1, false);
            entity.foodEssence -= match.get().getFoodEssenceCost();

            entity.itemHandler.setStackInSlot(1, new ItemStack(match.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(1).getCount() + 1));

            entity.resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleFoodContainer inventory, ItemStack output) {
        return inventory.getItem(1).getItem() == output.getItem() || inventory.getItem(1).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleFoodContainer inventory) {
        return inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
    }

}

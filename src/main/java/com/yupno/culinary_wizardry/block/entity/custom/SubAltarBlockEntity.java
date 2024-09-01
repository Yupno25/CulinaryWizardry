package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.custom.SubAltarBlock;
import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.screen.SubAltarMenu;
import com.yupno.culinary_wizardry.utils.EssenceCalculation;
import com.yupno.culinary_wizardry.utils.FoodType;
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

public class SubAltarBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 0 -> stack.getItem().isEdible();
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final ContainerData data;
    private final FoodType type;
    private final int tier;
    private int foodProgress = 0;
    private int maxFoodProgress = 28;
    private int culinaryEssence = 0;
    private int maxCulinaryEssence;

    public SubAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SUB_ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);

        this.type = ((SubAltarBlock)pBlockState.getBlock()).getType();
        this.tier = ((SubAltarBlock)pBlockState.getBlock()).getTier();
        maxCulinaryEssence = (int)(1000 * Math.pow(10, tier));

        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return SubAltarBlockEntity.this.foodProgress;
                    case 1: return SubAltarBlockEntity.this.maxFoodProgress;
                    case 2: return SubAltarBlockEntity.this.culinaryEssence;
                    case 3: return SubAltarBlockEntity.this.maxCulinaryEssence;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: SubAltarBlockEntity.this.foodProgress = value; break;
                    case 1: SubAltarBlockEntity.this.maxFoodProgress = value; break;
                    case 2: SubAltarBlockEntity.this.culinaryEssence = value; break;
                    case 3: SubAltarBlockEntity.this.maxCulinaryEssence = value; break;
                }
            }

            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        String name = "";
        switch (tier) {
            case 1: name = "low"; break;
            case 2: name = "mid"; break;
            case 3: name = "high"; break;
            case 4: name = "transcendent"; break;
        }

        return new TranslatableComponent("block.culinary_wizardry." + name + "_sub_altar_" + type.getName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SubAltarMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(side == null) {
                return lazyItemHandler.cast();
            }

            return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> false, (index, stack) -> itemHandler.isItemValid(0, stack) && index == 0)).cast();
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
        tag.putInt("pureCulinaryEssence", culinaryEssence);
        tag.putInt("foodProgress", foodProgress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        culinaryEssence = nbt.getInt("pureCulinaryEssence");
        foodProgress = nbt.getInt("foodProgress");
    }

    public void drops() {
        SimpleFoodContainer inventory = new SimpleFoodContainer(itemHandler.getSlots(), culinaryEssence);
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    /**
     * RECIPE STUFF
     * */

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SubAltarBlockEntity entity) {
        if(entity.itemHandler.getStackInSlot(0).isEdible() && entity.culinaryEssence < entity.maxCulinaryEssence){
            entity.foodProgress++;
            setChanged(pLevel, pPos, pState);

            if(entity.foodProgress > entity.maxFoodProgress){
                if(entity.type == FoodType.CULINARY){
                    entity.culinaryEssence = Math.min(entity.culinaryEssence +
                            EssenceCalculation.calculatePureFoodEssence(entity.itemHandler.getStackInSlot(0), entity.tier), entity.maxCulinaryEssence);
                }else {
                    entity.culinaryEssence = Math.min(entity.culinaryEssence +
                            EssenceCalculation.calculateOtherFoodEssence(entity.itemHandler.getStackInSlot(0), entity.tier, entity.type), entity.maxCulinaryEssence);
                }


                entity.itemHandler.extractItem(0, 1, false);
                entity.resetFoodProgress();
            }
        }else {
            entity.resetFoodProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private void resetFoodProgress() {
        this.foodProgress = 0;
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
}

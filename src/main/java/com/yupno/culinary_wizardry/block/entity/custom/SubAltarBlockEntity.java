package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.custom.SubAltarBlock;
import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.screen.SubAltarMenu;
import com.yupno.culinary_wizardry.utils.EssenceCalculation;
import com.yupno.culinary_wizardry.utils.FoodType;
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
    private int eatingProgress = 0;
    private int maxEatingProgress = 28;
    private int culinaryEssence = 0;
    private int maxCulinaryEssence;
    private int bufferCulinaryEssence = 0;
    private int bufferMaxCulinaryEssence;

    public SubAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SUB_ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);

        this.type = ((SubAltarBlock)pBlockState.getBlock()).getType();
        this.tier = ((SubAltarBlock)pBlockState.getBlock()).getTier();
        maxCulinaryEssence = EssenceCalculation.calculateMaxCulinaryEssence(tier);
        bufferMaxCulinaryEssence = maxCulinaryEssence / 20;

        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return SubAltarBlockEntity.this.eatingProgress;
                    case 1: return SubAltarBlockEntity.this.maxEatingProgress;
                    case 2: return SubAltarBlockEntity.this.culinaryEssence;
                    case 3: return SubAltarBlockEntity.this.maxCulinaryEssence;
                    case 4: return SubAltarBlockEntity.this.bufferCulinaryEssence;
                    case 5: return SubAltarBlockEntity.this.bufferMaxCulinaryEssence;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: SubAltarBlockEntity.this.eatingProgress = value; break;
                    case 1: SubAltarBlockEntity.this.maxEatingProgress = value; break;
                    case 2: SubAltarBlockEntity.this.culinaryEssence = value; break;
                    case 3: SubAltarBlockEntity.this.maxCulinaryEssence = value; break;
                    case 4: SubAltarBlockEntity.this.bufferCulinaryEssence = value; break;
                    case 5: SubAltarBlockEntity.this.bufferMaxCulinaryEssence = value; break;
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
        tag.putInt("foodProgress", eatingProgress);
        tag.putInt("bufferCulinaryEssence", bufferCulinaryEssence);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        culinaryEssence = nbt.getInt("culinaryEssence");
        eatingProgress = nbt.getInt("foodProgress");
        bufferCulinaryEssence = nbt.getInt("bufferCulinaryEssence");
    }

    public int getCulinaryEssence(){
        return culinaryEssence;
    }

    public void setCulinaryEssence(int newCulinaryEssence){
        culinaryEssence = newCulinaryEssence;
    }

    /**
     * RECIPE STUFF
     * */

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SubAltarBlockEntity entity) {
        if(entity.bufferCulinaryEssence != 0 && entity.culinaryEssence < entity.maxCulinaryEssence){
            int temp = entity.maxCulinaryEssence - entity.culinaryEssence;
            entity.culinaryEssence = Math.min(entity.culinaryEssence + entity.bufferCulinaryEssence, entity.maxCulinaryEssence);
            entity.bufferCulinaryEssence = Math.max(entity.bufferCulinaryEssence - temp, 0);
        }

        if(entity.itemHandler.getStackInSlot(0).isEdible() && entity.culinaryEssence < entity.maxCulinaryEssence && entity.bufferCulinaryEssence == 0){
            entity.eatingProgress++;
            setChanged(pLevel, pPos, pState);

            if(entity.eatingProgress > entity.maxEatingProgress){
                int temp;
                if(entity.type == FoodType.CULINARY){
                    temp = entity.culinaryEssence + EssenceCalculation.calculatePureFoodEssence(entity.itemHandler.getStackInSlot(0), entity.tier);
                }else {
                    temp = entity.culinaryEssence + EssenceCalculation.calculateOtherFoodEssence(entity.itemHandler.getStackInSlot(0), entity.tier, entity.type);
                }

                if(temp > entity.maxCulinaryEssence)
                    entity.bufferCulinaryEssence = Math.min(temp - entity.maxCulinaryEssence, entity.bufferMaxCulinaryEssence);

                entity.culinaryEssence = Math.min(temp, entity.maxCulinaryEssence);


                entity.itemHandler.extractItem(0, 1, false);
                entity.resetFoodProgress();
            }
        }else {
            entity.resetFoodProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private void resetFoodProgress() {
        this.eatingProgress = 0;
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

            return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> false, (index, stack) -> itemHandler.isItemValid(0, stack) && index == 0)).cast();
        }

        return super.getCapability(cap, side);
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

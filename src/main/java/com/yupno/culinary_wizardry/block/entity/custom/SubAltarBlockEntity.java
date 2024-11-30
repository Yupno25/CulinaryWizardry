package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.custom.SubAltarBlock;
import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.block.entity.custom.base.WrappedHandler;
import com.yupno.culinary_wizardry.utils.EssenceCalculation;
import com.yupno.culinary_wizardry.utils.FoodType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import java.util.Random;

public class SubAltarBlockEntity extends BlockEntity {

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS, 0);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem().isEdible();
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final ContainerData data;
    private final FoodType foodType;
    private final int tier;
    private int eatingProgress = 0;
    private final int maxEatingProgress = 28;
    private int essence = 0;
    private final int maxEssence;
    private int bufferEssence = 0;
    private final int bufferMaxEssence;
    private int tick = 0;

    public SubAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SUB_ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);

        this.foodType = ((SubAltarBlock) pBlockState.getBlock()).getType();
        this.tier = ((SubAltarBlock) pBlockState.getBlock()).getTier();
        maxEssence = EssenceCalculation.calculateMaxEssence(tier);
        bufferMaxEssence = maxEssence / 20;

        this.data = new ContainerData() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> SubAltarBlockEntity.this.eatingProgress;
                    case 1 -> SubAltarBlockEntity.this.maxEatingProgress;
                    case 2 -> SubAltarBlockEntity.this.essence;
                    case 3 -> SubAltarBlockEntity.this.maxEssence;
                    case 4 -> SubAltarBlockEntity.this.bufferEssence;
                    case 5 -> SubAltarBlockEntity.this.bufferMaxEssence;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SubAltarBlockEntity.this.eatingProgress = value;
                    case 2 -> SubAltarBlockEntity.this.essence = value;
                    case 4 -> SubAltarBlockEntity.this.bufferEssence = value;
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
        tag.putInt("essence", essence);
        tag.putInt("foodProgress", eatingProgress);
        tag.putInt("bufferEssence", bufferEssence);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        essence = nbt.getInt("essence");
        eatingProgress = nbt.getInt("foodProgress");
        bufferEssence = nbt.getInt("bufferEssence");
    }

    /**
     * Essence Processing Logic
     */

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SubAltarBlockEntity entity) {
        bufferEssence(entity);
        essenceProcessing(pLevel, pPos, pState, entity);
    }

    private static void bufferEssence(SubAltarBlockEntity entity) {
        if (entity.bufferEssence != 0 && entity.essence < entity.maxEssence) {
            int temp = entity.maxEssence - entity.essence;
            entity.essence = Math.min(entity.essence + entity.bufferEssence, entity.maxEssence);
            entity.bufferEssence = Math.max(entity.bufferEssence - temp, 0);
        }
    }

    private static void essenceProcessing(Level pLevel, BlockPos pPos, BlockState pState, SubAltarBlockEntity entity) {
        if (entity.itemHandler.getStackInSlot(0).isEdible() && entity.essence < entity.maxEssence && entity.bufferEssence == 0) {
            entity.eatingProgress++;
            setChanged(pLevel, pPos, pState);

            if (entity.eatingProgress > entity.maxEatingProgress) {
                int temp = entity.essence + EssenceCalculation.calculateFoodEssence(entity.itemHandler.getStackInSlot(0), entity.tier, entity.getFoodType());

                if (temp > entity.maxEssence)
                    entity.bufferEssence = Math.min(temp - entity.maxEssence, entity.bufferMaxEssence);

                entity.essence = Math.min(temp, entity.maxEssence);


                entity.itemHandler.extractItem(0, 1, false);
                entity.resetFoodProgress();
            }
        } else {
            entity.resetFoodProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private void resetFoodProgress() {
        this.eatingProgress = 0;
    }

    /**
     * Particles
     */
    private static final int TIME_BETWEEN_PARTICLES = 10;

    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, SubAltarBlockEntity pBlockEntity) {
        ItemStack item = pBlockEntity.getItemFromSlot();
        pBlockEntity.tick++;

        if ((pBlockEntity.getEssence() != pBlockEntity.getMaxEssence()) && !item.isEmpty() && pBlockEntity.tick % TIME_BETWEEN_PARTICLES == 0) {
            pBlockEntity.tick = 0;
            Random random = pLevel.random;
            pLevel.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5,
                    random.nextFloat(-0.04F, 0.04F), -0.6, random.nextFloat(-0.04F, 0.04F));
        }
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

            return LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> false, (index, stack) -> itemHandler.isItemValid(0, stack) && index == 0)).cast();
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

    public int getMaxEssence() {
        return maxEssence;
    }

    public int getEssence() {
        return essence;
    }

    public void setEssence(int newEssence) {
        essence = newEssence;
    }

    public int getTier() {
        return tier;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public ItemStackHandler getInventory() {
        return itemHandler;
    }

    public void setStackInSlot(ItemStack stackInSlot) {
        itemHandler.setStackInSlot(0, stackInSlot);
    }

    public ItemStack getItemFromSlot() {
        return itemHandler.getStackInSlot(0);
    }
}

package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.block.entity.custom.base.BaseFoodAltarBE;
import com.yupno.culinary_wizardry.screen.FoodAltarTier2Menu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoodAltarTier2BE extends BaseFoodAltarBE implements MenuProvider {

    public FoodAltarTier2BE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER2_ENTITY.get(), pPos, pBlockState, 2);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
    }

    private void update() {
        checkForMultiblock();
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier2BE entity) {
        entity.internalTicks++;
        if (entity.internalTicks % 20 == 0) {
            entity.internalTicks = 0;
            entity.update();
        }

        craftingLogic(entity, pLevel, pPos, pState);
    }

    public static void clientTick(Level pLevel, BlockPos blockPos, BlockState pState, FoodAltarTier2BE pBlockEntity) {
        craftingAnimationParticles(pLevel, blockPos, pBlockEntity);
    }


    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier2");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory playerInventory, @NotNull Player pPlayer) {
        return new FoodAltarTier2Menu(pContainerId, playerInventory, this, this.data);
    }
}

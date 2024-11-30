package com.yupno.culinary_wizardry.screen;

import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.block.entity.custom.FoodAltarTier3BE;
import com.yupno.culinary_wizardry.screen.base.BaseFoodAltarMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class FoodAltarTier3Menu extends BaseFoodAltarMenu {
    private final FoodAltarTier3BE blockEntity;
    private final Level level;

    public FoodAltarTier3Menu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(7));
    }

    public FoodAltarTier3Menu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.FOOD_ALTAR_TIER3_MENU.get(), pContainerId, 6, inv, entity, data);
        this.level = inv.player.level;
        blockEntity = ((FoodAltarTier3BE) entity);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.FOOD_ALTAR_TIER3.get()) && blockEntity.isFullAltar();
    }
}

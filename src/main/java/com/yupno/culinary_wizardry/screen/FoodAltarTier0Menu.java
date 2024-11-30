package com.yupno.culinary_wizardry.screen;

import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.block.entity.custom.FoodAltarTier0BE;
import com.yupno.culinary_wizardry.screen.base.CoreMenu;
import com.yupno.culinary_wizardry.screen.slot.ModFoodSlot;
import com.yupno.culinary_wizardry.screen.slot.ModResultSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FoodAltarTier0Menu extends CoreMenu {
    private final FoodAltarTier0BE blockEntity;
    private final Level level;
    private final ContainerData data;

    public FoodAltarTier0Menu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(6));
    }

    public FoodAltarTier0Menu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.FOOD_ALTAR_TIER0_MENU.get(), pContainerId, 3);
        checkContainerSize(inv, 3);
        blockEntity = ((FoodAltarTier0BE) entity);
        this.level = inv.player.level;
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 48, 32));
            this.addSlot(new ModResultSlot(handler, 1, 102, 32));
            this.addSlot(new ModFoodSlot(handler, 2, 152, 61));
        });

        addDataSlots(data);
    }

    /**
     * NORMAL CRAFTING ARROW
     */

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 26; // This is the width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    /**
     * CULINARY ESSENCE BAR
     */

    public int getCulinaryEssence() {
        return data.get(4);
    }

    public int getMaxCulinaryEssence() {
        return data.get(5);
    }

    public int getProgressSize() {
        return 50; // This is the height in pixels of the texture
    }

    public int getScaledCulinaryEssence() {
        int culinaryEssence = this.data.get(4);
        int maxCulinaryEssence = this.data.get(5);

        return maxCulinaryEssence != 0 && culinaryEssence != 0 ? culinaryEssence * getProgressSize() / maxCulinaryEssence : 0;
    }

    /**
     * FOOD PROCESSING
     */

    public int getEatingProgress() {
        return this.data.get(2);
    }

    public int getMaxEatingProgress() {
        return this.data.get(3);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.FOOD_ALTAR_TIER0.get());
    }
}

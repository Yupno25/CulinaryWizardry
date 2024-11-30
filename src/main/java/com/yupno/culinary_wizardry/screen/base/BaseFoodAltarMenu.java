package com.yupno.culinary_wizardry.screen.base;

import com.yupno.culinary_wizardry.block.entity.custom.base.CoreFoodAltarBE;
import com.yupno.culinary_wizardry.screen.slot.ModResultSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class BaseFoodAltarMenu extends CoreMenu {
    private final CoreFoodAltarBE blockEntity;
    private final ContainerData data;

    protected BaseFoodAltarMenu(@Nullable MenuType<?> pMenuType, int pContainerId, int slotCount, Inventory inv, BlockEntity entity, ContainerData data) {
        super(pMenuType, pContainerId, slotCount);
        checkContainerSize(inv, 6);
        blockEntity = ((CoreFoodAltarBE) entity);
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 61, 13));
            this.addSlot(new SlotItemHandler(handler, 1, 53, 43));
            this.addSlot(new SlotItemHandler(handler, 2, 80, 62));
            this.addSlot(new SlotItemHandler(handler, 3, 107, 43));
            this.addSlot(new SlotItemHandler(handler, 4, 99, 13));
            this.addSlot(new ModResultSlot(handler, 5, 80, 37));
        });

        addDataSlots(data);
    }

    public int[] getUsedItemSlots() {
        return new int[]{data.get(2), data.get(3), data.get(4), data.get(5), data.get(6)};
    }

    /**
     * CRAFTING ANIMATION
     */

    public int getCraftingProgress() {
        return this.data.get(0);
    }

    public int getMaxCraftingProgress() {
        return this.data.get(1);
    }
}

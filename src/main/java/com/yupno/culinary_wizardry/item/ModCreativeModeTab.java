package com.yupno.culinary_wizardry.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab CULINARY_WIZARDRY_TAB = new CreativeModeTab("culinary_wizardry_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.EXAMPLE_ITEM.get());
        }
    };
}

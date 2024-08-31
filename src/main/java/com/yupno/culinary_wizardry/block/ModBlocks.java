package com.yupno.culinary_wizardry.block;

import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.custom.FoodAltarTier0Block;
import com.yupno.culinary_wizardry.item.ModCreativeModeTab;
import com.yupno.culinary_wizardry.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CulinaryWizardry.MOD_ID);

    /**
     * BLOCKS
     */

    public static final RegistryObject<Block> EXAMPLE_BLOCK = registerBlock("example_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(9f).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> FOOD_ALTAR_TIER0 = registerBlock("food_altar_tier0",
            () -> new FoodAltarTier0Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.5F).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> FOOD_ALTAR_TIER1 = registerBlock("food_altar_tier1",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.5F).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> LOW_SUB_ALTAR_CULINARY = registerBlock("low_sub_altar_culinary",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> LOW_SUB_ALTAR_FRUITS = registerBlock("low_sub_altar_fruits",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> LOW_SUB_ALTAR_GRAINS = registerBlock("low_sub_altar_grains",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> LOW_SUB_ALTAR_PROTEINS = registerBlock("low_sub_altar_proteins",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> LOW_SUB_ALTAR_SUGARS = registerBlock("low_sub_altar_sugars",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> LOW_SUB_ALTAR_VEGETABLES = registerBlock("low_sub_altar_vegetables",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);




    /**
     * OTHER STUFF
     */

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block,
                                                                            CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

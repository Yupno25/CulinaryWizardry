package com.yupno.culinary_wizardry.block;

import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.custom.*;
import com.yupno.culinary_wizardry.item.ModCreativeModeTab;
import com.yupno.culinary_wizardry.item.ModItems;
import com.yupno.culinary_wizardry.utils.FoodType;
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
                    .strength(2.5F).requiresCorrectToolForDrops().noOcclusion()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> FOOD_ALTAR_TIER1 = registerBlock("food_altar_tier1",
            () -> new FoodAltarTier1Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.5F).requiresCorrectToolForDrops().noOcclusion()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> FOOD_ALTAR_TIER2 = registerBlock("food_altar_tier2",
            () -> new FoodAltarTier2Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.5F).requiresCorrectToolForDrops().noOcclusion()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> FOOD_ALTAR_TIER3 = registerBlock("food_altar_tier3",
            () -> new FoodAltarTier3Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.5F).requiresCorrectToolForDrops().noOcclusion()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> FOOD_ALTAR_TIER4 = registerBlock("food_altar_tier4",
            () -> new FoodAltarTier4Block(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2.5F).requiresCorrectToolForDrops().noOcclusion()), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> LOW_SUB_ALTAR_CULINARY = registerBlock("low_sub_altar_culinary",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.CULINARY, 1), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> MID_SUB_ALTAR_CULINARY = registerBlock("mid_sub_altar_culinary",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.CULINARY, 2), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> MID_SUB_ALTAR_FRUITS = registerBlock("mid_sub_altar_fruits",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.FRUITS, 2), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> MID_SUB_ALTAR_GRAINS = registerBlock("mid_sub_altar_grains",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.GRAINS, 2), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> MID_SUB_ALTAR_PROTEINS = registerBlock("mid_sub_altar_proteins",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.PROTEINS, 2), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> MID_SUB_ALTAR_SUGARS = registerBlock("mid_sub_altar_sugars",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.SUGARS, 2), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> MID_SUB_ALTAR_VEGETABLES = registerBlock("mid_sub_altar_vegetables",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.VEGETABLES, 2), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> HIGH_SUB_ALTAR_CULINARY = registerBlock("high_sub_altar_culinary",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.CULINARY, 3), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> HIGH_SUB_ALTAR_FRUITS = registerBlock("high_sub_altar_fruits",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.FRUITS, 3), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> HIGH_SUB_ALTAR_GRAINS = registerBlock("high_sub_altar_grains",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.GRAINS, 3), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> HIGH_SUB_ALTAR_PROTEINS = registerBlock("high_sub_altar_proteins",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.PROTEINS, 3), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> HIGH_SUB_ALTAR_SUGARS = registerBlock("high_sub_altar_sugars",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.SUGARS, 3), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> HIGH_SUB_ALTAR_VEGETABLES = registerBlock("high_sub_altar_vegetables",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.VEGETABLES, 3), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> TRANSCENDENT_SUB_ALTAR_CULINARY = registerBlock("transcendent_sub_altar_culinary",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.CULINARY, 4), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> TRANSCENDENT_SUB_ALTAR_FRUITS = registerBlock("transcendent_sub_altar_fruits",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.FRUITS, 4), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> TRANSCENDENT_SUB_ALTAR_GRAINS = registerBlock("transcendent_sub_altar_grains",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.GRAINS, 4), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> TRANSCENDENT_SUB_ALTAR_PROTEINS = registerBlock("transcendent_sub_altar_proteins",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.PROTEINS, 4), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> TRANSCENDENT_SUB_ALTAR_SUGARS = registerBlock("transcendent_sub_altar_sugars",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.SUGARS, 4), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);

    public static final RegistryObject<Block> TRANSCENDENT_SUB_ALTAR_VEGETABLES = registerBlock("transcendent_sub_altar_vegetables",
            () -> new SubAltarBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .strength(2f).requiresCorrectToolForDrops().noOcclusion(), FoodType.VEGETABLES, 4), ModCreativeModeTab.CULINARY_WIZARDRY_TAB);




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

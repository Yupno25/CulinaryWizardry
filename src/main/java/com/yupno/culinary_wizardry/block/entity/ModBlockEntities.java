package com.yupno.culinary_wizardry.block.entity;

import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.block.entity.custom.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CulinaryWizardry.MOD_ID);


    public static final RegistryObject<BlockEntityType<FoodAltarTier0BlockEntity>> FOOD_ALTAR_TIER0_ENTITY =
            BLOCK_ENTITIES.register("food_altar_tier0_block_entity", () ->
                    BlockEntityType.Builder.of(FoodAltarTier0BlockEntity::new,
                    ModBlocks.FOOD_ALTAR_TIER0.get()).build(null));

    public static final RegistryObject<BlockEntityType<FoodAltarTier1BlockEntity>> FOOD_ALTAR_TIER1_ENTITY =
            BLOCK_ENTITIES.register("food_altar_tier1_block_entity", () ->
                    BlockEntityType.Builder.of(FoodAltarTier1BlockEntity::new,
                            ModBlocks.FOOD_ALTAR_TIER1.get()).build(null));

    public static final RegistryObject<BlockEntityType<FoodAltarTier2BlockEntity>> FOOD_ALTAR_TIER2_ENTITY =
            BLOCK_ENTITIES.register("food_altar_tier2_block_entity", () ->
                    BlockEntityType.Builder.of(FoodAltarTier2BlockEntity::new,
                            ModBlocks.FOOD_ALTAR_TIER2.get()).build(null));

    public static final RegistryObject<BlockEntityType<FoodAltarTier3BlockEntity>> FOOD_ALTAR_TIER3_ENTITY =
            BLOCK_ENTITIES.register("food_altar_tier3_block_entity", () ->
                    BlockEntityType.Builder.of(FoodAltarTier3BlockEntity::new,
                            ModBlocks.FOOD_ALTAR_TIER3.get()).build(null));

    public static final RegistryObject<BlockEntityType<FoodAltarTier4BlockEntity>> FOOD_ALTAR_TIER4_ENTITY =
            BLOCK_ENTITIES.register("food_altar_tier4_block_entity", () ->
                    BlockEntityType.Builder.of(FoodAltarTier4BlockEntity::new,
                            ModBlocks.FOOD_ALTAR_TIER4.get()).build(null));

    public static final RegistryObject<BlockEntityType<SubAltarBlockEntity>> SUB_ALTAR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("sub_altar_block_entity", () ->
                    BlockEntityType.Builder.of(SubAltarBlockEntity::new,
                            ModBlocks.LOW_SUB_ALTAR_CULINARY.get(), ModBlocks.MID_SUB_ALTAR_CULINARY.get(), ModBlocks.MID_SUB_ALTAR_FRUITS.get(),
                            ModBlocks.MID_SUB_ALTAR_GRAINS.get(), ModBlocks.MID_SUB_ALTAR_PROTEINS.get(), ModBlocks.MID_SUB_ALTAR_SUGARS.get(),
                            ModBlocks.MID_SUB_ALTAR_VEGETABLES.get(), ModBlocks.HIGH_SUB_ALTAR_CULINARY.get(), ModBlocks.HIGH_SUB_ALTAR_FRUITS.get(),
                            ModBlocks.HIGH_SUB_ALTAR_GRAINS.get(), ModBlocks.HIGH_SUB_ALTAR_PROTEINS.get(), ModBlocks.HIGH_SUB_ALTAR_SUGARS.get(),
                            ModBlocks.HIGH_SUB_ALTAR_VEGETABLES.get(), ModBlocks.TRANSCENDENT_SUB_ALTAR_CULINARY.get(), ModBlocks.TRANSCENDENT_SUB_ALTAR_FRUITS.get(),
                            ModBlocks.TRANSCENDENT_SUB_ALTAR_GRAINS.get(), ModBlocks.TRANSCENDENT_SUB_ALTAR_PROTEINS.get(), ModBlocks.TRANSCENDENT_SUB_ALTAR_SUGARS.get(),
                            ModBlocks.TRANSCENDENT_SUB_ALTAR_VEGETABLES.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}

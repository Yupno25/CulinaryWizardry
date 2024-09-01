package com.yupno.culinary_wizardry.block.entity;

import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.block.entity.custom.FoodAltarTier0BlockEntity;
import com.yupno.culinary_wizardry.block.entity.custom.SubAltarBlockEntity;
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

    public static final RegistryObject<BlockEntityType<SubAltarBlockEntity>> SUB_ALTAR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("sub_altar_block_entity", () ->
                    BlockEntityType.Builder.of(SubAltarBlockEntity::new,
                            ModBlocks.LOW_SUB_ALTAR_CULINARY.get(), ModBlocks.MID_SUB_ALTAR_CULINARY.get(), ModBlocks.MID_SUB_ALTAR_FRUITS.get(),
                            ModBlocks.MID_SUB_ALTAR_GRAINS.get(), ModBlocks.MID_SUB_ALTAR_PROTEINS.get(), ModBlocks.MID_SUB_ALTAR_SUGARS.get(),
                            ModBlocks.MID_SUB_ALTAR_VEGETABLES.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}

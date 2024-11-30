package com.yupno.culinary_wizardry.client.events;

import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.block.entity.render.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CulinaryWizardry.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupEvents {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.SUB_ALTAR_BLOCK_ENTITY.get(), SubAltarRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.FOOD_ALTAR_TIER0_ENTITY.get(), FoodAltarTier0Renderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FOOD_ALTAR_TIER1_ENTITY.get(), FoodAltarTier1Renderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FOOD_ALTAR_TIER2_ENTITY.get(), FoodAltarTier2Renderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FOOD_ALTAR_TIER3_ENTITY.get(), FoodAltarTier3Renderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FOOD_ALTAR_TIER4_ENTITY.get(), FoodAltarTier4Renderer::new);
    }
}

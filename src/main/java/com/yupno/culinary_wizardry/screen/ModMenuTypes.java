package com.yupno.culinary_wizardry.screen;

import com.yupno.culinary_wizardry.CulinaryWizardry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, CulinaryWizardry.MOD_ID);


    public static final RegistryObject<MenuType<FoodAltarTier0Menu>> FOOD_ALTAR_TIER0_MENU =
            registerMenuType(FoodAltarTier0Menu::new, "food_altar_tier0_menu");

    public static final RegistryObject<MenuType<FoodAltarTier1Menu>> FOOD_ALTAR_TIER1_MENU =
            registerMenuType(FoodAltarTier1Menu::new, "food_altar_tier1_menu");

    public static final RegistryObject<MenuType<SubAltarMenu>> SUB_ALTAR_MENU =
            registerMenuType(SubAltarMenu::new, "sub_altar_menu");



    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name){
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus){
        MENUS.register(eventBus);
    }
}

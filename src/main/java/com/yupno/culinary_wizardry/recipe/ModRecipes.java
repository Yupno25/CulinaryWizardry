package com.yupno.culinary_wizardry.recipe;

import com.yupno.culinary_wizardry.CulinaryWizardry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CulinaryWizardry.MOD_ID);

    /**
     * RECIPES
     */

    public static final RegistryObject<RecipeSerializer<FoodAltarRecipe>> FOOD_ALTAR_RECIPES =
            SERIALIZERS.register("food_altar_recipes", () -> FoodAltarRecipe.Serializer.INSTANCE);


    /**
     * Registering Logic
     */
    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}

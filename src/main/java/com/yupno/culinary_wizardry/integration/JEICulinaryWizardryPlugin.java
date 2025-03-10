package com.yupno.culinary_wizardry.integration;

import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.recipe.SimpleSubAltarRecipe;
import com.yupno.culinary_wizardry.utils.EssenceCalculation;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEICulinaryWizardryPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(CulinaryWizardry.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FoodAltarRecipeCategoryTier0(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FoodAltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SubAltarCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        // Food Altar recipes
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<FoodAltarRecipe> recipes = recipeManager.getAllRecipesFor(FoodAltarRecipe.Type.INSTANCE);
        List<List<FoodAltarRecipe>> recipesByTier = getRecipesByTier(recipes);

        registration.addRecipes(
            new RecipeType<>(FoodAltarRecipeCategoryTier0.UID, FoodAltarRecipe.class),
            recipesByTier.get(0)
        );
        registration.addRecipes(
            new RecipeType<>(FoodAltarRecipeCategory.UID, FoodAltarRecipe.class),
            recipes
        );

        // Sub Altar recipes
        List<SimpleSubAltarRecipe> subAltarRecipes = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ItemStack stack = new ItemStack(item);
            FoodProperties foodProperties = item.getFoodProperties(stack, null);

            if (foodProperties != null) {
                if (foodProperties.getNutrition() > 0) {
                    subAltarRecipes.add(new SimpleSubAltarRecipe(stack));
                }
            }
        }
        registration.addRecipes(
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class),
            subAltarRecipes
        );
    }

    @NotNull
    private List<List<FoodAltarRecipe>> getRecipesByTier(List<FoodAltarRecipe> recipes) {
        List<List<FoodAltarRecipe>> recipesByTier = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            recipesByTier.add(new ArrayList<>());
        }

        for (FoodAltarRecipe recipe : recipes) {
            for (int i = 4; i >= recipe.getTier(); i--) {
                if (i != 0 || isValidTier0Recipe(recipe)) {
                    recipesByTier.get(i).add(recipe);
                }
            }
        }
        return recipesByTier;
    }

    private boolean isValidTier0Recipe(FoodAltarRecipe recipe) {
        return recipe.getIngredients().size() == 1 &&
            recipe.getCulinaryEssenceCost() <= EssenceCalculation.calculateMaxEssence(0) &&
            recipe.getFruitsEssenceCost() == 0 &&
            recipe.getGrainsEssenceCost() == 0 &&
            recipe.getProteinsEssenceCost() == 0 &&
            recipe.getSugarsEssenceCost() == 0 &&
            recipe.getVegetablesEssenceCost() == 0;
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        // Food Altars
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.FOOD_ALTAR_TIER0.get()),
            new RecipeType<>(FoodAltarRecipeCategoryTier0.UID, FoodAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.FOOD_ALTAR_TIER1.get()),
            new RecipeType<>(FoodAltarRecipeCategory.UID, FoodAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.FOOD_ALTAR_TIER2.get()),
            new RecipeType<>(FoodAltarRecipeCategory.UID, FoodAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.FOOD_ALTAR_TIER3.get()),
            new RecipeType<>(FoodAltarRecipeCategory.UID, FoodAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.FOOD_ALTAR_TIER4.get()),
            new RecipeType<>(FoodAltarRecipeCategory.UID, FoodAltarRecipe.class)
        );

        // Sub Altars
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.LOW_SUB_ALTAR_CULINARY.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.MID_SUB_ALTAR_CULINARY.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.MID_SUB_ALTAR_FRUITS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.MID_SUB_ALTAR_GRAINS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.MID_SUB_ALTAR_PROTEINS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.MID_SUB_ALTAR_SUGARS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.MID_SUB_ALTAR_VEGETABLES.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.HIGH_SUB_ALTAR_CULINARY.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.HIGH_SUB_ALTAR_FRUITS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.HIGH_SUB_ALTAR_GRAINS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.HIGH_SUB_ALTAR_PROTEINS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.HIGH_SUB_ALTAR_SUGARS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.HIGH_SUB_ALTAR_VEGETABLES.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.TRANSCENDENT_SUB_ALTAR_CULINARY.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.TRANSCENDENT_SUB_ALTAR_FRUITS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.TRANSCENDENT_SUB_ALTAR_GRAINS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.TRANSCENDENT_SUB_ALTAR_PROTEINS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.TRANSCENDENT_SUB_ALTAR_SUGARS.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.TRANSCENDENT_SUB_ALTAR_VEGETABLES.get()),
            new RecipeType<>(SubAltarCategory.UID, SimpleSubAltarRecipe.class)
        );
    }
}

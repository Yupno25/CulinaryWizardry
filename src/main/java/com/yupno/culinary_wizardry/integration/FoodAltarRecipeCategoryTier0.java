package com.yupno.culinary_wizardry.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;

public class FoodAltarRecipeCategoryTier0 implements IRecipeCategory<FoodAltarRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(CulinaryWizardry.MOD_ID, "food_altar_tier_0");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(CulinaryWizardry.MOD_ID, "textures/gui/jei/food_altar_tier0_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public FoodAltarRecipeCategoryTier0(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 86);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FOOD_ALTAR_TIER0.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends FoodAltarRecipe> getRecipeClass() {
        return FoodAltarRecipe.class;
    }

    @Override
    public @NotNull Component getTitle() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }
    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull FoodAltarRecipe recipe, @Nonnull IFocusGroup focusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 32 + 3).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 102, 32 + 3).addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(
        @NotNull FoodAltarRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull PoseStack stack, double mouseX, double mouseY
    ) {
        Minecraft mc = Minecraft.getInstance();

        mc.font.draw(stack, "Tier: " + recipe.getTier(), 75, 5, Color.BLACK.getRGB());
        mc.font.draw(stack, "Culinary: " + recipe.getCulinaryEssenceCost(), 0, 20, Color.BLACK.getRGB());
        mc.font.draw(stack, "Fruits: " + recipe.getFruitsEssenceCost(), 0, 55, Color.BLACK.getRGB());
        mc.font.draw(stack, "Grains: " + recipe.getGrainsEssenceCost(), 0, 70, Color.BLACK.getRGB());
        mc.font.draw(stack, "Proteins: " + recipe.getProteinsEssenceCost(), 100, 20, Color.BLACK.getRGB());
        mc.font.draw(stack, "Sugars: " + recipe.getSugarsEssenceCost(), 100, 55, Color.BLACK.getRGB());
        mc.font.draw(stack, "Vegetables: " + recipe.getVegetablesEssenceCost(), 100, 70, Color.BLACK.getRGB());
    }
}

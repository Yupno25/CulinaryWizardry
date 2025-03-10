package com.yupno.culinary_wizardry.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.item.ModItems;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.recipe.SimpleSubAltarRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;

public class SubAltarCategory implements IRecipeCategory<SimpleSubAltarRecipe> {

    public final static ResourceLocation UID = new ResourceLocation(CulinaryWizardry.MOD_ID, "sub_altar");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(CulinaryWizardry.MOD_ID, "textures/gui/jei/sub_altar_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public SubAltarCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 86);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.LOW_SUB_ALTAR_CULINARY.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends SimpleSubAltarRecipe> getRecipeClass() {
        return SimpleSubAltarRecipe.class;
    }

    @Override
    public @NotNull Component getTitle() {
        return new TranslatableComponent("block.culinary_wizardry.sub_altar");
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
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull SimpleSubAltarRecipe recipe, @Nonnull IFocusGroup focusGroup) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 81, 17).addIngredients(Ingredient.of(recipe.getStack().getItem()));
    }

    @Override
    public void draw(
        @NotNull SimpleSubAltarRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull PoseStack stack, double mouseX, double mouseY
    ) {
        Minecraft mc = Minecraft.getInstance();

        mc.font.draw(stack, "Culinary: " + recipe.getCulinaryEssence(), 0, 40, Color.BLACK.getRGB());
        mc.font.draw(stack, "Fruits: " + recipe.getFruitsEssence(), 0, 55, Color.BLACK.getRGB());
        mc.font.draw(stack, "Grains: " + recipe.getGrainsEssence(), 0, 70, Color.BLACK.getRGB());
        mc.font.draw(stack, "Proteins: " + recipe.getProteinsEssence(), 100, 40, Color.BLACK.getRGB());
        mc.font.draw(stack, "Sugars: " + recipe.getSugarsEssence(), 100, 55, Color.BLACK.getRGB());
        mc.font.draw(stack, "Vegetables: " + recipe.getVegetablesEssence(), 100, 70, Color.BLACK.getRGB());
    }
}

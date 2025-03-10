package com.yupno.culinary_wizardry.recipe;

import com.yupno.culinary_wizardry.utils.EssenceCalculation;
import com.yupno.culinary_wizardry.utils.FoodType;
import net.minecraft.world.item.ItemStack;

public class SimpleSubAltarRecipe {
    private final int culinaryEssence;
    private final int fruitsEssence;
    private final int grainsEssence;
    private final int proteinsEssence;
    private final int sugarsEssence;
    private final int vegetablesEssence;
    private final ItemStack stack;

    public SimpleSubAltarRecipe(ItemStack stack)
    {
        this.stack = stack;
        this.culinaryEssence = EssenceCalculation.calculateFoodEssence(stack, 2, FoodType.CULINARY);
        this.fruitsEssence = EssenceCalculation.calculateFoodEssence(stack, 2, FoodType.FRUITS);
        this.grainsEssence = EssenceCalculation.calculateFoodEssence(stack, 2, FoodType.GRAINS);
        this.proteinsEssence = EssenceCalculation.calculateFoodEssence(stack, 2, FoodType.PROTEINS);
        this.sugarsEssence = EssenceCalculation.calculateFoodEssence(stack, 2, FoodType.SUGARS);
        this.vegetablesEssence = EssenceCalculation.calculateFoodEssence(stack, 2, FoodType.VEGETABLES);
    }

    /**
     * Getter Methods
     */

    public int getCulinaryEssence() {
        return culinaryEssence;
    }

    public int getFruitsEssence() {
        return fruitsEssence;
    }

    public int getGrainsEssence() {
        return grainsEssence;
    }

    public int getProteinsEssence() {
        return proteinsEssence;
    }

    public int getSugarsEssence() {
        return sugarsEssence;
    }

    public int getVegetablesEssence() {
        return vegetablesEssence;
    }

    public ItemStack getStack() {
        return stack;
    }
}

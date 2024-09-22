package com.yupno.culinary_wizardry.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.IDietGroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EssenceCalculation {
    public static int calculateFoodEssence(ItemStack food, int tier, FoodType foodType) {
        FoodProperties properties = food.getFoodProperties(null);

        float tempPureFoodEssence = (properties.getNutrition() + properties.getNutrition() * properties.getSaturationModifier() * 1.6f) / 2;
        float totalPercentage = 0;

        /** Bonus for positive effects / Loss for negative effects */
        tempPureFoodEssence = calculateEffects(properties, tempPureFoodEssence);

        /** Adjustments for different dietary groups (and more nutrients) */
        Set<IDietGroup> groups = DietApi.getInstance().getGroups(null, food);
        Map<IDietGroup, Float> map = DietApi.getInstance().get(null, food).get();

        if (!groups.isEmpty()) {
            int differentNutrients = 1;

            if (foodType == FoodType.CULINARY) {
                /** Bonus */
                for (IDietGroup group : groups) {
                    totalPercentage += map.get(group) * differentNutrients;
                    differentNutrients++;
                }
            } else {
                /** Penalty */
                for (IDietGroup group : groups) {
                    if (group.getName().equals(foodType.getName())) {
                        totalPercentage += map.get(group) * 5;
                    }

                    totalPercentage -= map.get(group) * differentNutrients;
                    differentNutrients++;
                }
            }


            totalPercentage *= 100;
            if (totalPercentage < 0)
                totalPercentage = 0;
        }

        /** Bonus for bigger numbers */
        tempPureFoodEssence = (float) Math.pow((tempPureFoodEssence * totalPercentage), 1.2) / 2;

        /** Final calculations, takes level of food altar into consideration */
        tempPureFoodEssence *= ((tier * 0.25f) + 0.5f);

        return Math.max(Math.round(tempPureFoodEssence), 1);
    }

    private static float calculateEffects(FoodProperties properties, float foodEssence) {
        List<Pair<MobEffectInstance, Float>> list = properties.getEffects();

        if (!list.isEmpty()) {
            for (Pair<MobEffectInstance, Float> floatPair : list) {
                float effectAmplification = (Math.min((floatPair.getFirst().getDuration() / 15), 12) * floatPair.getFirst().getAmplifier() + 2);

                if (floatPair.getFirst().getEffect().isBeneficial()) {
                    foodEssence += effectAmplification;
                } else {
                    foodEssence -= effectAmplification;
                }
            }
        }

        return foodEssence;
    }

    public static int calculateMaxEssence(int tier) {
        return (int) (1000 * Math.pow(10, tier));
    }
}

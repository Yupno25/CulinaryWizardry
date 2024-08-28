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

public class CulinaryEssencesCalculation {
    public static int calculatePureFoodEssence(ItemStack food, int level) {
        FoodProperties properties = food.getFoodProperties(null);

        float tempPureFoodEssence = (properties.getNutrition() + properties.getNutrition() * properties.getSaturationModifier() * 1.6f) / 2;
        float totalPercentage = 0;

        /** Bonus for positive effects / Loss for negative effects */
        List<Pair<MobEffectInstance, Float>> list = properties.getEffects();

        if (!list.isEmpty()) {
            for (Pair<MobEffectInstance, Float> floatPair : list) {
                float effectAmplification = (Math.min((floatPair.getFirst().getDuration() / 15), 12) * floatPair.getFirst().getAmplifier() + 2);

                if (floatPair.getFirst().getEffect().isBeneficial()) {
                    tempPureFoodEssence += effectAmplification;
                } else {
                    tempPureFoodEssence -= effectAmplification;
                }
            }
        }

        /** Bonus for more different dietary groups (and more nutrients) */
        Set<IDietGroup> groups = DietApi.getInstance().getGroups(null, food);
        Map<IDietGroup, Float> map = DietApi.getInstance().get(null, food).get();

        if (!groups.isEmpty()) {
            int differentNutrients = 1;

            for (IDietGroup group : groups) {
                totalPercentage += map.get(group) * differentNutrients;
                differentNutrients++;
            }

            totalPercentage *= 100;
        }

        /** Bonus for bigger numbers */
        tempPureFoodEssence = (float) Math.pow((tempPureFoodEssence * totalPercentage), 1.1) / 2;

        /** Final calculations, takes level of food altar into consideration */
        tempPureFoodEssence *= ((level * 0.25f) + 0.5f);


        //LogUtils.getLogger().debug("Result: " + tempPureFoodEssence);

        return Math.max(Math.round(tempPureFoodEssence), 1);
    }
}

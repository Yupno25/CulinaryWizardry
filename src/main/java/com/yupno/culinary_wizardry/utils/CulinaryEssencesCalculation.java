package com.yupno.culinary_wizardry.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.IDietGroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CulinaryEssencesCalculation {
    public static int calculatePureFoodEssence(ItemStack food, int level){
        FoodProperties properties = food.getFoodProperties(null);

        float tempPureFoodEssence = (properties.getNutrition() + properties.getNutrition() * properties.getSaturationModifier() * 1.6f) / 2;
        float totalPercentage = 0;

        /** Bonus for positive effects / Loss for negative effects */
        List<Pair<MobEffectInstance, Float>> list = properties.getEffects();

        if(!list.isEmpty()){
            for (Pair<MobEffectInstance, Float> floatPair : list) {
                float effectAmplification = (Math.min((floatPair.getFirst().getDuration()/15), 12) * floatPair.getFirst().getAmplifier() + 2);

                if(floatPair.getFirst().getEffect().isBeneficial()){
                    tempPureFoodEssence += effectAmplification;
                }else {
                    tempPureFoodEssence -= effectAmplification;
                }
            }
        }

        /** Bonus for more different dietary groups (and more nutrients) */
        Set<IDietGroup> groups = DietApi.getInstance().getGroups(null, food);
        Map<IDietGroup, Float> map = DietApi.getInstance().get(null, food).get();

        if(!groups.isEmpty()){
            float differentNutrients = 1;

            for (IDietGroup group : groups) {
                totalPercentage += map.get(group) * differentNutrients;
                differentNutrients += 1f;
            }

            totalPercentage *= 100;
        }

        /** Final calculations, bonus for bigger numbers */
        tempPureFoodEssence = (float) Math.pow((tempPureFoodEssence * totalPercentage), 1.1);

        switch (level){
            case 0:{tempPureFoodEssence *= 0.5f; break;}
            case 1:{tempPureFoodEssence *= 0.75f; break;}
            case 2:{break;}
            case 3:{tempPureFoodEssence *= 1.25f; break;}
            case 4:{tempPureFoodEssence *= 1.5f; break;}
        }

        LogUtils.getLogger().debug("Result: " + tempPureFoodEssence);

        return Math.max(Math.round(tempPureFoodEssence), 1);
    }

/*
golden_carrot: 10.2, 11.5            = 24
cooked_beef: 9.6, 12.0               = 20
cooked_porkchop: 9.6, 12.0           = 20
cooked_mutton: 8.3, 10.5             = 18
cooked_salmon: 8.3, 10.5             = 18
enchanted_golden_apple: 9.2, 9.0     = 50
golden_apple: 9.2, 9.0               = 25
cooked_chicken: 6.6, 10.0            = 14
baked_potato: 5.5, 8.5               = 12
bread: 5.5, 8.5                      = 12
cooked_cod: 5.5, 8.5                 = 12
cooked_rabbit: 5.5, 8.5              = 12
pumpkin_pie: 4.4, 3.5, 3.5, 3.5      = 20
carrot: 3.6, 6.0                     = 10
spider_eye: 4.6, 5.0                 = 0
apple: 2.2, 6.5                      = 9
chorus_fruit: 2.2, 6.5               = 9
beef: 1.8, 5.0                       = 7
porkchop: 1.8, 5.0                   = 7
rabbit: 1.8, 5.0                     = 7
rotten_flesh: 2.4, 5.5               = 0
melon_slice: 1.3, 3.5                = 4
mutton: 1.6, 3.5                     = 5
poisonous_potato: 1.6, 3.5           = 0
chicken: 1.6, 3.5                    = 0
honey_bottle: 3.6, 7.5               = 5
beetroot: 0.8, 2.5                   = 4-5
dried_kelp: 0.8, 2.0                 = 3
potato: 0.8, 2.0                     = 3
salmon: 1.2, 3.0                     = 2
cod: 1.2, 3.0                        = 2
sweet_berries: 1.2, 3.0              = 2
glow_berries: 1.2, 3.0               = 2
cookie: 1.2, 1.5, 1.5                = 2-3
pufferfish: 0.6, 1.5                 = 1
tropical_fish: 0.6, 1.5              = 1
*/
}

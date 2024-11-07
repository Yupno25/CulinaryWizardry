package com.yupno.culinary_wizardry.utils;

import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class CulinaryUtils {
    public static Color getLiquidEssenceColor(FoodType foodType){
        switch (foodType){
            case FRUITS: return new Color(196, 26, 77);
            case GRAINS: return new Color(196, 151, 26);
            case SUGARS: return new Color(236, 132, 216);
            case PROTEINS: return new Color(196, 111, 26);
            case VEGETABLES: return new Color(11, 139, 0);
            default: return new Color(184, 132, 88); // CULINARY
        }
    }
}

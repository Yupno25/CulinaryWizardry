package com.yupno.culinary_wizardry.utils;

import java.awt.*;

public class CulinaryUtils {
    public static Color getLiquidEssenceColor(FoodType foodType) {
        return switch (foodType) {
            case CULINARY -> new Color(184, 132, 88);
            case FRUITS -> new Color(196, 26, 77);
            case GRAINS -> new Color(196, 151, 26);
            case SUGARS -> new Color(236, 132, 216);
            case PROTEINS -> new Color(196, 111, 26);
            case VEGETABLES -> new Color(11, 139, 0);
        };
    }
}

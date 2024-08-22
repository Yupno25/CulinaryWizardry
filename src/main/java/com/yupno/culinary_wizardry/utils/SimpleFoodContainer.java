package com.yupno.culinary_wizardry.utils;

import net.minecraft.world.SimpleContainer;

public class SimpleFoodContainer extends SimpleContainer {
    private final int foodEssence;
    public SimpleFoodContainer(int slots, int foodEssence) {
        super(slots);
        this.foodEssence = foodEssence;
    }

    public int getFoodEssence(){
        return foodEssence;
    }
}

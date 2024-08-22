package com.yupno.culinary_wizardry.utils;

import net.minecraft.world.SimpleContainer;

public class SimpleFoodContainer extends SimpleContainer {
    private final int pureCulinaryEssence;
    public SimpleFoodContainer(int slots, int pureCulinaryEssence) {
        super(slots);
        this.pureCulinaryEssence = pureCulinaryEssence;
    }

    public int getPureCulinaryEssence(){
        return pureCulinaryEssence;
    }
}

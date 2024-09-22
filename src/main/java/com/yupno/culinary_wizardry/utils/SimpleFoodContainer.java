package com.yupno.culinary_wizardry.utils;

import net.minecraft.world.SimpleContainer;

public class SimpleFoodContainer extends SimpleContainer {
    private final int culinaryEssence;
    private final int fruitsEssence;
    private final int grainsEssence;
    private final int proteinsEssence;
    private final int sugarsEssence;
    private final int vegetablesEssence;
    private final int tier;

    public SimpleFoodContainer(int slots, int culinaryEssence, int fruitsEssence, int grainsEssence, int proteinsEssence, int sugarsEssence, int vegetablesEssence, int tier) {
        super(slots);
        this.culinaryEssence = culinaryEssence;
        this.fruitsEssence = fruitsEssence;
        this.grainsEssence = grainsEssence;
        this.proteinsEssence = proteinsEssence;
        this.sugarsEssence = sugarsEssence;
        this.vegetablesEssence = vegetablesEssence;
        this.tier = tier;
    }

    public int getCulinaryEssence(){
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

    public int getTier() {
        return tier;
    }
}

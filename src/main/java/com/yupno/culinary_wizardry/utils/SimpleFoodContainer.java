package com.yupno.culinary_wizardry.utils;

import net.minecraft.world.SimpleContainer;

public class SimpleFoodContainer extends SimpleContainer {
    private final int pureCulinaryEssence;
    private final int pureFruitsEssence;
    private final int pureGrainsEssence;
    private final int pureProteinsEssence;
    private final int pureSugarsEssence;
    private final int pureVegetablesEssence;
    private final int tier;

    public SimpleFoodContainer(int slots, int pureCulinaryEssence, int pureFruitsEssence, int pureGrainsEssence, int pureProteinsEssence, int pureSugarsEssence, int pureVegetablesEssence, int tier) {
        super(slots);
        this.pureCulinaryEssence = pureCulinaryEssence;
        this.pureFruitsEssence = pureFruitsEssence;
        this.pureGrainsEssence = pureGrainsEssence;
        this.pureProteinsEssence = pureProteinsEssence;
        this.pureSugarsEssence = pureSugarsEssence;
        this.pureVegetablesEssence = pureVegetablesEssence;
        this.tier = tier;
    }

    public int getPureCulinaryEssence(){
        return pureCulinaryEssence;
    }

    public int getPureFruitsEssence() {
        return pureFruitsEssence;
    }

    public int getPureGrainsEssence() {
        return pureGrainsEssence;
    }

    public int getPureProteinsEssence() {
        return pureProteinsEssence;
    }

    public int getPureSugarsEssence() {
        return pureSugarsEssence;
    }

    public int getPureVegetablesEssence() {
        return pureVegetablesEssence;
    }

    public int getTier() {
        return tier;
    }
}

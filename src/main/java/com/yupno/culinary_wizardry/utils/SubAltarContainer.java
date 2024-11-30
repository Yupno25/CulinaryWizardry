package com.yupno.culinary_wizardry.utils;

import com.yupno.culinary_wizardry.block.entity.custom.SubAltarBlockEntity;

public class SubAltarContainer {
    private final FoodType foodType;
    private SubAltarBlockEntity subAltarBlockEntity = null;
    private int currentEssenceCost = 0;
    private int remainingEssenceCost = 0;
    private float currentEssenceOverflow = 0;

    public SubAltarContainer(FoodType foodType) {
        this.foodType = foodType;
    }

    /**
     * Getter and Setter Methods
     */

    public SubAltarBlockEntity getSubAltarBlockEntity() {
        return subAltarBlockEntity;
    }

    public void setSubAltarBlockEntity(SubAltarBlockEntity subAltarBlockEntity) {
        this.subAltarBlockEntity = subAltarBlockEntity;
    }

    public int getCurrentEssenceCost() {
        return currentEssenceCost;
    }

    public void setCurrentEssenceCost(int currentEssenceCost) {
        this.currentEssenceCost = currentEssenceCost;
    }

    public int getRemainingEssenceCost() {
        return remainingEssenceCost;
    }

    public void setRemainingEssenceCost(int remainingEssenceCost) {
        this.remainingEssenceCost = remainingEssenceCost;
    }

    public float getCurrentEssenceOverflow() {
        return currentEssenceOverflow;
    }

    public void setCurrentEssenceOverflow(float currentEssenceOverflow) {
        this.currentEssenceOverflow = currentEssenceOverflow;
    }
}

package com.yupno.culinary_wizardry.utils;

public enum FoodType {
    CULINARY("culinary"),
    FRUITS("fruits"),
    GRAINS("grains"),
    PROTEINS("proteins"),
    SUGARS("sugars"),
    VEGETABLES("vegetables");

    private String name;

    FoodType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

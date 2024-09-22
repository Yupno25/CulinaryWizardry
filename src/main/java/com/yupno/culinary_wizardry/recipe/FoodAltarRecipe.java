package com.yupno.culinary_wizardry.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.utils.FoodType;
import com.yupno.culinary_wizardry.utils.SimpleFoodContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FoodAltarRecipe implements Recipe<SimpleFoodContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final int pureCulinaryEssence;
    private final int pureFruitsEssence;
    private final int pureGrainsEssence;
    private final int pureProteinsEssence;
    private final int pureSugarsEssence;
    private final int pureVegetablesEssence;
    private final int tier;
    private final NonNullList<Ingredient> recipeItems;

    public FoodAltarRecipe(ResourceLocation id, ItemStack output, int pureCulinaryEssence, int pureFruitsEssence, int pureGrainsEssence,
                           int pureProteinsEssence, int pureSugarsEssence, int pureVegetablesEssence, int tier, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.pureCulinaryEssence = pureCulinaryEssence;
        this.pureFruitsEssence = pureFruitsEssence;
        this.pureGrainsEssence = pureGrainsEssence;
        this.pureProteinsEssence = pureProteinsEssence;
        this.pureSugarsEssence = pureSugarsEssence;
        this.pureVegetablesEssence = pureVegetablesEssence;
        this.tier = tier;
        this.recipeItems = recipeItems;
    }

    /**
     * pContainer = The block
     * recipeItems = The recipe
     */

    @Override
    public boolean matches(SimpleFoodContainer pContainer, Level pLevel) {
        if(pContainer.getPureCulinaryEssence() < pureCulinaryEssence || pContainer.getPureFruitsEssence() < pureFruitsEssence ||
           pContainer.getPureGrainsEssence() < pureGrainsEssence || pContainer.getPureProteinsEssence() < pureProteinsEssence ||
           pContainer.getPureSugarsEssence() < pureSugarsEssence || pContainer.getPureVegetablesEssence() < pureVegetablesEssence)
            return false;

        if(pContainer.getTier() < tier)
            return false;

        // Converts the recipeItems into an ItemStack List
        List<String> recipeList = new ArrayList<>();
        for (int i = 0; i < recipeItems.size(); i++) {
            recipeList.add(recipeItems.get(i).getItems()[0].getItem().toString());
        }

        if(recipeList.size() > 1 && pContainer.getTier() == 0)
            return false;

        // -1 cause of result slot
        // Converts simpleContainer into an ItemStack List
        List<String> containerList = new ArrayList<>();
        for (int i = 0; i < pContainer.getContainerSize() - 1; i++) {
            if(pContainer.getItem(i) != ItemStack.EMPTY)
                containerList.add(pContainer.getItem(i).getItem().toString());
        }

        for (int i = 0; i < containerList.size(); i++) {
            recipeList.remove(containerList.get(i));
        }

        return recipeList.isEmpty();
    }

    @Override
    public ItemStack assemble(SimpleFoodContainer pContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    public List<String> getRecipeList(){
        List<String> recipeList = new ArrayList<>();
        for (int i = 0; i < recipeItems.size(); i++) {
            recipeList.add(recipeItems.get(i).getItems()[0].getItem().toString());
        }
        return recipeList;
    }

    public int getPureCulinaryEssenceCost() {
        return pureCulinaryEssence;
    }
    public int getPureFruitsEssenceCost() {
        return pureFruitsEssence;
    }

    public int getPureGrainsEssenceCost() {
        return pureGrainsEssence;
    }

    public int getPureProteinsEssenceCost() {
        return pureProteinsEssence;
    }

    public int getPureSugarsEssenceCost() {
        return pureSugarsEssence;
    }

    public int getPureVegetablesEssenceCost() {
        return pureVegetablesEssence;
    }

    public int getEssenceCostByType(FoodType foodType){
        switch (foodType){
            case CULINARY: return pureCulinaryEssence;
            case FRUITS: return pureFruitsEssence;
            case GRAINS: return pureGrainsEssence;
            case PROTEINS: return pureProteinsEssence;
            case SUGARS: return pureSugarsEssence;
            case VEGETABLES: return pureVegetablesEssence;
            default: return 0;
        }
    }

    public int getTier() {
        return tier;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<FoodAltarRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "food_altar";
    }

    public static class Serializer implements RecipeSerializer<FoodAltarRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(CulinaryWizardry.MOD_ID, "food_altar");

        @Override
        public FoodAltarRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            int pureCulinaryEssence = GsonHelper.getAsInt(json, "pure_culinary_essence");
            int pureFruitsEssence = GsonHelper.getAsInt(json, "pure_fruits_essence");
            int pureGrainsEssence = GsonHelper.getAsInt(json, "pure_grains_essence");
            int pureProteinsEssence = GsonHelper.getAsInt(json, "pure_proteins_essence");
            int pureSugarsEssence = GsonHelper.getAsInt(json, "pure_sugars_essence");
            int pureVegetablesEssence = GsonHelper.getAsInt(json, "pure_vegetables_essence");
            int tier = GsonHelper.getAsInt(json, "tier");

            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new FoodAltarRecipe(id, output, pureCulinaryEssence, pureFruitsEssence, pureGrainsEssence,
                    pureProteinsEssence, pureSugarsEssence, pureVegetablesEssence, tier, inputs);
        }

        /* MAKE SURE FROM AND TO NETWORK HAVE THE SAME ORDER OF OPERATIONS */
        /* MAKE SURE FROM AND TO NETWORK HAVE THE SAME ORDER OF OPERATIONS */
        /* MAKE SURE FROM AND TO NETWORK HAVE THE SAME ORDER OF OPERATIONS */

        @Override
        public FoodAltarRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            int pureCulinaryEssence = buf.readInt();
            int pureFruitsEssence = buf.readInt();
            int pureGrainsEssence = buf.readInt();
            int pureProteinsEssence = buf.readInt();
            int pureSugarsEssence = buf.readInt();
            int pureVegetablesEssence = buf.readInt();
            int tier = buf.readInt();
            return new FoodAltarRecipe(id, output, pureCulinaryEssence, pureFruitsEssence, pureGrainsEssence,
                    pureProteinsEssence, pureSugarsEssence, pureVegetablesEssence, tier, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FoodAltarRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeInt(recipe.getPureCulinaryEssenceCost());
            buf.writeInt(recipe.getPureFruitsEssenceCost());
            buf.writeInt(recipe.getPureGrainsEssenceCost());
            buf.writeInt(recipe.getPureProteinsEssenceCost());
            buf.writeInt(recipe.getPureSugarsEssenceCost());
            buf.writeInt(recipe.getPureVegetablesEssenceCost());
            buf.writeInt(recipe.getTier());
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>) cls;
        }
    }
}

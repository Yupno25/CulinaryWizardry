package com.yupno.culinary_wizardry.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.utils.FoodType;
import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FoodAltarRecipe implements Recipe<SimpleEssenceContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final int culinaryEssence;
    private final int fruitsEssence;
    private final int grainsEssence;
    private final int proteinsEssence;
    private final int sugarsEssence;
    private final int vegetablesEssence;
    private final int tier;
    private final NonNullList<Ingredient> recipeItems;

    public FoodAltarRecipe(
        ResourceLocation id, ItemStack output, int culinaryEssence, int fruitsEssence, int grainsEssence,
        int proteinsEssence, int sugarsEssence, int vegetablesEssence, int tier, NonNullList<Ingredient> recipeItems
    ) {
        this.id = id;
        this.output = output;
        this.culinaryEssence = culinaryEssence;
        this.fruitsEssence = fruitsEssence;
        this.grainsEssence = grainsEssence;
        this.proteinsEssence = proteinsEssence;
        this.sugarsEssence = sugarsEssence;
        this.vegetablesEssence = vegetablesEssence;
        this.tier = tier;
        this.recipeItems = recipeItems;
    }

    /**
     * pContainer = The block
     * recipeItems = The recipe
     */

    @Override
    public boolean matches(SimpleEssenceContainer pContainer, @NotNull Level pLevel) {
        if (pContainer.getCulinaryEssence() < culinaryEssence || pContainer.getFruitsEssence() < fruitsEssence ||
            pContainer.getGrainsEssence() < grainsEssence || pContainer.getProteinsEssence() < proteinsEssence ||
            pContainer.getSugarsEssence() < sugarsEssence || pContainer.getVegetablesEssence() < vegetablesEssence)
            return false;

        if (pContainer.getTier() < tier || recipeItems.size() > 5 || recipeItems.size() == 0 || (tier == 0 && recipeItems.size() > 1))
            return false;

        // Converts the recipeItems into an ItemStack List
        List<String> recipeList = new ArrayList<>();
        for (Ingredient recipeItem : recipeItems) {
            recipeList.add(recipeItem.getItems()[0].getItem().toString());
        }

        if (recipeList.size() > 1 && pContainer.getTier() == 0)
            return false;

        // -1 cause of result slot
        // Converts simpleContainer into an ItemStack List
        List<String> containerList = new ArrayList<>();
        for (int i = 0; i < pContainer.getContainerSize() - 1; i++) {
            if (pContainer.getItem(i) != ItemStack.EMPTY)
                containerList.add(pContainer.getItem(i).getItem().toString());
        }

        for (String s : containerList) {
            recipeList.remove(s);
        }

        return recipeList.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleEssenceContainer pContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return output.copy();
    }

    public List<String> getRecipeList() {
        List<String> recipeList = new ArrayList<>();
        for (Ingredient recipeItem : recipeItems) {
            recipeList.add(recipeItem.getItems()[0].getItem().toString());
        }
        return recipeList;
    }

    public int getCulinaryEssenceCost() {
        return culinaryEssence;
    }

    public int getFruitsEssenceCost() {
        return fruitsEssence;
    }

    public int getGrainsEssenceCost() {
        return grainsEssence;
    }

    public int getProteinsEssenceCost() {
        return proteinsEssence;
    }

    public int getSugarsEssenceCost() {
        return sugarsEssence;
    }

    public int getVegetablesEssenceCost() {
        return vegetablesEssence;
    }

    public int getEssenceCostByType(FoodType foodType) {
        return switch (foodType) {
            case CULINARY -> culinaryEssence;
            case FRUITS -> fruitsEssence;
            case GRAINS -> grainsEssence;
            case PROTEINS -> proteinsEssence;
            case SUGARS -> sugarsEssence;
            case VEGETABLES -> vegetablesEssence;
        };
    }

    public int getTier() {
        return tier;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
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
        public @NotNull FoodAltarRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            int culinaryEssence = GsonHelper.getAsInt(json, "culinary_essence");
            int fruitsEssence = GsonHelper.getAsInt(json, "fruits_essence");
            int grainsEssence = GsonHelper.getAsInt(json, "grains_essence");
            int proteinsEssence = GsonHelper.getAsInt(json, "proteins_essence");
            int sugarsEssence = GsonHelper.getAsInt(json, "sugars_essence");
            int vegetablesEssence = GsonHelper.getAsInt(json, "vegetables_essence");
            int tier = GsonHelper.getAsInt(json, "tier");

            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new FoodAltarRecipe(
                id, output, culinaryEssence, fruitsEssence, grainsEssence,
                proteinsEssence, sugarsEssence, vegetablesEssence, tier, inputs
            );
        }

        /* MAKE SURE FROM AND TO NETWORK HAVE THE SAME ORDER OF OPERATIONS */
        /* MAKE SURE FROM AND TO NETWORK HAVE THE SAME ORDER OF OPERATIONS */
        /* MAKE SURE FROM AND TO NETWORK HAVE THE SAME ORDER OF OPERATIONS */

        @Override
        public FoodAltarRecipe fromNetwork(@NotNull ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            int culinaryEssence = buf.readInt();
            int fruitsEssence = buf.readInt();
            int grainsEssence = buf.readInt();
            int proteinsEssence = buf.readInt();
            int sugarsEssence = buf.readInt();
            int vegetablesEssence = buf.readInt();
            int tier = buf.readInt();
            return new FoodAltarRecipe(
                id, output, culinaryEssence, fruitsEssence, grainsEssence,
                proteinsEssence, sugarsEssence, vegetablesEssence, tier, inputs
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FoodAltarRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeInt(recipe.getCulinaryEssenceCost());
            buf.writeInt(recipe.getFruitsEssenceCost());
            buf.writeInt(recipe.getGrainsEssenceCost());
            buf.writeInt(recipe.getProteinsEssenceCost());
            buf.writeInt(recipe.getSugarsEssenceCost());
            buf.writeInt(recipe.getVegetablesEssenceCost());
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

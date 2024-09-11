package com.yupno.culinary_wizardry.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.utils.SimpleFoodContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class FoodAltarRecipe implements Recipe<SimpleFoodContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final int pureCulinaryEssence;
    private final NonNullList<Ingredient> recipeItems;

    public FoodAltarRecipe(ResourceLocation id, ItemStack output, int pureCulinaryEssence, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.pureCulinaryEssence = pureCulinaryEssence;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(SimpleFoodContainer pContainer, Level pLevel) {
        return recipeItems.get(0).test(pContainer.getItem(0)) && pContainer.getPureCulinaryEssence() >= pureCulinaryEssence;
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

    public int getPureCulinaryEssenceCost() {
        return pureCulinaryEssence;
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
        public static final String ID = "food_altar_tier0";
    }

    public static class Serializer implements RecipeSerializer<FoodAltarRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(CulinaryWizardry.MOD_ID, "food_altar_tier0");

        @Override
        public FoodAltarRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            int pureCulinaryEssence = GsonHelper.getAsInt(json, "pure_culinary_essence");

            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new FoodAltarRecipe(id, output, pureCulinaryEssence, inputs);
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
            return new FoodAltarRecipe(id, output, pureCulinaryEssence, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FoodAltarRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeInt(recipe.getPureCulinaryEssenceCost());
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

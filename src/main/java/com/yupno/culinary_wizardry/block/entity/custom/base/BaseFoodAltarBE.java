package com.yupno.culinary_wizardry.block.entity.custom.base;

import com.yupno.culinary_wizardry.block.entity.custom.SubAltarBlockEntity;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.utils.FoodType;
import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
import com.yupno.culinary_wizardry.utils.SubAltarContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * ONLY FOR TIER 2 ALTAR AND ABOVE
 */
public class BaseFoodAltarBE extends CoreFoodAltarBE {
    public BaseFoodAltarBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int tier) {
        super(pType, pPos, pBlockState, tier);
    }

    public Vec3i[] subAltarShifts;

    public final Vec3i[] xSubAltarShifts = new Vec3i[]{
            new Vec3i(0, 0, 3),
            new Vec3i(0, 0, -3),
            new Vec3i(3, 0, 2),
            new Vec3i(3, 0, -2),
            new Vec3i(-3, 0, 2),
            new Vec3i(-3, 0, -2),
    };

    public final Vec3i[] zSubAltarShifts = new Vec3i[]{
            new Vec3i(3, 0, 0),
            new Vec3i(-3, 0, 0),
            new Vec3i(2, 0, 3),
            new Vec3i(-2, 0, 3),
            new Vec3i(2, 0, -3),
            new Vec3i(-2, 0, -3),
    };

    public final Map<FoodType, SubAltarContainer> subAltars = Map.of(
            FoodType.CULINARY, new SubAltarContainer(FoodType.CULINARY),
            FoodType.FRUITS, new SubAltarContainer(FoodType.FRUITS),
            FoodType.GRAINS, new SubAltarContainer(FoodType.GRAINS),
            FoodType.PROTEINS, new SubAltarContainer(FoodType.PROTEINS),
            FoodType.SUGARS, new SubAltarContainer(FoodType.SUGARS),
            FoodType.VEGETABLES, new SubAltarContainer(FoodType.VEGETABLES)
    );

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", craftingProgress);
        tag.putInt("internalTicks", internalTicks);
        tag.putBoolean("isComplete", isFullAltar());
        // Saves which direction the altarShape is in
        int directionX;
        if (subAltarShifts == null) {
            directionX = 0;
        } else if (subAltarShifts[0].getZ() == 3) {
            directionX = 1;
        } else {
            directionX = 2;
        }
        tag.putInt("directionIsX", directionX);


        for (FoodType foodType : FoodType.values()) {
            tag.putInt("currentEssence" + foodType.getName(), subAltars.get(foodType).getCurrentEssenceCost());
            tag.putInt("remainingEssence" + foodType.getName(), subAltars.get(foodType).getRemainingEssenceCost());
            tag.putFloat("currentOverflow" + foodType.getName(), subAltars.get(foodType).getCurrentEssenceOverflow());
        }

        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        craftingProgress = nbt.getInt("progress");
        internalTicks = nbt.getInt("internalTicks");
        setFullAltar(nbt.getBoolean("isComplete"));

        // Loads which direction the altarShape is in
        if (nbt.getInt("directionIsX") == 1) {
            subAltarShifts = xSubAltarShifts;
        } else if (nbt.getInt("directionIsX") == 2) {
            subAltarShifts = zSubAltarShifts;
        }

        for (FoodType foodType : FoodType.values()) {
            subAltars.get(foodType).setCurrentEssenceCost(nbt.getInt("currentEssence" + foodType.getName()));
            subAltars.get(foodType).setRemainingEssenceCost(nbt.getInt("remainingEssence" + foodType.getName()));
            subAltars.get(foodType).setCurrentEssenceOverflow(nbt.getFloat("currentOverflow" + foodType.getName()));
        }
    }

    /**
     * Multiblock Logic
     */

    public void checkForMultiblock() {
        // Checks for direction of subAltars
        if (level.getBlockEntity(worldPosition.offset(xSubAltarShifts[0])) instanceof SubAltarBlockEntity) {
            subAltarShifts = xSubAltarShifts;
        } else if (level.getBlockEntity(worldPosition.offset(zSubAltarShifts[0])) instanceof SubAltarBlockEntity) {
            subAltarShifts = zSubAltarShifts;
        } else {
            failedUpdate();
            return;
        }

        // Checks if all six SubAltars are present
        List<FoodType> foodTypes = new LinkedList<>(Arrays.asList(FoodType.values()));
        for (FoodType foodType : FoodType.values()) {
            for (Vec3i offset : subAltarShifts) {
                BlockEntity blockEntity = level.getBlockEntity(worldPosition.offset(offset));

                if (blockEntity instanceof SubAltarBlockEntity subAltarBlock) {
                    if (foodTypes.contains(subAltarBlock.getFoodType()) && subAltarBlock.getTier() <= getTier()) {
                        foodTypes.remove(subAltarBlock.getFoodType());
                        // Caches SubAltarBlockEntities
                        subAltars.get(subAltarBlock.getFoodType()).setSubAltarBlockEntity(subAltarBlock);
                        break;
                    }
                } else {
                    failedUpdate();
                    return;
                }
            }
        }

        if (foodTypes.isEmpty()) {
            setFullAltar(true);
        } else {
            failedUpdate();
        }
    }

    public void failedUpdate() {
        setFullAltar(false);
        for (FoodType foodType : FoodType.values()) {
            subAltars.get(foodType).setSubAltarBlockEntity(null);
        }
    }

    /**
     * Particles
     */

    public static void craftingAnimationParticles(Level pLevel, BlockPos blockPos, BaseFoodAltarBE pBlockEntity) {
        if (pBlockEntity.subAltarShifts != null && pBlockEntity.craftingProgress > 0 && pBlockEntity.craftingProgress % 3 == 0) {
            for (Vec3i offset : pBlockEntity.subAltarShifts) {
                BlockPos pos = blockPos.offset(offset);

                pLevel.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        -offset.getX() * 0.1, -offset.getY() * 0.1, -offset.getZ() * 0.1);
            }
        }

        if (pBlockEntity.craftingProgress == pBlockEntity.maxCraftingProgress) {
            Random random = new Random();
            for (int i = 0; i < 12; i++) {
                pLevel.addParticle(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5,
                        random.nextFloat(-0.1F, 0.1F), 0.2, random.nextFloat(-0.1F, 0.1F));

            }
        }
    }

    /**
     * Crafting Logic
     */

    public static void craftingLogic(BaseFoodAltarBE entity, Level pLevel, BlockPos pPos, BlockState pState) {
        if (entity.isFullAltar() && checkOrCraftItem(entity, false)) {
            entity.craftingProgress++;
            pLevel.markAndNotifyBlock(pPos, pLevel.getChunkAt(pPos), pState, pState, Block.UPDATE_CLIENTS, 0);

            for (FoodType foodType : FoodType.values()) {
                SubAltarContainer subAltar = entity.subAltars.get(foodType);
                SubAltarBlockEntity subAltarBlock = subAltar.getSubAltarBlockEntity();

                if (subAltarBlock == null)
                    return;

                setChanged(pLevel, pPos, pState);
                if (entity.craftingProgress > entity.maxCraftingProgress) {
                    checkOrCraftItem(entity, true);
                    return;
                }

                // Gets 1/maxProgress of the essence so that the essence drains slowly and not all at once
                // Needs to round since the division is almost certainly not an int
                // The overflow from that is saved to ensure that the full cost will be paid
                float temp = (float) subAltar.getCurrentEssenceCost() / entity.maxCraftingProgress;
                int temp2 = Math.round(temp + subAltar.getCurrentEssenceOverflow());
                if (temp2 == Math.round(temp)) {
                    subAltar.setCurrentEssenceOverflow(subAltar.getCurrentEssenceOverflow() + temp - temp2);
                } else {
                    subAltar.setCurrentEssenceOverflow(subAltar.getCurrentEssenceOverflow() - (temp2 - temp));
                }

                subAltarBlock.setEssence(subAltarBlock.getEssence() - temp2);
                subAltar.setRemainingEssenceCost(subAltar.getRemainingEssenceCost() - temp2);
            }
        } else {
            entity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    public static boolean checkOrCraftItem(BaseFoodAltarBE entity, boolean craft) {
        for (FoodType foodType : FoodType.values()) {
            if (entity.subAltars.get(foodType).getSubAltarBlockEntity() == null)
                return false;
        }

        Level level = entity.level;
        SimpleEssenceContainer inventory;

        inventory = new SimpleEssenceContainer(entity.itemHandler.getSlots(),
                calculateEssence(entity, FoodType.CULINARY),
                calculateEssence(entity, FoodType.FRUITS),
                calculateEssence(entity, FoodType.GRAINS),
                calculateEssence(entity, FoodType.PROTEINS),
                calculateEssence(entity, FoodType.SUGARS),
                calculateEssence(entity, FoodType.VEGETABLES),
                entity.getTier());


        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // Reverses the order of chosen recipes, this leads to the first recipe chosen being the one with the most ingredients
        // This prevents recipes from blocking each other
        List<FoodAltarRecipe> recipes = level.getRecipeManager().getAllRecipesFor(FoodAltarRecipe.Type.INSTANCE);
        Optional<FoodAltarRecipe> match = recipes.stream().sorted(Comparator.comparingInt(recipe -> ((FoodAltarRecipe) recipe).getRecipeList().size()).reversed())
                .filter(recipe -> recipe.matches(inventory, level)).findFirst();


        if (match.isPresent() && canInsertAmountIntoOutputSlot(inventory) && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem())) {
            List<String> recipeList = match.get().getRecipeList();
            for (int i = 0; i < 5; i++) {
                String string = entity.itemHandler.getStackInSlot(i).getItem().toString();
                if (recipeList.contains(string)) {
                    entity.usedItemSlots[i] = i;
                    if (craft) entity.itemHandler.extractItem(i, 1, false);
                    recipeList.remove(string);
                } else {
                    entity.usedItemSlots[i] = -1;
                }
            }

            if (craft) {
                entity.itemHandler.setStackInSlot(5, new ItemStack(match.get().getResultItem().getItem(),
                        entity.itemHandler.getStackInSlot(5).getCount() + 1));

                entity.resetProgress();
            } else {
                for (FoodType foodType : FoodType.values()) {
                    SubAltarContainer subAltar = entity.subAltars.get(foodType);
                    subAltar.setCurrentEssenceCost(match.get().getEssenceCostByType(foodType));
                    if (subAltar.getRemainingEssenceCost() == 0) {
                        subAltar.setRemainingEssenceCost(subAltar.getCurrentEssenceCost());
                    }
                }
                return true;
            }
        }

        return false;
    }

    public static int calculateEssence(BlockEntity entity, FoodType foodType) {
        SubAltarContainer subAltar = null;
        if (entity instanceof BaseFoodAltarBE) {
            subAltar = ((BaseFoodAltarBE) entity).subAltars.get(foodType);
        }

        if (subAltar.getCurrentEssenceCost() != 0 && (subAltar.getCurrentEssenceCost() - subAltar.getRemainingEssenceCost()) == 0) {
            return subAltar.getSubAltarBlockEntity().getEssence() + subAltar.getCurrentEssenceCost();
        } else {
            return subAltar.getSubAltarBlockEntity().getEssence() + (subAltar.getCurrentEssenceCost() - subAltar.getRemainingEssenceCost());
        }
    }

    public void resetProgress() {
        this.craftingProgress = 0;
        for (FoodType foodType : FoodType.values()) {
            subAltars.get(foodType).setCurrentEssenceCost(0);
            subAltars.get(foodType).setRemainingEssenceCost(0);
            subAltars.get(foodType).setCurrentEssenceOverflow(0);
        }
        level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS, 0);
    }
}

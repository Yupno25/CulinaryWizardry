package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.screen.FoodAltarTier2Menu;
import com.yupno.culinary_wizardry.utils.FoodType;
import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
import com.yupno.culinary_wizardry.utils.SubAltarContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FoodAltarTier2BlockEntity extends BaseFoodAltarBlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int craftingProgress = 0;
    private int maxCraftingProgress = 28;
    private final int tier = 2;

    public FoodAltarTier2BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER2_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return FoodAltarTier2BlockEntity.this.craftingProgress;
                    case 1:
                        return FoodAltarTier2BlockEntity.this.maxCraftingProgress;
                    case 2:
                        return FoodAltarTier2BlockEntity.this.subAltars.get(FoodType.CULINARY).getSafeEssence();
                    case 3:
                        return FoodAltarTier2BlockEntity.this.subAltars.get(FoodType.FRUITS).getSafeEssence();
                    case 4:
                        return FoodAltarTier2BlockEntity.this.subAltars.get(FoodType.GRAINS).getSafeEssence();
                    case 5:
                        return FoodAltarTier2BlockEntity.this.subAltars.get(FoodType.PROTEINS).getSafeEssence();
                    case 6:
                        return FoodAltarTier2BlockEntity.this.subAltars.get(FoodType.SUGARS).getSafeEssence();
                    case 7:
                        return FoodAltarTier2BlockEntity.this.subAltars.get(FoodType.VEGETABLES).getSafeEssence();
                    case 8:
                        return FoodAltarTier2BlockEntity.this.usedItemSlots[0];
                    case 9:
                        return FoodAltarTier2BlockEntity.this.usedItemSlots[1];
                    case 10:
                        return FoodAltarTier2BlockEntity.this.usedItemSlots[2];
                    case 11:
                        return FoodAltarTier2BlockEntity.this.usedItemSlots[3];
                    case 12:
                        return FoodAltarTier2BlockEntity.this.usedItemSlots[4];
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        FoodAltarTier2BlockEntity.this.craftingProgress = value;
                        break;
                    case 1:
                        FoodAltarTier2BlockEntity.this.maxCraftingProgress = value;
                        break;
                    case 2, 3, 4, 5, 6, 7:
                        break;
                    case 8:
                        FoodAltarTier2BlockEntity.this.usedItemSlots[0] = value;
                        break;
                    case 9:
                        FoodAltarTier2BlockEntity.this.usedItemSlots[1] = value;
                        break;
                    case 10:
                        FoodAltarTier2BlockEntity.this.usedItemSlots[2] = value;
                        break;
                    case 11:
                        FoodAltarTier2BlockEntity.this.usedItemSlots[3] = value;
                        break;
                    case 12:
                        FoodAltarTier2BlockEntity.this.usedItemSlots[4] = value;
                        break;
                }
            }

            public int getCount() {
                return 13;
            }
        };
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", craftingProgress);
        tag.putInt("internalTicks", internalTicks);
        tag.putBoolean("isComplete", isFullAltar());

        for (FoodType foodType : FoodType.values()) {
            tag.putInt("currentEssence" + foodType.getName(), subAltars.get(foodType).getCurrentEssenceCost());
            tag.putInt("remainingEssence" + foodType.getName(), subAltars.get(foodType).getRemainingEssenceCost());
            tag.putFloat("currentOverflow" + foodType.getName(), subAltars.get(foodType).getCurrentEssenceOverflow());
        }

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        craftingProgress = nbt.getInt("progress");
        internalTicks = nbt.getInt("internalTicks");
        setFullAltar(nbt.getBoolean("isComplete"));

        for (FoodType foodType : FoodType.values()) {
            subAltars.get(foodType).setCurrentEssenceCost(nbt.getInt("currentEssence" + foodType.getName()));
            subAltars.get(foodType).setRemainingEssenceCost(nbt.getInt("remainingEssence" + foodType.getName()));
            subAltars.get(foodType).setCurrentEssenceOverflow(nbt.getFloat("currentOverflow" + foodType.getName()));
        }
    }

    private void update() {
        Vec3i[] subAltarShifts;

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
                    if (foodTypes.contains(subAltarBlock.getFoodType()) && subAltarBlock.getTier() == getTier()) {
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

    private void failedUpdate() {
        setFullAltar(false);
        for (FoodType foodType : FoodType.values()) {
            subAltars.get(foodType).setSubAltarBlockEntity(null);
        }
    }

    public int getTier() {
        return tier;
    }

    /**
     * RECIPE STUFF
     */
    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier2BlockEntity entity) {
        entity.internalTicks++;
        if (entity.internalTicks % 20 == 0) {
            entity.internalTicks = 0;
            entity.update();
        }

        if (entity.isFullAltar() && checkOrCraftItem(entity, false)) {
            entity.craftingProgress++;

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

    private static boolean checkOrCraftItem(FoodAltarTier2BlockEntity entity, boolean craft) {
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
                if (match.isPresent()) {
                    entity.itemHandler.setStackInSlot(5, new ItemStack(match.get().getResultItem().getItem(),
                            entity.itemHandler.getStackInSlot(5).getCount() + 1));

                    entity.resetProgress();
                }
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

    private void resetProgress() {
        this.craftingProgress = 0;
        for (FoodType foodType : FoodType.values()) {
            subAltars.get(foodType).setCurrentEssenceCost(0);
            subAltars.get(foodType).setRemainingEssenceCost(0);
            subAltars.get(foodType).setCurrentEssenceOverflow(0);
        }
    }

    /**
     * Particles
     */
    private static final int TIME_BETWEEN_PARTICLES = 10;

    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier2BlockEntity pBlockEntity) {

    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier2");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory playerInventory, Player pPlayer) {
        return new FoodAltarTier2Menu(pContainerId, playerInventory, this, this.data);
    }
}

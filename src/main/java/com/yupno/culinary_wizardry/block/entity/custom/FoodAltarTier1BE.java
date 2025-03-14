package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.block.entity.custom.base.CoreFoodAltarBE;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.screen.FoodAltarTier1Menu;
import com.yupno.culinary_wizardry.utils.FoodType;
import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class FoodAltarTier1BE extends CoreFoodAltarBE implements MenuProvider {
    private final Vec3i subAltarShift = new Vec3i(0, 3, 0);
    private SubAltarBlockEntity subAltarBlockEntity = null;
    private int currentCulinaryEssenceCost = 0;
    private int remainingCulinaryEssenceCost = 0;
    private float currentCulinaryEssenceOverflow = 0;

    public FoodAltarTier1BE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER1_ENTITY.get(), pPos, pBlockState, 1);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", craftingProgress);
        tag.putInt("internalTicks", internalTicks);
        tag.putBoolean("isComplete", isFullAltar());
        tag.putInt("currentCulinary", currentCulinaryEssenceCost);
        tag.putInt("remainingCulinary", remainingCulinaryEssenceCost);
        tag.putFloat("currentOverflow", currentCulinaryEssenceOverflow);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        craftingProgress = nbt.getInt("progress");
        internalTicks = nbt.getInt("internalTicks");
        setFullAltar(nbt.getBoolean("isComplete"));
        currentCulinaryEssenceCost = nbt.getInt("currentCulinary");
        remainingCulinaryEssenceCost = nbt.getInt("remainingCulinary");
        currentCulinaryEssenceOverflow = nbt.getFloat("currentOverflow");
    }

    private void update() {
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.offset(subAltarShift));

        if (blockEntity instanceof SubAltarBlockEntity subAltarBlock && subAltarBlock.getFoodType().equals(FoodType.CULINARY) && subAltarBlock.getTier() == getTier()) {
            setFullAltar(true);

            // Cache SubAltarBlockEntity
            subAltarBlockEntity = subAltarBlock;
        } else {
            failedUpdate();
        }
    }

    private void failedUpdate() {
        setFullAltar(false);
        subAltarBlockEntity = null;
    }

    public SubAltarBlockEntity getSubAltar() {
        return subAltarBlockEntity;
    }

    /**
     * RECIPE STUFF
     */
    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier1BE entity) {
        entity.internalTicks++;
        if (entity.internalTicks % 20 == 0) {
            entity.internalTicks = 0;
            entity.update();
        }

        craftingLogic(pLevel, pPos, pState, entity);
    }

    private static void craftingLogic(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier1BE entity) {
        if (entity.isFullAltar() && checkOrCraftItem(entity, false)) {
            entity.craftingProgress++;
            pLevel.markAndNotifyBlock(pPos, pLevel.getChunkAt(pPos), pState, pState, Block.UPDATE_CLIENTS, 0);

            SubAltarBlockEntity subAltarBlockEntity = entity.getSubAltar();

            if (subAltarBlockEntity == null)
                return;

            setChanged(pLevel, pPos, pState);
            if (entity.craftingProgress > entity.maxCraftingProgress) {
                checkOrCraftItem(entity, true);
                return;
            }

            // Gets 1/maxProgress of the essence so that the essence drains slowly and not all at once
            // Needs to round since the division is almost certainly not an int
            // The overflow from that is saved to ensure that the full cost will be paid
            float temp = (float) entity.currentCulinaryEssenceCost / entity.maxCraftingProgress;
            int temp2 = Math.round(temp + entity.currentCulinaryEssenceOverflow);
            if (temp2 == Math.round(temp)) {
                entity.currentCulinaryEssenceOverflow += temp - temp2;
            } else {
                entity.currentCulinaryEssenceOverflow -= temp2 - temp;
            }


            subAltarBlockEntity.setEssence(subAltarBlockEntity.getEssence() - temp2);
            entity.remainingCulinaryEssenceCost -= temp2;
        } else {
            entity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean checkOrCraftItem(FoodAltarTier1BE entity, boolean craft) {
        SubAltarBlockEntity subAltarBlockEntity = entity.getSubAltar();

        if (subAltarBlockEntity == null)
            return false;

        Level level = entity.level;
        SimpleEssenceContainer inventory;

        if (entity.currentCulinaryEssenceCost != 0 && (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost) == 0) {
            inventory = new SimpleEssenceContainer(entity.itemHandler.getSlots(), subAltarBlockEntity.getEssence()
                    + entity.currentCulinaryEssenceCost, 0, 0, 0, 0, 0, entity.getTier());
        } else {
            inventory = new SimpleEssenceContainer(entity.itemHandler.getSlots(), subAltarBlockEntity.getEssence()
                    + (entity.currentCulinaryEssenceCost - entity.remainingCulinaryEssenceCost), 0,
                    0, 0, 0, 0, entity.getTier());
        }


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
                entity.currentCulinaryEssenceCost = match.get().getCulinaryEssenceCost();
                if (entity.remainingCulinaryEssenceCost == 0) {
                    entity.remainingCulinaryEssenceCost = entity.currentCulinaryEssenceCost;
                }
                return true;
            }
        }
        return false;
    }

    private void resetProgress() {
        this.craftingProgress = 0;
        this.currentCulinaryEssenceCost = 0;
        this.currentCulinaryEssenceOverflow = 0;
        this.remainingCulinaryEssenceCost = 0;
        level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS, 0);
    }

    /**
     * Particles
     */

    public static void clientTick(Level pLevel, BlockPos blockPos, BlockState pState, FoodAltarTier1BE pBlockEntity) {
        if (pBlockEntity.craftingProgress > 0 && pBlockEntity.craftingProgress % 3 == 0) {
            BlockPos pos = blockPos.offset(pBlockEntity.subAltarShift);

            pLevel.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    -pBlockEntity.subAltarShift.getX() * 0.1, -pBlockEntity.subAltarShift.getY() * 0.1, -pBlockEntity.subAltarShift.getZ() * 0.1);
        }

        if (pBlockEntity.craftingProgress == pBlockEntity.maxCraftingProgress) {
            Random random = new Random();
            for (int i = 0; i < 12; i++) {
                pLevel.addParticle(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5,
                        random.nextFloat(-0.1F, 0.1F), 0.2, random.nextFloat(-0.1F, 0.1F));

            }
        }
    }


    @Override
    public @NotNull Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier1");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory playerInventory, @NotNull Player pPlayer) {
        return new FoodAltarTier1Menu(pContainerId, playerInventory, this, this.data);
    }
}

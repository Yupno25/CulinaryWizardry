package com.yupno.culinary_wizardry.block.entity.custom;

import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.screen.FoodAltarTier3Menu;
import com.yupno.culinary_wizardry.utils.FoodType;
import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
import com.yupno.culinary_wizardry.utils.SubAltarContainer;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FoodAltarTier3BlockEntity extends BaseFoodAltarBlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int craftingProgress = 0;
    private int maxCraftingProgress = 28;
    private final int tier = 3;

    public FoodAltarTier3BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER3_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return FoodAltarTier3BlockEntity.this.craftingProgress;
                    case 1:
                        return FoodAltarTier3BlockEntity.this.maxCraftingProgress;
                    case 2:
                        return FoodAltarTier3BlockEntity.this.usedItemSlots[0];
                    case 3:
                        return FoodAltarTier3BlockEntity.this.usedItemSlots[1];
                    case 4:
                        return FoodAltarTier3BlockEntity.this.usedItemSlots[2];
                    case 5:
                        return FoodAltarTier3BlockEntity.this.usedItemSlots[3];
                    case 6:
                        return FoodAltarTier3BlockEntity.this.usedItemSlots[4];
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        FoodAltarTier3BlockEntity.this.craftingProgress = value;
                        break;
                    case 1:
                        FoodAltarTier3BlockEntity.this.maxCraftingProgress = value;
                        break;
                    case 2:
                        FoodAltarTier3BlockEntity.this.usedItemSlots[0] = value;
                        break;
                    case 3:
                        FoodAltarTier3BlockEntity.this.usedItemSlots[1] = value;
                        break;
                    case 4:
                        FoodAltarTier3BlockEntity.this.usedItemSlots[2] = value;
                        break;
                    case 5:
                        FoodAltarTier3BlockEntity.this.usedItemSlots[3] = value;
                        break;
                    case 6:
                        FoodAltarTier3BlockEntity.this.usedItemSlots[4] = value;
                        break;
                }
            }

            public int getCount() {
                return 7;
            }
        };
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", craftingProgress);
        tag.putInt("internalTicks", internalTicks);
        tag.putBoolean("isComplete", isFullAltar());
        // Saves which direction the altarShape is in
        int directionX;
        if(subAltarShifts == null){
            directionX = 0;
        } else if (subAltarShifts[0].getZ() == 3) {
            directionX = 1;
        }else {
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
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        craftingProgress = nbt.getInt("progress");
        internalTicks = nbt.getInt("internalTicks");
        setFullAltar(nbt.getBoolean("isComplete"));

        // Loads which direction the altarShape is in
        if(nbt.getInt("directionIsX") == 1){
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

    private void update() {
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
    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier3BlockEntity entity) {
        entity.internalTicks++;
        if (entity.internalTicks % 20 == 0) {
            entity.internalTicks = 0;
            entity.update();
        }

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

    private static boolean checkOrCraftItem(FoodAltarTier3BlockEntity entity, boolean craft) {
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
        level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS, 0);
    }

    /**
     * Particles
     */

    public static void clientTick(Level pLevel, BlockPos blockPos, BlockState pState, FoodAltarTier3BlockEntity pBlockEntity) {
        // Make it a separate method and put into basefoodaltar
        if (pBlockEntity.subAltarShifts != null && pBlockEntity.craftingProgress > 0 && pBlockEntity.craftingProgress % 3 == 0) {
            for (Vec3i offset : pBlockEntity.subAltarShifts) {
                BlockPos pos = blockPos.offset(offset);

                pLevel.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        -offset.getX() * 0.1, -offset.getY() * 0.1, -offset.getZ() * 0.1);
            }
        }

        if(pBlockEntity.craftingProgress == pBlockEntity.maxCraftingProgress){
            Random random = new Random();
            for (int i = 0; i < 12; i++) {
                pLevel.addParticle(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5,
                        random.nextFloat(-0.1F, 0.1F), 0.2, random.nextFloat(-0.1F, 0.1F));

            }
        }
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier3");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory playerInventory, Player pPlayer) {
        return new FoodAltarTier3Menu(pContainerId, playerInventory, this, this.data);
    }
}

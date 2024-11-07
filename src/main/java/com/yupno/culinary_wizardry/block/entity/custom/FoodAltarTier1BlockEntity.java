package com.yupno.culinary_wizardry.block.entity.custom;

import com.google.common.base.Predicates;
import com.yupno.culinary_wizardry.block.ModBlocks;
import com.yupno.culinary_wizardry.block.entity.ModBlockEntities;
import com.yupno.culinary_wizardry.recipe.FoodAltarRecipe;
import com.yupno.culinary_wizardry.screen.FoodAltarTier1Menu;
import com.yupno.culinary_wizardry.utils.SimpleEssenceContainer;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FoodAltarTier1BlockEntity extends BaseFoodAltarBlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int craftingProgress = 0;
    private int maxCraftingProgress = 28;
    private final int tier = 1;

    /**
     * SUBALTARSHIFT, HEIGHT, ALTARSHAPE, THISALTARHEIGHT AND ALTARLEVELS ARE CLOSELY CONNECTED
     * Height matches the number of Arrays in altarLevels
     * <p>
     * A layer of only Air/Any Blocks doesn't work
     * A layer with only a center block and other than that Any Block doesn't work
     */
    private final int thisAltarHeight = 1; // Position of this altar relative to the structure as a whole
    private final int blocksToCenter = 1; // How many blocks it takes to get to the center of the layer
    private final String[][] altarLevels = new String[][]{
            {"bBb", "B?B", "bBb"},
            {"w?w", "?f?", "w?w"},
            {"tAt", "A?A", "tAt"},
            {"AwA", "wuw", "AwA"},
            {"A?A", "?C?", "A?A"},
            {"AwA", "wdw", "AwA"}
    };
    private final int height = altarLevels.length; // Height of the whole structure
    private BlockPattern[] altarShapes = new BlockPattern[height];
    private final Vec3i subAltarShift = new Vec3i(0, 3, 0);
    private SubAltarBlockEntity subAltarBlockEntity = null;
    private int currentCulinaryEssenceCost = 0;
    private int remainingCulinaryEssenceCost = 0;
    private float currentCulinaryEssenceOverflow = 0;

    public FoodAltarTier1BlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FOOD_ALTAR_TIER1_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return FoodAltarTier1BlockEntity.this.craftingProgress;
                    case 1:
                        return FoodAltarTier1BlockEntity.this.maxCraftingProgress;
                    case 2:
                        return FoodAltarTier1BlockEntity.this.getSafeCulinaryEssence();
                    case 3:
                        return FoodAltarTier1BlockEntity.this.usedItemSlots[0];
                    case 4:
                        return FoodAltarTier1BlockEntity.this.usedItemSlots[1];
                    case 5:
                        return FoodAltarTier1BlockEntity.this.usedItemSlots[2];
                    case 6:
                        return FoodAltarTier1BlockEntity.this.usedItemSlots[3];
                    case 7:
                        return FoodAltarTier1BlockEntity.this.usedItemSlots[4];
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        FoodAltarTier1BlockEntity.this.craftingProgress = value;
                        break;
                    case 1:
                        FoodAltarTier1BlockEntity.this.maxCraftingProgress = value;
                        break;
                    case 2:
                        break;
                    case 3:
                        FoodAltarTier1BlockEntity.this.usedItemSlots[0] = value;
                        break;
                    case 4:
                        FoodAltarTier1BlockEntity.this.usedItemSlots[1] = value;
                        break;
                    case 5:
                        FoodAltarTier1BlockEntity.this.usedItemSlots[2] = value;
                        break;
                    case 6:
                        FoodAltarTier1BlockEntity.this.usedItemSlots[3] = value;
                        break;
                    case 7:
                        FoodAltarTier1BlockEntity.this.usedItemSlots[4] = value;
                        break;
                }
            }

            public int getCount() {
                return 8;
            }
        };

        EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;

        for (int i = 0; i < height; i++) {
            altarShapes[i] = BlockPatternBuilder.start()
                    .aisle(altarLevels[i][0], altarLevels[i][1], altarLevels[i][2])
                    .where('?', BlockInWorld.hasState(BlockStatePredicate.ANY))
                    .where('A', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.AIR)))
                    .where('b', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICKS)))
                    .where('B', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.CHISELED_STONE_BRICKS)))
                    .where('t', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SOUL_TORCH)))
                    .where('u', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_SLAB).where(TYPE, Predicates.equalTo(SlabType.TOP))))
                    .where('d', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_SLAB).where(TYPE, Predicates.equalTo(SlabType.BOTTOM))))
                    .where('w', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.STONE_BRICK_WALL)))
                    .where('f', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.FOOD_ALTAR_TIER1.get())))
                    .where('C', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.LOW_SUB_ALTAR_CULINARY.get()))).build();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", craftingProgress);
        tag.putInt("internalTicks", internalTicks);
        tag.putBoolean("isComplete", isFullAltarShape());
        tag.putInt("currentCulinary", currentCulinaryEssenceCost);
        tag.putInt("remainingCulinary", remainingCulinaryEssenceCost);
        tag.putFloat("currentOverflow", currentCulinaryEssenceOverflow);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        craftingProgress = nbt.getInt("progress");
        internalTicks = nbt.getInt("internalTicks");
        setFullAltarShape(nbt.getBoolean("isComplete"));
        currentCulinaryEssenceCost = nbt.getInt("currentCulinary");
        remainingCulinaryEssenceCost = nbt.getInt("remainingCulinary");
        currentCulinaryEssenceOverflow = nbt.getFloat("currentOverflow");
    }

    private void update() {
        // Check for complete Altar Shape
        for (int i = 0; i < altarShapes.length; i++) {
            if (level == null) {
                failedUpdate();
                return;
            }

            BlockPattern.BlockPatternMatch layerIsCorrect = altarShapes[i].find(level, worldPosition.offset(0, i - thisAltarHeight, 0));
            if (layerIsCorrect == null) {
                failedUpdate();
                return;
            }

            // Checks if this Block is actually the center Block of this layer
            if (!layerIsCorrect.getFrontTopLeft().offset(-blocksToCenter, 0, -blocksToCenter).equals(worldPosition.offset(0, i - thisAltarHeight, 0))) {
                failedUpdate();
                return;
            }
        }

        setFullAltarShape(true);

        // Cache SubAltarBlockEntity
        subAltarBlockEntity = ((SubAltarBlockEntity) (level.getBlockEntity(new BlockPos(worldPosition.offset(subAltarShift)))));
    }

    private void failedUpdate() {
        setFullAltarShape(false);
        subAltarBlockEntity = null;
    }

    public SubAltarBlockEntity getSubAltar() {
        return subAltarBlockEntity;
    }

    public int getTier() {
        return tier;
    }

    private int getSafeCulinaryEssence() {
        if (subAltarBlockEntity == null)
            return 0;

        return subAltarBlockEntity.getEssence();
    }

    /**
     * RECIPE STUFF
     */
    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier1BlockEntity entity) {
        entity.internalTicks++;
        if (entity.internalTicks % 20 == 0) {
            entity.internalTicks = 0;
            entity.update();
        }


        if (entity.isFullAltarShape() && checkOrCraftItem(entity, false)) {
            entity.craftingProgress++;

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

    private static boolean checkOrCraftItem(FoodAltarTier1BlockEntity entity, boolean craft) {
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
    }

    /**
     * Particles
     * */
    private static final int TIME_BETWEEN_PARTICLES = 10;

    public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, FoodAltarTier1BlockEntity pBlockEntity) {

    }


    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.culinary_wizardry.food_altar_tier1");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory playerInventory, Player pPlayer) {
        return new FoodAltarTier1Menu(pContainerId, playerInventory, this, this.data);
    }
}

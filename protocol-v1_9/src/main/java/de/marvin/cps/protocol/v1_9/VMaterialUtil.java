package de.marvin.cps.protocol.v1_9;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Version-specific utility class for handling {@link Material} related operations.
 */
public class VMaterialUtil {

    /**
     * Set of non-block materials that can be placed.
     */
    private static final @NotNull EnumSet<Material> PLACEABLE_NON_BLOCK_MATERIALS = EnumSet.of(
            Material.SIGN,
            Material.WOOD_DOOR,
            Material.IRON_DOOR,
            Material.CAKE,
            Material.BED,
            Material.DIODE,
            Material.REDSTONE_COMPARATOR,
            Material.BREWING_STAND_ITEM,
            Material.CAULDRON_ITEM,
            Material.FLOWER_POT_ITEM,
            Material.SKULL_ITEM,
            Material.BANNER,
            Material.SPRUCE_DOOR_ITEM,
            Material.BIRCH_DOOR_ITEM,
            Material.JUNGLE_DOOR_ITEM,
            Material.ACACIA_DOOR_ITEM,
            Material.DARK_OAK_DOOR_ITEM,
            Material.REDSTONE,
            Material.STRING,
            Material.SEEDS,
            Material.CARROT_ITEM,
            Material.PUMPKIN_SEEDS,
            Material.MELON_SEEDS,
            Material.NETHER_STALK,
            Material.SUGAR_CANE,
            Material.FLINT_AND_STEEL,
            Material.FIREWORK_CHARGE,
            Material.WOOD_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLD_HOE,
            Material.DIAMOND_HOE,
            Material.WOOD_SPADE,
            Material.STONE_SPADE,
            Material.IRON_SPADE,
            Material.GOLD_SPADE,
            Material.DIAMOND_SPADE
    );

    private static final @NotNull EnumSet<Material> INTERACTABLE_MATERIALS = EnumSet.of(
            Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST,
            Material.FURNACE, Material.BURNING_FURNACE,
            Material.WORKBENCH,
            Material.ENCHANTMENT_TABLE,
            Material.ANVIL,
            Material.BREWING_STAND,
            Material.HOPPER, Material.CAULDRON, Material.DROPPER, Material.DISPENSER,
            Material.BEACON,
            Material.JUKEBOX, Material.NOTE_BLOCK,
            Material.LEVER,
            Material.STONE_BUTTON, Material.WOOD_BUTTON,
            Material.WOOD_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR,
            Material.FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE,
            Material.FENCE, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.JUNGLE_FENCE, Material.ACACIA_FENCE, Material.DARK_OAK_FENCE,
            Material.TRAP_DOOR,
            Material.BED_BLOCK, Material.CAKE_BLOCK,
            Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON,
            Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON,
            Material.DAYLIGHT_DETECTOR, Material.DAYLIGHT_DETECTOR_INVERTED,
            Material.FLOWER_POT,
            Material.SIGN_POST, Material.WALL_SIGN
    );

    /**
     * Checks if the given {@link Material} is placeable, either through {@link Material#isBlock()} or, as
     * a non-block item that can be placed, through {@link VMaterialUtil#PLACEABLE_NON_BLOCK_MATERIALS}.
     *
     * @param material {@link Material} to check
     * @return {@code true} if the {@link Material} is placeable, {@code false} otherwise
     */
    public static boolean isPlaceable(
            @NotNull Material material
    ) {
        return (material.isBlock() && !material.equals(Material.AIR))
                || VMaterialUtil.isPlaceableNonBlock(material);
    }

    /**
     * Checks if the given {@link Material} is a non-block item that can be placed.
     *
     * @param material {@link Material} to check
     * @return {@code true} if the {@link Material} is a placeable non-block item, {@code false} otherwise
     */
    public static boolean isPlaceableNonBlock(
            @NotNull Material material
    ) {
        return PLACEABLE_NON_BLOCK_MATERIALS.contains(material);
    }

    /**
     * Checks if the given {@link Material} is interactable.
     *
     * @param material {@link Material} to check
     * @return {@code true} if the {@link Material} is interactable, {@code false} otherwise
     */
    public static boolean isInteractable(
            @NotNull Material material
    ) {
        return INTERACTABLE_MATERIALS.contains(material);
    }

    /**
     * Checks if the given {@link Material} is a minecart.
     *
     * @param material {@link Material} to check
     * @return {@code true} if the {@link Material} is a minecart, {@code false} otherwise
     */
    public static boolean isMinecart(
            @NotNull Material material
    ) {
        return material.equals(Material.MINECART) || material.equals(Material.EXPLOSIVE_MINECART)
                || material.equals(Material.HOPPER_MINECART) || material.equals(Material.POWERED_MINECART)
                || material.equals(Material.STORAGE_MINECART) || material.equals(Material.COMMAND_MINECART);
    }

    /**
     * Checks if the given {@link ItemStack} is bone meal.
     *
     * @param itemStack {@link ItemStack} to check
     * @return {@code true} if the {@link ItemStack} is bone meal, {@code false} otherwise
     */
    public static boolean isBoneMeal(
            @NotNull ItemStack itemStack
    ) {
        return itemStack.getType().equals(Material.INK_SACK) && itemStack.getDurability() == 15;
    }

    /**
     * Checks if the given {@link ItemStack} can be potted.
     *
     * @param itemStack {@link ItemStack} to check
     * @return {@code true} if the {@link ItemStack} can be potted, {@code false} otherwise
     */
    public static boolean canBePotted(
            @Nullable ItemStack itemStack
    ) {
        if (itemStack == null) return false;
        var type = itemStack.getType();
        var data = itemStack.getData() != null ? itemStack.getDurability() : 0;

        return switch (type) {
            case SAPLING -> true; // saplings
            case RED_ROSE, YELLOW_FLOWER -> true; // all flowers
            case RED_MUSHROOM, BROWN_MUSHROOM, CACTUS, DEAD_BUSH -> true;
            case LONG_GRASS -> data == 2; // fern
            default -> false;
        };
    }

}

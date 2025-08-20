package de.marvin.cps.core.util;

import de.marvin.cps.core.protocol.MinecraftVersion;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class for handling {@link Material} related operations.
 */
public class MaterialUtil {

    /**
     * {@link Set} of {@link Material Materials} that can be broken instantly.
     */
    private static final Set<Material> INSTANT_BREAKABLE = new HashSet<>();

    /**
     * Loads the instant breakable materials based on the provided
     * {@link MinecraftVersion}.
     *
     * @param version {@link MinecraftVersion} to load materials for
     */
    public static void load(MinecraftVersion version) {
        MaterialResolver add = names -> names.stream()
                .filter(data -> {
                    MinecraftVersion from = data.from();
                    MinecraftVersion to = data.to();
                    return (from == null || from.isOlderThanOrEquals(version)) &&
                            (to == null || to.isNewerThanOrEquals(version));
                })
                .map(data -> Material.getMaterial(data.name()))
                .filter(Objects::nonNull)
                .forEach(INSTANT_BREAKABLE::add);

        add.resolve(
                new MaterialData("SAPLING", null, MinecraftVersion.v1_12_2),
                new MaterialData("OAK_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("SPRUCE_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("BIRCH_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("JUNGLE_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("ACACIA_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("DARK_OAK_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("MANGROVE_PROPAGULE", MinecraftVersion.v1_19, null),
                new MaterialData("CHERRY_SAPLING", MinecraftVersion.v1_20, null),
                new MaterialData("PALE_OAK_SAPLING", MinecraftVersion.v1_21, null)
        );

        add.resolve(
                new MaterialData("LONG_GRASS", null, MinecraftVersion.v1_12_2),
                new MaterialData("GRASS", MinecraftVersion.v1_13, MinecraftVersion.v1_20_3),
                new MaterialData("SHORT_GRASS", MinecraftVersion.v1_20_4, null),
                new MaterialData("SHORT_DRY_GRASS", MinecraftVersion.v1_20_4, null),
                new MaterialData("SEAGRASS", MinecraftVersion.v1_13, null),
                new MaterialData("TALL_SEAGRASS", MinecraftVersion.v1_13, null),
                new MaterialData("FERN", MinecraftVersion.v1_13, null)
        );

        add.resolve(new MaterialData("DEAD_BUSH", null, null));

        add.resolve(new MaterialData("SEA_PICKLE", MinecraftVersion.v1_13, null));

        add.resolve(
                new MaterialData("YELLOW_FLOWER", null, MinecraftVersion.v1_12_2),
                new MaterialData("DANDELION", MinecraftVersion.v1_13, null)
        );

        add.resolve(
                new MaterialData("RED_ROSE", null, MinecraftVersion.v1_12_2),
                new MaterialData("POPPY", MinecraftVersion.v1_13, null),
                new MaterialData("BLUE_ORCHID", MinecraftVersion.v1_13, null),
                new MaterialData("ALLIUM", MinecraftVersion.v1_13, null),
                new MaterialData("AZURE_BLUET", MinecraftVersion.v1_13, null),
                new MaterialData("RED_TULIP", MinecraftVersion.v1_13, null),
                new MaterialData("ORANGE_TULIP", MinecraftVersion.v1_13, null),
                new MaterialData("WHITE_TULIP", MinecraftVersion.v1_13, null),
                new MaterialData("OXEYE_DAISY", MinecraftVersion.v1_13, null),
                new MaterialData("CORNFLOWER", MinecraftVersion.v1_14, null),
                new MaterialData("LILY_OF_THE_VALLEY", MinecraftVersion.v1_14, null)
        );

        add.resolve(new MaterialData("WITHER_ROSE", MinecraftVersion.v1_14, null));
        add.resolve(new MaterialData("SPORE_BLOSSOM", MinecraftVersion.v1_17, null));
        add.resolve(new MaterialData("BROWN_MUSHROOM", null, null));
        add.resolve(new MaterialData("RED_MUSHROOM", null, null));

        add.resolve(new MaterialData("CRIMSON_FUNGUS", MinecraftVersion.v1_16, null));
        add.resolve(new MaterialData("WARPED_FUNGUS", MinecraftVersion.v1_16, null));

        add.resolve(new MaterialData("CRIMSON_ROOTS", MinecraftVersion.v1_16, null));
        add.resolve(new MaterialData("WARPED_ROOTS", MinecraftVersion.v1_16, null));

        add.resolve(new MaterialData("NETHER_SPROUTS", MinecraftVersion.v1_16, null));

        add.resolve(new MaterialData("WEEPING_VINES", MinecraftVersion.v1_16, null));
        add.resolve(new MaterialData("WEEPING_VINES_PLANT", MinecraftVersion.v1_16, null));
        add.resolve(new MaterialData("TWISTING_VINES", MinecraftVersion.v1_16, null));
        add.resolve(new MaterialData("TWISTING_VINES_PLANT", MinecraftVersion.v1_16, null));

        add.resolve(new MaterialData("SUGAR_CANE", null, null));

        add.resolve(new MaterialData("KELP", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("KELP_PLANT", MinecraftVersion.v1_13, null));

        add.resolve(new MaterialData("HANGING_ROOTS", MinecraftVersion.v1_17, null));
        add.resolve(new MaterialData("SMALL_DRIPLEAF", MinecraftVersion.v1_17, null));

        add.resolve(new MaterialData("TORCH", MinecraftVersion.v1_8_8, null));
        add.resolve(new MaterialData("WALL_TORCH", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("SOUL_TORCH", MinecraftVersion.v1_16, null));
        add.resolve(new MaterialData("SOUL_WALL_TORCH", MinecraftVersion.v1_16, null));

        add.resolve(new MaterialData("END_ROD", MinecraftVersion.v1_9, null));

        add.resolve(
                new MaterialData("WATER_LILY", null, MinecraftVersion.v1_12_2),
                new MaterialData("LILY_PAD", MinecraftVersion.v1_13, null)
        );

        add.resolve(
                new MaterialData("DOUBLE_PLANT", null, MinecraftVersion.v1_12_2),
                new MaterialData("SUNFLOWER", MinecraftVersion.v1_13, null),
                new MaterialData("LILAC", MinecraftVersion.v1_13, null),
                new MaterialData("TALL_GRASS", MinecraftVersion.v1_13, null),
                new MaterialData("LARGE_FERN", MinecraftVersion.v1_13, null),
                new MaterialData("ROSE_BUSH", MinecraftVersion.v1_13, null),
                new MaterialData("PEONY", MinecraftVersion.v1_13, null)
        );

        add.resolve(new MaterialData("SWEET_BERRY_BUSH", MinecraftVersion.v1_14, null));
        add.resolve(new MaterialData("FIREFLY_BUSH", MinecraftVersion.v1_21_5, null));
        add.resolve(new MaterialData("CACTUS_FLOWER", MinecraftVersion.v1_21_5, null));

        add.resolve(new MaterialData("TUBE_CORAL", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("BRAIN_CORAL", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("BUBBLE_CORAL", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("FIRE_CORAL", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("HORN_CORAL", MinecraftVersion.v1_13, null));

        add.resolve(new MaterialData("DEAD_BRAIN_CORAL", MinecraftVersion.v1_13_1, null));
        add.resolve(new MaterialData("DEAD_BUBBLE_CORAL", MinecraftVersion.v1_13_1, null));
        add.resolve(new MaterialData("DEAD_FIRE_CORAL", MinecraftVersion.v1_13_1, null));
        add.resolve(new MaterialData("DEAD_HORN_CORAL", MinecraftVersion.v1_13_1, null));
        add.resolve(new MaterialData("DEAD_TUBE_CORAL", MinecraftVersion.v1_13_1, null));

        add.resolve(new MaterialData("TUBE_CORAL_FAN", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("BRAIN_CORAL_FAN", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("BUBBLE_CORAL_FAN", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("FIRE_CORAL_FAN", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("HORN_CORAL_FAN", MinecraftVersion.v1_13, null));

        add.resolve(new MaterialData("DEAD_TUBE_CORAL_FAN", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("DEAD_BRAIN_CORAL_FAN", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("DEAD_BUBBLE_CORAL_FAN", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("DEAD_FIRE_CORAL_FAN", MinecraftVersion.v1_13, null));
        add.resolve(new MaterialData("DEAD_HORN_CORAL_FAN", MinecraftVersion.v1_13, null));

        add.resolve(new MaterialData("SCAFFOLDING", MinecraftVersion.v1_14, null));

        add.resolve(
                new MaterialData("FLOWER_POT", null, null),
                new MaterialData("POTTED_DANDELION", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_POPPY", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_ALLIUM", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_AZURE_BLUET", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_OXEYE_DAISY", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_BLUE_ORCHID", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_RED_TULIP", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_ORANGE_TULIP", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_WHITE_TULIP", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_PINK_TULIP", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_RED_MUSHROOM", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_BROWN_MUSHROOM", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_OAK_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_BIRCH_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_SPRUCE_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_JUNGLE_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_ACACIA_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_DARK_OAK_SAPLING", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_CACTUS", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_FERN", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_DEAD_BUSH", MinecraftVersion.v1_13, null),
                new MaterialData("POTTED_CORNFLOWER", MinecraftVersion.v1_14, null),
                new MaterialData("POTTED_LILY_OF_THE_VALLEY", MinecraftVersion.v1_14, null),
                new MaterialData("POTTED_WITHER_ROSE", MinecraftVersion.v1_14, null),
                new MaterialData("POTTED_BAMBOO", MinecraftVersion.v1_14, null),
                new MaterialData("POTTED_CRIMSON_FUNGUS", MinecraftVersion.v1_16, null),
                new MaterialData("POTTED_WARPED_FUNGUS", MinecraftVersion.v1_16, null),
                new MaterialData("POTTED_CRIMSON_ROOTS", MinecraftVersion.v1_16, null),
                new MaterialData("POTTED_WARPED_ROOTS", MinecraftVersion.v1_16, null),
                new MaterialData("POTTED_AZALEA_BUSH", MinecraftVersion.v1_17, null),
                new MaterialData("POTTED_FLOWERING_AZALEA_BUSH", MinecraftVersion.v1_17, null),
                new MaterialData("POTTED_MANGROVE_PROPAGULE", MinecraftVersion.v1_19, null),
                new MaterialData("POTTED_TORCHFLOWER", MinecraftVersion.v1_19_4, null),
                new MaterialData("POTTED_CHERRY_SAPLING", MinecraftVersion.v1_19_4, null),
                new MaterialData("POTTED_PALE_OAK_SAPLING", MinecraftVersion.v1_21_2, null),
                new MaterialData("POTTED_CLOSED_EYEBLOSSOM", MinecraftVersion.v1_21_4, null),
                new MaterialData("POTTED_OPEN_EYEBLOSSOM", MinecraftVersion.v1_21_4, null)
        );

        add.resolve(new MaterialData("PALE_HANGING_MOSS", MinecraftVersion.v1_21_2, null));

        add.resolve(new MaterialData("LEAF_LITTER", MinecraftVersion.v1_21_5, null));
        add.resolve(new MaterialData("WILDFLOWERS", MinecraftVersion.v1_21_5, null));

        add.resolve(new MaterialData("WHEAT", null, null));
        add.resolve(new MaterialData("CARROTS", null, null));
        add.resolve(new MaterialData("POTATOES", null, null));
        add.resolve(new MaterialData("NETHER_WART", null, null));
        add.resolve(new MaterialData("BEETROOTS", MinecraftVersion.v1_9, null));

        add.resolve(new MaterialData("REDSTONE_WIRE", null, null));

        add.resolve(
                new MaterialData("REDSTONE_TORCH", null, null),
                new MaterialData("REDSTONE_TORCH_ON", null, MinecraftVersion.v1_12_2),
                new MaterialData("REDSTONE_TORCH_OFF", null, MinecraftVersion.v1_12_2),
                new MaterialData("REDSTONE_WALL_TORCH", MinecraftVersion.v1_13, null)
        );

        add.resolve(new MaterialData("LEVER", null, null));

        add.resolve(new MaterialData("TRIPWIRE_HOOK", null, null));
        add.resolve(new MaterialData("TRIPWIRE", null, null));

        add.resolve(
                new MaterialData("DIODE", null, MinecraftVersion.v1_12_2),
                new MaterialData("DIODE_BLOCK_OFF", null, MinecraftVersion.v1_12_2),
                new MaterialData("DIODE_BLOCK_ON", null, MinecraftVersion.v1_12_2),
                new MaterialData("REPEATER", MinecraftVersion.v1_13, null)
        );

        add.resolve(
                new MaterialData("REDSTONE_COMPARATOR", null, MinecraftVersion.v1_12_2),
                new MaterialData("REDSTONE_COMPARATOR_OFF", null, MinecraftVersion.v1_12_2),
                new MaterialData("REDSTONE_COMPARATOR_ON", null, MinecraftVersion.v1_12_2),
                new MaterialData("COMPARATOR", MinecraftVersion.v1_13, null)
        );
    }

    /**
     * Checks if the given {@link Material} can be broken instantly.
     *
     * @param material {@link Material} to check
     * @return {@code true} if the material can be broken instantly,
     *         {@code false} otherwise.
     */
    public static boolean isInstantBreakable(
            @NotNull final Material material
    ) {
        return INSTANT_BREAKABLE.contains(material);
    }

    /**
     * Represents a material name and {@link MinecraftVersion} range in which it is
     * available.
     *
     * @param name name of the material
     * @param from first {@link MinecraftVersion} from which the material is available,
     *             or {@code null} if it is available from the earliest version known by
     *             this plugin
     * @param to   latest {@link MinecraftVersion} to which the material is available,
     *             or {@code null} if it is available till the newest version known by
     *             this plugin
     */
    private record MaterialData(
            @NotNull String name,
            @Nullable MinecraftVersion from,
            @Nullable MinecraftVersion to
    ) {
    }

    /**
     * Functional interface used for resolving material names
     * (as strings) into {@link Material} instances and registering
     * them into the {@link MaterialUtil#INSTANT_BREAKABLE} set.
     */
    @FunctionalInterface
    private interface MaterialResolver {
        void resolve(List<MaterialData> data);

        default void resolve(MaterialData... names) {
            this.resolve(List.of(names));
        }
    }

}

package de.marvin.cps.util;

import org.bukkit.Material;

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

    static {
        MaterialResolver add = names -> names.stream()
                .map(Material::getMaterial)
                .filter(Objects::nonNull)
                .forEach(INSTANT_BREAKABLE::add);

        add.resolve("SAPLING", "OAK_SAPLING", "SPRUCE_SAPLING", "BIRCH_SAPLING", "JUNGLE_SAPLING", "ACACIA_SAPLING", "DARK_OAK_SAPLING", "MANGROVE_PROPAGULE", "CHERRY_SAPLING", "PALE_OAK_SAPLING");
        add.resolve("LONG_GRASS", "TALL_GRASS");
        add.resolve("DEAD_BUSH");
        add.resolve("YELLOW_FLOWER", "DANDELION");
        add.resolve("RED_ROSE", "POPPY");
        add.resolve("BROWN_MUSHROOM");
        add.resolve("RED_MUSHROOM");
        add.resolve("TORCH");
        add.resolve("WATER_LILY", "LILY_PAD");

        add.resolve("DOUBLE_PLANT", "SUNFLOWER", "LILAC", "ROSE_BUSH", "PEONY", "LARGE_FERN");
        add.resolve("PALE_HANGING_MOSS");

        add.resolve("LEVER");
        add.resolve("REDSTONE", "REDSTONE_WIRE");
        add.resolve("REDSTONE_TORCH_OFF", "REDSTONE_TORCH_ON", "REDSTONE_TORCH");
        add.resolve("TRIPWIRE_HOOK");
        add.resolve("TRIPWIRE");
        add.resolve("DIODE_BLOCK_OFF", "DIODE_BLOCK_ON", "DIODE", "REPEATER");
        add.resolve("REDSTONE_COMPARATOR_OFF", "REDSTONE_COMPARATOR_ON", "REDSTONE_COMPARATOR", "COMPARATOR");
    }

    /**
     * Checks if the given {@link Material} can be broken instantly.
     *
     * @param material {@link Material} to check
     * @return {@code true} if the material can be broken instantly, {@code false} otherwise.
     */
    public static boolean isInstantBreakable(Material material) {
        return INSTANT_BREAKABLE.contains(material);
    }

    /**
     * Functional interface used for resolving material names
     * (as strings) into {@link Material} instances and registering
     * them into the {@link MaterialUtil#INSTANT_BREAKABLE} set.
     */
    @FunctionalInterface
    private interface MaterialResolver {
        void resolve(List<String> names);

        default void resolve(String... names) {
            this.resolve(List.of(names));
        }
    }

}

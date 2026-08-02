package de.marvin.cps.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Utility class for interacting with {@link UUID UUIDs}.
 */
public final class UniqueIdUtil {

    /**
     * Do not instantiate this class. It is a utility class and should only be used statically.
     */
    private UniqueIdUtil() {
        throw new AssertionError("Utility classes cannot be instantiated.");
    }

    /**
     * Checks if the given input is a valid {@link UUID}.
     *
     * @param input Input to check
     * @return {@code true} if the input is a valid {@link UUID},
     * {@code false} otherwise.
     */
    public static boolean isUniqueId(
            @NotNull final String input
    ) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

}

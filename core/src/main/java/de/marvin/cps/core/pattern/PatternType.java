package de.marvin.cps.core.pattern;

import de.marvin.cps.core.pattern.types.BasicPattern;
import de.marvin.cps.core.pattern.types.HistoryPattern;
import de.marvin.cps.core.pattern.types.StreakPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Represents the different available {@link AbstractPattern} types.
 */
public enum PatternType {

    /**
     * Represents the {@link BasicPattern} type.
     */
    BASIC(new BasicPattern()),
    /**
     * Represents the {@link HistoryPattern} type.
     */
    HISTORY(new HistoryPattern()),
    /**
     * Represents the {@link StreakPattern} type.
     */
    STREAK(new StreakPattern());

    /**
     * Corresponding {@link AbstractPattern} of the respective {@link PatternType}.
     */
    private final @NotNull AbstractPattern pattern;

    PatternType(
            @NotNull AbstractPattern pattern
    ) {
        this.pattern = pattern;
    }

    /**
     * Returns the {@link AbstractPattern} instance of this {@link PatternType}.
     *
     * @return {@link AbstractPattern} instance of this {@link PatternType}
     */
    public @NotNull AbstractPattern pattern() {
        return this.pattern;
    }

    /**
     * Returns the {@link PatternType} by its {@link PatternType#name()}.
     *
     * @param type Name of {@link PatternType} to return
     * @return {@link PatternType} based on given name or {@code null} if no match was found
     */
    public static @Nullable PatternType fromString(
            @NotNull String type
    ) {
        return Arrays.stream(values())
                .filter(monitorMode -> monitorMode.name().equalsIgnoreCase(type))
                .findAny()
                .orElse(null);
    }

    /**
     * Returns the next {@link PatternType} in the enum order, wrapping around to the first if at the end.
     *
     * @param pattern The current {@link PatternType} to get the next of
     * @return The next {@link PatternType} in the enum order
     */
    public static @NotNull PatternType next(
            @NotNull PatternType pattern
    ) {
        var ordinal = pattern.ordinal();
        if (ordinal >= values().length - 1) return values()[0];
        return values()[ordinal + 1];
    }

}


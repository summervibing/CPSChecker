package de.marvin.cps.core.pattern;

import de.marvin.cps.core.pattern.types.BasicPattern;
import de.marvin.cps.core.pattern.types.HistoryPattern;
import de.marvin.cps.core.pattern.types.StreakPattern;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum PatternType {

    BASIC(new BasicPattern()),
    HISTORY(new HistoryPattern()),
    STREAK(new StreakPattern());

    private final AbstractPattern pattern;

    PatternType(
            @NotNull final AbstractPattern pattern
    ) {
        this.pattern = pattern;
    }

    /**
     * Gets the {@link AbstractPattern} instance
     * of this {@link PatternType}.
     *
     * @return {@link AbstractPattern} instance.
     */
    public AbstractPattern pattern() {
        return this.pattern;
    }

    /**
     * Gets {@link PatternType} by its {@link PatternType#name()}.
     *
     * @param type name of {@link PatternType} to get
     * @return {@link PatternType} based on given name.
     */
    public static PatternType fromString(
            @NotNull final String type
    ) {
        return Arrays.stream(values())
                .filter(monitorMode -> monitorMode.name().equalsIgnoreCase(type))
                .findAny()
                .orElse(null);
    }

    public static PatternType next(
            @NotNull final PatternType pattern
    ) {
        var ordinal = pattern.ordinal();
        if (ordinal >= values().length - 1) return values()[0];
        return values()[ordinal + 1];
    }

}


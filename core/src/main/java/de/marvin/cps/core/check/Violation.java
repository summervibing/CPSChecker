package de.marvin.cps.core.check;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a violation that has been detected during a check.
 */
public class Violation {

    /**
     * The {@link ViolationType Type} of this {@link Violation}.
     */
    private final @NotNull ViolationType type;

    public Violation(
            @NotNull ViolationType type
    ) {
        this.type = type;
    }

    /**
     * Returns the {@link ViolationType} of this {@link Violation}.
     *
     * @return The {@link ViolationType} of this {@link Violation}
     */
    public @NotNull ViolationType type() {
        return this.type;
    }

}

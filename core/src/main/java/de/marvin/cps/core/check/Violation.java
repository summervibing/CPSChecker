package de.marvin.cps.core.check;

import org.jetbrains.annotations.NotNull;

public class Violation {

    private final ViolationType type;

    public Violation(
            @NotNull final ViolationType type
    ) {
        this.type = type;
    }

    /**
     * Gets the {@link ViolationType} of this {@link Violation}.
     *
     * @return The {@link ViolationType} of this {@link Violation}.
     */
    public ViolationType type() {
        return this.type;
    }

}

package de.marvin.cps.core.pattern;

import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.monitor.Monitor;
import de.marvin.cps.core.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the displayed {@link AbstractPattern Pattern} of a {@link User} in a {@link Monitor}.
 */
public abstract class AbstractPattern {

    /**
     * {@link Monitor} format of this {@link AbstractPattern Pattern}.
     */
    private final @NotNull Message format;
    /**
     * Explanation of this {@link AbstractPattern Pattern}.
     */
    private final @NotNull String explanation;

    protected AbstractPattern(
            @NotNull Message format,
            @NotNull String explanation
    ) {
        this.format = format;
        this.explanation = explanation;
    }

    /**
     * Prints the {@link AbstractPattern Pattern} based on the given ticks and current index of an
     * {@link User}.
     *
     * @param ticks        Array of {@link AbstractTick} to print the current pattern of
     * @param currentIndex Current index in the tick array
     * @return A string representation of the current {@link AbstractPattern Pattern}
     */
    public abstract @NotNull String print(
            @NotNull AbstractTick[] ticks,
            int currentIndex
    );

    /**
     * Returns {@link Monitor} format of this {@link AbstractPattern Pattern}.
     *
     * @return {@link Monitor} format of this {@link AbstractPattern Pattern}
     */
    public @NotNull Message format() {
        return this.format;
    }

    /**
     * Returns explanation of the {@link AbstractPattern Pattern}
     *
     * @return A string explaining the {@link AbstractPattern Pattern}
     */
    public @NotNull String explanation() {
        return this.explanation;
    }

}

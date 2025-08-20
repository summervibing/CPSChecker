package de.marvin.cps.core.pattern;

import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.user.User;
import de.marvin.cps.core.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPattern {

    private final Message format;
    private final String explanation;

    protected AbstractPattern(Message format, String explanation) {
        this.format = format;
        this.explanation = explanation;
    }

    /**
     * Prints the pattern based on the given ticks and current
     * index of an {@link User}.
     *
     * @param ticks        Array of {@link AbstractTick} to print
     *                     the current pattern of
     * @param currentIndex Current index in the tick array
     * @return A string representation of the current pattern.
     */
    public abstract String print(
            @NotNull final AbstractTick[] ticks,
            final int currentIndex
    );

    /**
     * {@link Monitor} format of this pattern.
     *
     * @return {@link Monitor} format of this pattern.
     */
    public Message format() {
        return this.format;
    }

    /**
     * Gets explanation of the pattern.
     *
     * @return A string explaining the pattern.
     */
    public String explanation() {
        return this.explanation;
    }

}

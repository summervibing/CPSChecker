package de.marvin.cps.core.pattern.types;

import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.message.Message;
import de.marvin.cps.core.pattern.AbstractPattern;
import org.jetbrains.annotations.NotNull;

public class BasicPattern extends AbstractPattern {

    public BasicPattern() {
        super(
                Message.MONITOR_BASIC,
                ""
        );
    }

    /**
     * Prints no pattern since {@link BasicPattern} just
     * displays the current clicks per second.
     *
     * @param ticks        Array of {@link AbstractTick} to print
     *                     the current pattern of
     * @param currentIndex Current index in the tick array
     * @return An empty string, since no pattern is printed.
     */
    @Override
    public String print(
            @NotNull AbstractTick[] ticks,
            final int currentIndex
    ) {
        return "";
    }

}

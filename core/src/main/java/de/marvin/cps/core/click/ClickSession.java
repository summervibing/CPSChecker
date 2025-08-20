package de.marvin.cps.core.click;

import de.marvin.cps.core.CPSChecker;
import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.click.tick.types.LeftClickTick;
import de.marvin.cps.core.click.tick.types.RightClickTick;
import de.marvin.cps.core.pattern.AbstractPattern;
import de.marvin.cps.core.pattern.PatternType;
import de.marvin.cps.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ClickSession {

    private static int patternSize = 100; // In ticks
    private static int displaySize = 40; // In ticks

    private final LeftClickTick[] leftClickTicks = new LeftClickTick[ClickSession.patternSize];
    private final RightClickTick[] rightClickTicks = new RightClickTick[ClickSession.patternSize];

    private int currentIndex = 0;

    /**
     * Creates a new {@link ClickSession} instance with a fixed size
     * of {@link ClickSession#patternSize} ticks.
     */
    public ClickSession() {
        for (int i = 0; i < ClickSession.patternSize; i++) this.updateTickAt(i);
    }

    /**
     * Moves the tick array window forward by one {@link AbstractTick Tick}.
     * <p>
     * <b>Note:</b> Needs to be called every tick to update the patterns.
     */
    public void nextTick() {
        this.currentIndex = (this.currentIndex + 1) % ClickSession.patternSize;
        // Reset the current tick
        this.updateTickAt(this.currentIndex);
    }

    /**
     * Adds a click to the current {@link AbstractTick Tick}.
     *
     * @param type {@link ClickType} of click to register
     */
    public void registerClick(
            @NotNull final ClickType type
    ) {
        switch (type) {
            case LEFT_CLICK -> this.leftClickTicks[this.currentIndex].addClick();
            case INVALID_LEFT_CLICK -> this.leftClickTicks[this.currentIndex].addInvalidClick();
            case ATTACK -> this.leftClickTicks[this.currentIndex].addAttack();

            case RIGHT_CLICK -> this.rightClickTicks[this.currentIndex].addClick();
            case PLACEMENT -> this.rightClickTicks[this.currentIndex].addPlacement();
        }
    }

    /**
     * Calculates the clicks per second in the last 20
     * {@link AbstractTick Ticks}.
     *
     * @param type {@link ClickType} to calculate clicks per second for
     * @return Number of clicks per second in the last 20 ticks.
     */
    public int clicksPerSecond(
            @NotNull final ClickType type
    ) {
        var totalClicks = 0;

        for (int i = 0; i < 20; i++) {
            var index = ClickSession.indexFrom(this.currentIndex, i);
            var tick = type.isLeftClick() ? this.leftClickTicks[index] : this.rightClickTicks[index];
            switch (type) {
                case LEFT_CLICK, RIGHT_CLICK -> totalClicks += tick.clicks();
                case INVALID_LEFT_CLICK -> totalClicks += ((LeftClickTick) tick).invalidClicks();
                case ATTACK -> totalClicks += ((LeftClickTick) tick).attacks();
                case PLACEMENT -> totalClicks += ((RightClickTick) tick).placements();
            }
        }

        return totalClicks;
    }

    /**
     * Prints given {@link AbstractPattern Pattern} for given {@link ClickType}.
     *
     * @param patternType {@link PatternType} to print
     * @param clickType   {@link ClickType#LEFT_CLICK} for pattern of left clicks,
     *                    {@link ClickType#RIGHT_CLICK} for pattern of right clicks.
     * @return {@link AbstractPattern Pattern} as a {@link String}.
     */
    public String printPattern(
            @NotNull final ClickType clickType,
            @NotNull final PatternType patternType
    ) {
        return patternType.pattern().print(
                clickType.isLeftClick() ? this.leftClickTicks : this.rightClickTicks,
                this.currentIndex
        );
    }

    // Helper methods

    /**
     * Updates {@link AbstractTick Tick} at given position in
     * {@link ClickSession#leftClickTicks} and {@link ClickSession#rightClickTicks}
     * arrays.
     *
     * @param position Position of arrays to update the {@link AbstractTick} at
     */
    private void updateTickAt(
            final int position
    ) {
        this.leftClickTicks[position] = new LeftClickTick();
        this.rightClickTicks[position] = new RightClickTick();
    }

    /**
     * Configures the cached {@link ClickSession#patternSize} and
     * {@link ClickSession#displaySize} of {@link AbstractPattern Patterns}.
     * <p>
     * If the given values are invalid, default values are used.
     *
     * @param patternSize Size of the pattern in ticks (default: 100)
     * @param displaySize Display size of the pattern in ticks (default: 40)
     */
    public static void configure(
            final int patternSize,
            final int displaySize
    ) {
        if (patternSize < 20 || patternSize > 1200) {
            CPSChecker.instance().log(
                    Level.WARNING,
                    "Invalid pattern size '" + patternSize + "' given in configuration. "
                            + "Only values between 20-1200 are allowed. Using default size of "
                            + ClickSession.patternSize + " ticks."
            );
            return;
        }
        if (displaySize < 10 || displaySize > 60) {
            CPSChecker.instance().log(
                    Level.WARNING,
                    "Invalid pattern display size '" + displaySize + "' given in configuration. "
                            + "Only values between 10-60 are allowed. Using default display size of "
                            + ClickSession.displaySize + " ticks."
            );
            return;
        }
        if (displaySize > patternSize) {
            CPSChecker.instance().log(
                    Level.WARNING,
                    "Pattern display size '" + displaySize + "' given in configuration exceeds pattern "
                            + "cache size '" + patternSize + "'. " + "Using default display size of "
                            + ClickSession.displaySize + " ticks."
            );
            return;
        }

        // Update static fields
        ClickSession.patternSize = patternSize;
        ClickSession.displaySize = displaySize;
    }

    /**
     * Gets the {@link ClickSession#patternSize} of tick arrays.
     *
     * @return {@link ClickSession#patternSize} of a tick arrays.
     */
    public static int patternSize() {
        return ClickSession.patternSize;
    }

    /**
     * Gets the {@link ClickSession#displaySize} of a {@link AbstractPattern} in ticks.
     *
     * @return {@link ClickSession#displaySize} of a {@link AbstractPattern} in ticks.
     */
    public static int displaySize() {
        return ClickSession.displaySize;
    }

    /**
     * Gets relative index of {@link AbstractTick} in {@link ClickSession}
     * based tick array on the given position in the {@link AbstractPattern}.
     *
     * @param currentIndex Current index in tick array of {@link User}
     * @return Index in {@link ClickSession} based tick array on given position.
     */
    public static int indexFrom(
            final int currentIndex,
            final int position
    ) {
        return (currentIndex - position + ClickSession.patternSize()) % ClickSession.patternSize();
    }

}

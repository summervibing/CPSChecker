package de.marvin.cps.core.monitor;

import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.pattern.AbstractPattern;
import de.marvin.cps.core.pattern.PatternType;
import de.marvin.cps.core.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link AbstractPattern Pattern} {@link Monitor} of
 * a {@link User} in the given {@link ClickType} and {@link PatternType}.
 */
public class Monitor {

    private final User user;
    private ClickType clickType;
    private PatternType patternType;
    private boolean paused = false;

    public Monitor(
            @NotNull final User user
    ) {
        this.user = user;
        this.clickType = ClickType.LEFT_CLICK;
        this.patternType = PatternType.BASIC;
    }

    public Monitor(
            @NotNull final User user,
            @NotNull final PatternType patternType
    ) {
        this.user = user;
        this.clickType = ClickType.LEFT_CLICK;
        this.patternType = patternType;
    }

    public Monitor(
            @NotNull final User user,
            @NotNull final ClickType clickType,
            @NotNull final PatternType patternType
    ) {
        this.user = user;
        this.clickType = clickType;
        this.patternType = patternType;
    }

    /**
     * Gets the {@link User} that is being monitored.
     *
     * @return Monitored {@link User}.
     */
    public User user() {
        return this.user;
    }

    /**
     * Gets current {@link ClickType} of the monitor.
     *
     * @return Current {@link ClickType} of the monitor.
     */
    public ClickType clickType() {
        return this.clickType;
    }

    /**
     * Gets current {@link PatternType} of the monitor.
     *
     * @return Current {@link PatternType} of the monitor.
     */
    public PatternType patternType() {
        return this.patternType;
    }

    /**
     * Gets current {@link AbstractPattern Pattern} of the monitor.
     *
     * @return Current {@link PatternType} of the monitor.
     */
    public AbstractPattern pattern() {
        return this.patternType.pattern();
    }

    /**
     * Prints the {@link AbstractPattern Pattern} of the monitor in the
     * format of the {@link PatternType} for the given {@link ClickType}.
     *
     * @return Formatted {@link String} representation of the {@link AbstractPattern}.
     */
    public String printPattern() {
        return this.user.clickSession().printPattern(this.clickType, this.patternType);
    }

    /**
     * Checks if the monitor is paused.
     *
     * @return {@code true} if the monitor is paused, otherwise {@code false}.
     */
    public boolean isPaused() {
        return this.paused;
    }

    /**
     * Sets the {@link ClickType} of the monitor.
     *
     * @param clickType {@link ClickType} to set
     */
    public void setClickType(
            @NotNull final ClickType clickType
    ) {
        if (this.paused) return;
        this.clickType = clickType.isLeftClick()
                ? ClickType.LEFT_CLICK : ClickType.RIGHT_CLICK;
    }

    /**
     * Switches the {@link ClickType}.
     *
     * @return {@link ClickType} the {@link Monitor} switched to.
     */
    public ClickType nextClickType() {
        if (this.paused) return this.clickType;
        return this.clickType = this.clickType.isLeftClick()
                ? ClickType.RIGHT_CLICK : ClickType.LEFT_CLICK;
    }

    /**
     * Sets the {@link PatternType} of the monitor.
     *
     * @param patternType {@link PatternType} to set
     */
    public void setPatternType(
            @NotNull final PatternType patternType
    ) {
        this.patternType = patternType;
    }

    /**
     * Switches to next {@link PatternType}.
     * <p>
     * <b>Note:</b> The order is determined by the
     * {@link PatternType#ordinal()} of the modes.
     *
     * @return The {@link PatternType} the {@link Monitor} switched to.
     */
    public PatternType nextPatternType() {
        var next = PatternType.next(this.patternType);
        this.setPatternType(next);
        return next;
    }

    /**
     * Sets the paused state of the monitor.
     *
     * @param paused {@code true} to pause the monitor, otherwise {@code false}.
     */
    public void setPaused(
            final boolean paused
    ) {
        this.paused = paused;
    }

}

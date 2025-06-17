package de.marvin.cps.monitor;

import de.marvin.cps.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a pattern {@link Monitor} of
 * a {@link User} in the given {@link MonitorMode}.
 */
public class Monitor {

    private final User user;
    private MonitorMode mode;
    private boolean paused = false;

    public Monitor(
            @NotNull final User user
    ) {
        this.user = user;
        this.mode = MonitorMode.BASIC;
    }

    public Monitor(
            @NotNull final User user,
            @NotNull final MonitorMode mode
    ) {
        this.user = user;
        this.mode = mode;
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
     * Gets the current {@link MonitorMode} of the monitor.
     *
     * @return Current {@link MonitorMode} of the monitor.
     */
    public MonitorMode mode() {
        return this.mode;
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
     * Sets the {@link MonitorMode} of the monitor.
     *
     * @param mode {@link MonitorMode} to set
     */
    public void setMode(
            @NotNull final MonitorMode mode
    ) {
        this.mode = mode;
    }

    /**
     * Switches to next {@link MonitorMode}.
     * <p>
     * <b>Note:</b> The order is determined by the
     * {@link MonitorMode#ordinal()} of the modes.
     *
     * @return The {@link MonitorMode} the {@link Monitor} switched to.
     */
    public MonitorMode nextMode() {
        var next = MonitorMode.next(this.mode);
        this.setMode(next);
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

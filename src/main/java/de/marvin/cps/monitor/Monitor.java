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

    public Monitor(
            @NotNull final User user
    ) {
        this.user = user;
        this.mode = MonitorMode.BASIC;
    }

    public Monitor(
            @NotNull final User user,
            @NotNull MonitorMode mode
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
     */
    public void nextMode() {
        this.setMode(MonitorMode.next(this.mode));
    }

}

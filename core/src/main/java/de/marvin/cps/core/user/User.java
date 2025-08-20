package de.marvin.cps.core.user;

import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.click.tick.AbstractTick;
import de.marvin.cps.core.pattern.AbstractPattern;
import de.marvin.cps.core.check.Violation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private final String name;
    private final UUID uniqueId;
    private boolean isOnline = true;

    private ClickSession clickSession;
    private final List<Violation> violations;

    public User(
            @NotNull final String name,
            @NotNull final UUID uniqueId
    ) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.clickSession = new ClickSession();
        this.violations = new ArrayList<>();
    }

    /**
     * Gets the name of the user.
     *
     * @return Name of the user.
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the {@link UUID} of the user.
     *
     * @return {@link UUID} of the user.
     */
    public UUID uniqueId() {
        return this.uniqueId;
    }

    /**
     * Checks if the user is currently online.
     *
     * @return {@code true} if the user is online, {@code false} otherwise.
     */
    public boolean isOnline() {
        return this.isOnline;
    }

    /**
     * Gets the current {@link ClickSession} of the user.
     *
     * @return Current {@link ClickSession} of the user.
     */
    public ClickSession clickSession() {
        return this.clickSession;
    }

    /**
     * Gets the clicks per second of the last 20 {@link AbstractTick Ticks}
     * of the current {@link AbstractPattern Pattern}.
     *
     * @param type {@link ClickType} to get the clicks per second for
     * @return Clicks per second of the current {@link AbstractPattern Pattern}.
     */
    public double clicksPerSecond(
            @NotNull final ClickType type
    ) {
        return this.clickSession.clicksPerSecond(type);
    }

    /**
     * Gets the list of {@link Violation Violations}
     * the user received.
     *
     * @return List of {@link Violation Violations}.
     */
    public List<Violation> violations() {
        return this.violations;
    }

    /**
     * Sets the {@link User#isOnline} status of the user.
     *
     * @param isOnline {@code true} if the user is online, {@code false} otherwise.
     */
    public void setOnline(
            final boolean isOnline
    ) {
        this.isOnline = isOnline;
        if (!isOnline) this.resetClickSession();
    }

    /**
     * Updates the current {@link ClickSession} of the {@link User}.
     * <p>
     * <b>Note:</b> This method should be called on every
     * tick for patterns to work correctly.
     */
    public void updateClickSession() {
        if (this.clickSession == null) return;
        this.clickSession.nextTick();
    }

    /**
     * Resets the current {@link ClickSession} of the {@link User}.
     */
    public void resetClickSession() {
        this.clickSession = new ClickSession();
    }

    /**
     * Adds a {@link Violation} to the {@link User}'s list of violations.
     *
     * @param violation {@link Violation} to add
     */
    public void addViolation(
            @NotNull final Violation violation
    ) {
        this.violations.add(violation);
    }

    /**
     * Clears {@link User}'s {@link User#violations}.
     */
    public void resetViolations() {
        this.violations.clear();
        this.resetClickSession();
    }


}

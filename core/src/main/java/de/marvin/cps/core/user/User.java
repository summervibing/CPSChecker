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

/**
 * Represents a {@link User} object.
 * <p>
 * Objects of the class hold user-specific data such as the {@link User#name username}, their {@link UUID},
 * the {@link ClickSession} and {@link Violation Violations}. They also provide methods to manage this data.
 */
public class User {

    private final @NotNull String name;
    private final @NotNull UUID uniqueId;
    private boolean isOnline = true;

    private @NotNull ClickSession clickSession;
    private final @NotNull List<Violation> violations;

    public User(
            @NotNull String name,
            @NotNull UUID uniqueId
    ) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.clickSession = new ClickSession();
        this.violations = new ArrayList<>();
    }

    /**
     * Returns the name of the user.
     *
     * @return Name of the user
     */
    public @NotNull String name() {
        return this.name;
    }

    /**
     * Returns the {@link UUID} of the user.
     *
     * @return {@link UUID} of the user
     */
    public @NotNull UUID uniqueId() {
        return this.uniqueId;
    }

    /**
     * Checks if the user is currently online.
     *
     * @return {@code true} if the user is online, {@code false} otherwise
     */
    public boolean isOnline() {
        return this.isOnline;
    }

    /**
     * Returns the current {@link ClickSession} of the user.
     *
     * @return Current {@link ClickSession} of the user
     */
    public @NotNull ClickSession clickSession() {
        return this.clickSession;
    }

    /**
     * Returns the clicks per second of the last 20 {@link AbstractTick Ticks} of the current
     * {@link AbstractPattern Pattern}.
     *
     * @param type {@link ClickType} to get the clicks per second for
     * @return Clicks per second of the current {@link AbstractPattern Pattern}
     */
    public double clicksPerSecond(
            @NotNull ClickType type
    ) {
        return this.clickSession.clicksPerSecond(type);
    }

    /**
     * Returns the list of {@link Violation Violations} the {@link User} received.
     *
     * @return List of {@link Violation Violations}.
     */
    public List<Violation> violations() {
        return this.violations;
    }

    /**
     * Sets the {@link User#isOnline} status of the {@link User}.
     *
     * @param isOnline {@code true} if the {@link User} is online, {@code false} otherwise
     */
    public void setOnline(
            boolean isOnline
    ) {
        this.isOnline = isOnline;
        if (!isOnline) this.resetClickSession();
    }

    /**
     * Updates the current {@link ClickSession} of the {@link User}.
     * <p>
     * <b>Note:</b> This method should be called on every tick for patterns to work correctly.
     */
    public void updateClickSession() {
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
            @NotNull Violation violation
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

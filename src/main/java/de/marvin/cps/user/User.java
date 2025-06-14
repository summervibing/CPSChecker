package de.marvin.cps.user;

import de.marvin.cps.check.Violation;
import de.marvin.cps.click.pattern.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private final String name;
    private final UUID uniqueId;
    private boolean isOnline = true;

    private Pattern currentPattern;
    private final List<Violation> violations;

    public User(
            @NotNull final String name,
            @NotNull final UUID uniqueId
    ) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.currentPattern = new Pattern();
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
     * Gets the current {@link Pattern} of the user.
     *
     * @return Current {@link Pattern} of the user.
     */
    public Pattern currentPattern() {
        return this.currentPattern;
    }

    /**
     * Gets the clicks per second of the current {@link Pattern}.
     * This method counts all clicks, including non-attacks.
     *
     * @return Clicks per second of last 20 {@link de.marvin.cps.click.pattern.Tick Ticks}
     * of the current {@link Pattern}.
     */
    public double clicksPerSecond() {
        return this.clicksPerSecond(false);
    }

    /**
     * Gets the clicks per second of the current {@link Pattern}.
     *
     * @param onlyAttacks if {@code true}, only counts attack clicks;
     *                    if {@code false}, counts all clicks.
     * @return Clicks per second of last 20 {@link de.marvin.cps.click.pattern.Tick Ticks}
     * of the current {@link Pattern}.
     */
    public int clicksPerSecond(boolean onlyAttacks) {
        return this.currentPattern.clicksPerSecond(onlyAttacks);
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
    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
        if (!isOnline) this.resetPattern();
    }

    /**
     * Updates the current {@link Pattern} of the user.
     * <p>
     * <b>Note:</b> This method should be called on every
     * tick for pattern to work correctly.
     */
    public void updatePattern() {
        if (this.currentPattern == null) return;
        this.currentPattern.nextTick();
    }

    /**
     * Resets the current {@link Pattern} of the user.
     */
    public void resetPattern() {
        this.currentPattern = new Pattern();
    }

    /**
     * Adds a {@link Violation} to the {@link User}'s list of violations.
     *
     * @param violation {@link Violation} to add
     */
    public void addViolation(Violation violation) {
        this.violations.add(violation);
    }

    /**
     * Clears user's violations.
     */
    public void resetViolations() {
        this.violations.clear();
        this.resetPattern();
    }


}

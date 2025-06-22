package de.marvin.cps.event;

import de.marvin.cps.check.Violation;
import de.marvin.cps.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is triggered when a {@link User} receives
 * a {@link Violation} for their click behavior.
 */
public class CPSViolationEvent extends AbstractEvent {

    private final User user;
    private final Violation violation;
    private final int previousViolationLevel;
    private final int newViolationLevel;

    public CPSViolationEvent(
            @NotNull final User user,
            @NotNull final Violation violation,
            final int previousViolationLevel,
            final int newViolationLevel
    ) {
        this.user = user;
        this.violation = violation;
        this.previousViolationLevel = previousViolationLevel;
        this.newViolationLevel = newViolationLevel;
    }

    /**
     * Gets {@link User} the {@link Violation} is
     * addressed to.
     *
     * @return {@link User} of the {@link Violation}.
     */
    public User user() {
        return this.user;
    }

    /**
     * Gets {@link Violation} that was triggered.
     *
     * @return {@link Violation} that was triggered.
     */
    public Violation violation() {
        return this.violation;
    }

    /**
     * Gets the previous violation level of the user.
     *
     * @return Previous violation level of the user.
     */
    public int previousViolationLevel() {
        return this.previousViolationLevel;
    }

    /**
     * Gets the new violation level of the user.
     *
     * @return New violation level of the user.
     */
    public int newViolationLevel() {
        return this.newViolationLevel;
    }

}

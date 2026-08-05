package de.marvin.cps.core.event;

import de.marvin.cps.core.check.Violation;
import de.marvin.cps.core.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is triggered when a {@link User} receives a {@link Violation} for their click behavior.
 */
public class CPSViolationEvent extends AbstractEvent {

    /**
     * The {@link User} that received the {@link Violation}.
     */
    private final @NotNull User user;
    /**
     * The {@link Violation} that was triggered.
     */
    private final @NotNull Violation violation;
    /**
     * The previous violation level of the user before the violation was triggered.
     */
    private final int previousViolationLevel;
    /**
     * The new violation level of the user after the violation was triggered.
     */
    private final int newViolationLevel;

    public CPSViolationEvent(
            @NotNull User user,
            @NotNull Violation violation,
            int previousViolationLevel,
            int newViolationLevel
    ) {
        this.user = user;
        this.violation = violation;
        this.previousViolationLevel = previousViolationLevel;
        this.newViolationLevel = newViolationLevel;
    }

    /**
     * Returns {@link User} the {@link Violation} is addressed to.
     *
     * @return {@link User} of the {@link Violation}
     */
    public @NotNull User user() {
        return this.user;
    }

    /**
     * Returns {@link Violation} that was triggered.
     *
     * @return {@link Violation} that was triggered
     */
    public @NotNull Violation violation() {
        return this.violation;
    }

    /**
     * Returns the previous violation level of the user.
     *
     * @return Previous violation level of the user
     */
    public int previousViolationLevel() {
        return this.previousViolationLevel;
    }

    /**
     * Returns the new violation level of the user.
     *
     * @return New violation level of the user
     */
    public int newViolationLevel() {
        return this.newViolationLevel;
    }

}

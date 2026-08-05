package de.marvin.cps.core.check;

import de.marvin.cps.core.message.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the type of violation that can occur.
 */
public enum ViolationType {

    /**
     * The click pattern matches an illegitimate pattern.
     */
    CLICK_PATTERN(Message.FLAG_CLICK_PATTERN),

    /**
     * The click speed exceeds the configured limit.
     */
    CLICK_SPEED_LIMITER(Message.FLAG_CLICK_SPEED_LIMITER);

    /**
     * The alert {@link Message} that is sent to moderators when a player receives a violation of the
     * respective {@link ViolationType}.
     */
    private final @NotNull Message alert;

    ViolationType(
            @NotNull Message alert
    ) {
        this.alert = alert;
    }

    /**
     * Gets the alert {@link Message} that is sent when a player receives a violation.
     *
     * @return The alert {@link Message}
     */
    public @NotNull Message alert() {
        return this.alert;
    }

}

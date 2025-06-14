package de.marvin.cps.check;

import de.marvin.cps.message.Message;
import org.jetbrains.annotations.NotNull;

public enum ViolationType {

    CLICK_PATTERN(Message.FLAG_CLICK_PATTERN),
    CLICK_SPEED_LIMITER(Message.FLAG_CLICK_SPEED_LIMITER);

    private final Message alert;

    ViolationType(
            @NotNull final Message alert
    ) {
        this.alert = alert;
    }

    /**
     * Gets the alert {@link Message} that is
     * sent when a player receives a violation.
     *
     * @return The alert {@link Message}.
     */
    public Message alert() {
        return this.alert;
    }

}

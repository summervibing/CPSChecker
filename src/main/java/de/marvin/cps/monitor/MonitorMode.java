package de.marvin.cps.monitor;

import de.marvin.cps.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum MonitorMode {

    BASIC(Message.MONITOR_BASIC),
    HISTORY(Message.MONITOR_HISTORY),
    STREAK(Message.MONITOR_STREAK);

    private final Message format;

    MonitorMode(
            @NotNull final Message format
    ) {
        this.format = format;
    }

    /**
     * Gets the format {@link Message} that is
     * used to display the monitor information.
     *
     * @return The format {@link Message}.
     */
    public Message format() {
        return this.format;
    }

    /**
     * Gets {@link MonitorMode} by its {@link MonitorMode#name()}.
     *
     * @param mode name of {@link MonitorMode} to get
     * @return {@link MonitorMode} based on given name.
     */
    public static MonitorMode fromString(
            @NotNull final String mode
    ) {
        return Arrays.stream(values())
                .filter(monitorMode -> monitorMode.name().equalsIgnoreCase(mode))
                .findAny()
                .orElse(null);
    }

    public static MonitorMode next(
            @NotNull final MonitorMode mode
    ) {
        var ordinal = mode.ordinal();
        if (ordinal >= values().length - 1) return values()[0];
        return values()[ordinal + 1];
    }

}


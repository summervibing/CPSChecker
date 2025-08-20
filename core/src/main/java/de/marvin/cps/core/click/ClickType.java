package de.marvin.cps.core.click;

import org.jetbrains.annotations.NotNull;

public enum ClickType {

    LEFT_CLICK,
    INVALID_LEFT_CLICK,
    ATTACK,

    RIGHT_CLICK,
    PLACEMENT;

    /**
     * Checks if {@link ClickType} is a left click.
     *
     * @return {@code true} if {@link ClickType} is a left click,
     *         {@code false} otherwise.
     */
    public boolean isLeftClick() {
        return this == LEFT_CLICK || this == INVALID_LEFT_CLICK || this == ATTACK;
    }

    /**
     * Checks if {@link ClickType} is a right click.
     *
     * @return {@code true} if {@link ClickType} is a right click,
     *         {@code false} otherwise.
     */
    public boolean isRightClick() {
        return this == RIGHT_CLICK || this == PLACEMENT;
    }

    /**
     * Converts a {@link String} representation of a click type
     * to a {@link ClickType} enum.
     *
     * @param type String representation of the click type.
     * @return Corresponding {@link ClickType} or
     * {@code null} if no match is found.
     */
    public static ClickType fromString(
            @NotNull final String type
    ) {
        return switch (type.toUpperCase()) {
            case "LEFT", "LEFT_CLICK" -> LEFT_CLICK;
            case "RIGHT", "RIGHT_CLICK" -> RIGHT_CLICK;
            default -> null;
        };
    }

}

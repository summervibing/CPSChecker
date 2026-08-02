package de.marvin.cps.permission;

import org.jetbrains.annotations.NotNull;

/**
 * This enum defines all plugin permissions.
 */
public enum Permission {

    /**
     * The permission required to access commands and use basic functionalities.
     */
    COMMAND_USE("use"),

    /**
     * The permission required to access admin commands and use advanced functionalities.
     */
    ADMIN_COMMAND_USE("use.admin");

    /**
     * The plugin prefix for all permissions.
     */
    private static final String PREFIX = "cps.";

    /**
     * The permission string of the enum constant.
     */
    private final String permission;

    Permission(
            @NotNull String permission
    ) {
        this.permission = PREFIX + permission;
    }

    /**
     * Returns the permission string of the enum constant.
     *
     * @return The permission string of the enum constant
     */
    @Override
    public String toString() {
        return this.permission;
    }

}

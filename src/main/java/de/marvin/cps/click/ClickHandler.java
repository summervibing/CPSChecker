package de.marvin.cps.click;

import de.marvin.cps.user.User;
import de.marvin.cps.user.UserHandler;
import org.jetbrains.annotations.NotNull;
import de.marvin.cps.click.pattern.Pattern;

import java.util.UUID;

public class ClickHandler {

    private final UserHandler userHandler;

    public ClickHandler(
            @NotNull final UserHandler userHandler
    ) {
        this.userHandler = userHandler;
    }

    /**
     * Registers a click in the current pattern
     * of the user with the given {@link UUID}.
     *
     * @param uniqueId {@link UUID} of the user
     * @param invalid {@code true} if the click is invalid
     *                            (e.g., hitting a block)
     */
    public void registerClick(
            @NotNull final UUID uniqueId,
            final boolean invalid
    ) {
        User user = this.user(uniqueId);
        if (user == null) return;
        user.currentPattern().registerClick(invalid);
    }

    /**
     * Registers an attack in the current pattern
     * of the user with the given {@link UUID}.
     *
     * @param uniqueId {@link UUID} of the user
     */
    public void registerAttack(
            @NotNull final UUID uniqueId
    ) {
        User user = this.user(uniqueId);
        if (user == null) return;
        user.currentPattern().registerAttack();
    }

    /**
     * Updates the {@link Pattern Patterns}
     * of all online {@link User Users}.
     * <p>
     * <b>Note:</b> This method needs to be called
     * every tick to ensure that the {@link Pattern Patterns}
     * are updated correctly.
     */
    public void update() {
        this.userHandler.users(true).forEach(User::updatePattern);
    }

    // Helper methods

    /**
     * Gets the {@link User} for the given {@link UUID}.
     *
     * @param uniqueId {@link UUID} of the user
     * @return {@link User} associated with the given {@link UUID}.
     */
    private User user(
            @NotNull final UUID uniqueId
    ) {
        return this.userHandler.user(uniqueId);
    }

}

package de.marvin.cps.api.click;

import de.marvin.cps.core.click.pattern.Pattern;
import de.marvin.cps.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Handles click patterns.
 */
public interface ClickHandler {

    /**
     * Registers a click in the current pattern
     * of the user with the given {@link UUID}.
     *
     * @param uniqueId {@link UUID} of the user
     * @param invalid {@code true} if the click is invalid
     *                            (e.g., hitting a block)
     */
    void registerClick(@NotNull UUID uniqueId, boolean invalid);

    /**
     * Registers an attack in the current pattern
     * of the user with the given {@link UUID}.
     *
     * @param uniqueId {@link UUID} of the user
     */
    void registerAttack(@NotNull final UUID uniqueId);

    /**
     * Updates the {@link Pattern Patterns}
     * of all online {@link User Users}.
     * <p>
     * <b>Note:</b> This method needs to be called
     * every tick to ensure that the {@link Pattern Patterns}
     * are updated correctly.
     */
    void update();

}

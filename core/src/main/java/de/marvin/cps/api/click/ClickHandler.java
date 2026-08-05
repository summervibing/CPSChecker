package de.marvin.cps.api.click;

import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Handles {@link ClickSession ClickSessions}.
 */
public interface ClickHandler {

    /**
     * Registers a click in the current {@link ClickSession} of the user with the given {@link UUID}.
     * <p>
     * Possible {@link ClickType ClickTypes} are:
     * <ul>
     *     <li>{@link ClickType#LEFT_CLICK}</li>
     *     <li>{@link ClickType#INVALID_LEFT_CLICK}</li>
     *     <li>{@link ClickType#ATTACK}</li>
     *     <li>{@link ClickType#RIGHT_CLICK}</li>
     *     <li>{@link ClickType#PLACEMENT}</li>
     * </ul>
     *
     * @param uniqueId {@link UUID} of the user
     * @param type     {@link ClickType} of the click
     */
    void registerClick(@NotNull UUID uniqueId, @NotNull ClickType type);


    /**
     * Updates the {@link ClickSession ClickSessions} of all online {@link User Users}.
     * <p>
     * <b>Note:</b> This method needs to be called every tick to ensure that the
     * {@link ClickSession ClickSessions} are updated correctly.
     */
    void update();

}

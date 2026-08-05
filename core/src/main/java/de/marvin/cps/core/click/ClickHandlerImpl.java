package de.marvin.cps.core.click;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.api.click.ClickHandler;
import de.marvin.cps.api.user.UserHandler;
import de.marvin.cps.core.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Handles {@link ClickSession ClickSessions}.
 */
@Singleton
public class ClickHandlerImpl implements ClickHandler {

    private final @NotNull UserHandler userHandler;

    @Inject
    public ClickHandlerImpl(
            @NotNull UserHandler userHandler
    ) {
        this.userHandler = userHandler;
    }

    /**
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user
     * @param type     {@link ClickType} of the click
     */
    @Override
    public void registerClick(
            @NotNull UUID uniqueId,
            @NotNull ClickType type
    ) {
        var user = this.user(uniqueId);
        if (user == null) return;
        user.clickSession().registerClick(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        this.userHandler.users(true).forEach(User::updateClickSession);
    }

    // Helper methods

    /**
     * Returns the {@link User} for the given {@link UUID}.
     *
     * @param uniqueId {@link UUID} of the user
     * @return {@link User} associated with the given {@link UUID} or {@code null} if not found
     */
    private @Nullable User user(
            @NotNull UUID uniqueId
    ) {
        return this.userHandler.user(uniqueId);
    }

}

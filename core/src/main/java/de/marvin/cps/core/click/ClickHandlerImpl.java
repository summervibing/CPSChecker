package de.marvin.cps.core.click;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.marvin.cps.api.click.ClickHandler;
import de.marvin.cps.core.user.User;
import de.marvin.cps.api.user.UserHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
public class ClickHandlerImpl implements ClickHandler {

    private final UserHandler userHandler;

    @Inject
    public ClickHandlerImpl(
            @NotNull final UserHandler userHandler
    ) {
        this.userHandler = userHandler;
    }

    /**
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user
     * @param invalid {@code true} if the click is invalid
     *                            (e.g., hitting a block)
     */
    @Override
    public void registerClick(
            @NotNull final UUID uniqueId,
            final boolean invalid
    ) {
        User user = this.user(uniqueId);
        if (user == null) return;
        user.currentPattern().registerClick(invalid);
    }

    /**
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user
     */
    @Override
    public void registerAttack(
            @NotNull final UUID uniqueId
    ) {
        User user = this.user(uniqueId);
        if (user == null) return;
        user.currentPattern().registerAttack();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

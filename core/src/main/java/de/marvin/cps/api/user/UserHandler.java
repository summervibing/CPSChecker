package de.marvin.cps.api.user;

import de.marvin.cps.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

/**
 * Handles user management within the system.
 */
public interface UserHandler {

    /**
     * Retrieves the {@link User} associated with the given {@link UUID}.
     *
     * @param uniqueId {@link UUID} of the user
     * @return {@link User} associated with given {@link UUID}
     * or {@code null} if not found.
     */
    User user(@NotNull UUID uniqueId);

    /**
     * Retrieves all registered {@link User Users}.
     *
     * @param online if {@code true}, only returns {@link User Users}
     *               with {@link User#isOnline()} status as {@code true}
     * @return {@link Collection} of all registered {@link User Users}.
     */
    Collection<User> users(boolean online);

    /**
     * Registers a {@link User} with the given {@link UUID} or
     * updates the {@link User}'s online status if already registered.
     *
     * @param uniqueId {@link UUID} of the user to register
     */
    void register(@NotNull String username, @NotNull UUID uniqueId);

    /**
     * Sets {@link User}'s online status to {@code false}.
     *
     * @param uniqueId {@link UUID} of the user to set offline
     */
    void logout(@NotNull UUID uniqueId);

    /**
     * Resets the {@link User} associated with
     * the given {@link UUID}.
     * <p>
     * <b>Note:</b> This will also reset violation level and
     * suspicious patterns of the user.
     *
     * @param uniqueId {@link UUID} of the user to reset
     * @return the previous registered {@link User} object.
     */
    User reset(@NotNull UUID uniqueId);

}

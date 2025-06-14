package de.marvin.cps.user;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UserHandler {

    private final Map<UUID, User> users = new HashMap<>();

    /**
     * Retrieves the {@link User} associated with the given {@link UUID}.
     *
     * @param uniqueId {@link UUID} of the user
     * @return {@link User} associated with given {@link UUID}
     * or {@code null} if not found.
     */
    public User user(
            @NotNull final UUID uniqueId
    ) {
        return this.users.get(uniqueId);
    }

    /**
     * Retrieves all registered {@link User Users}.
     *
     * @param online if {@code true}, only returns {@link User Users}
     *               with {@link User#isOnline()} status as {@code true}.
     * @return {@link Collection} of all registered {@link User Users}.
     */
    public Collection<User> users(boolean online) {
        if (!online) return this.users.values();
        return this.users.values().stream().filter(User::isOnline).toList();
    }

    /**
     * Registers a {@link User} with the given {@link UUID} or
     * updates the {@link User}'s online status if already registered.
     *
     * @param uniqueId {@link UUID} of the user to register
     */
    public void register(
            @NotNull final String username,
            @NotNull final UUID uniqueId
    ) {
        var user = this.user(uniqueId);
        if (user != null) {
            user.setOnline(true);
            return;
        }
        this.users.put(uniqueId, new User(username, uniqueId));
    }

    /**
     * Sets {@link User}'s online status to {@code false}.
     *
     * @param uniqueId {@link UUID} of the user to set offline
     */
    public void logout(
            @NotNull final UUID uniqueId
    ) {
        var user = this.user(uniqueId);
        if (user == null) return;
        user.setOnline(false);
    }

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
    public User reset(
            @NotNull final UUID uniqueId
    ) {
        var user = this.users.get(uniqueId);
        this.users.put(uniqueId, new User(user.name(), uniqueId));
        return user;
    }

}

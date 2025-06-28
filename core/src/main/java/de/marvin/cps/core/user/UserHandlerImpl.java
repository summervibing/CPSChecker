package de.marvin.cps.core.user;

import com.google.inject.Singleton;
import de.marvin.cps.api.user.UserHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Singleton
public class UserHandlerImpl implements UserHandler {

    private final Map<UUID, User> users = new HashMap<>();

    /**
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user
     * @return {@link User} associated with given {@link UUID}
     * or {@code null} if not found.
     */
    @Override
    public User user(
            @NotNull final UUID uniqueId
    ) {
        return this.users.get(uniqueId);
    }

    /**
     * {@inheritDoc}
     *
     * @param online if {@code true}, only returns {@link User Users}
     *               with {@link User#isOnline()} status as {@code true}.
     * @return {@link Collection} of all registered {@link User Users}.
     */
    @Override
    public Collection<User> users(
            final boolean online
    ) {
        if (!online) return this.users.values();
        return this.users.values().stream().filter(User::isOnline).toList();
    }

    /**
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user to register
     */
    @Override
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
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user to set offline
     */
    @Override
    public void logout(
            @NotNull final UUID uniqueId
    ) {
        var user = this.user(uniqueId);
        if (user == null) return;
        user.setOnline(false);
    }

    /**
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user to reset
     * @return the previous registered {@link User} object.
     */
    @Override
    public User reset(
            @NotNull final UUID uniqueId
    ) {
        var user = this.users.get(uniqueId);
        this.users.put(uniqueId, new User(user.name(), uniqueId));
        return user;
    }

}

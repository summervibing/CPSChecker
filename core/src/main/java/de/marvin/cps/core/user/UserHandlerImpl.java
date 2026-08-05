package de.marvin.cps.core.user;

import com.google.inject.Singleton;
import de.marvin.cps.api.user.UserHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles user management within the system.
 */
@Singleton
public class UserHandlerImpl implements UserHandler {

    private final @NotNull Map<UUID, User> users = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user
     * @return {@link User} associated with given {@link UUID} or {@code null} if not found
     */
    @Override
    public @Nullable User user(
            @NotNull UUID uniqueId
    ) {
        return this.users.get(uniqueId);
    }

    /**
     * {@inheritDoc}
     *
     * @param online if {@code true}, only returns {@link User Users} with {@link User#isOnline()} status
     *               as {@code true}, otherwise returns all registered {@link User Users}
     * @return {@link Collection} of all registered {@link User Users}
     */
    @Override
    public @NotNull Collection<User> users(
            boolean online
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
            @NotNull String username,
            @NotNull UUID uniqueId
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
            @NotNull UUID uniqueId
    ) {
        var user = this.user(uniqueId);
        if (user == null) return;
        user.setOnline(false);
    }

    /**
     * {@inheritDoc}
     *
     * @param uniqueId {@link UUID} of the user to reset
     * @return The previous registered {@link User} object or {@code null} if no user was found
     */
    @Override
    public @Nullable User reset(
            @NotNull UUID uniqueId
    ) {
        var user = this.user(uniqueId);
        if (user == null) return null;
        this.users.put(uniqueId, new User(user.name(), uniqueId));
        return user;
    }

}

package de.marvin.cps.plugin.listener;

import com.google.inject.Inject;
import de.marvin.cps.api.user.UserHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Registers users to the {@link UserHandler} and handles their logout events.
 */
public class PlayerConnectionListener implements Listener {

    private final @NotNull UserHandler userHandler;

    @Inject
    public PlayerConnectionListener(
            @NotNull UserHandler userHandler
    ) {
        this.userHandler = userHandler;
    }

    /**
     * Registers a user to the {@link UserHandler} when they log on to the server.
     *
     * @param event {@link PlayerLoginEvent} to handle
     */
    @EventHandler
    public void handle(
            @NotNull PlayerLoginEvent event
    ) {
        var player = event.getPlayer();
        this.userHandler.register(player.getName(), player.getUniqueId());
    }

    /**
     * Sets the user offline in the {@link UserHandler} when they leave the server.
     *
     * @param event {@link PlayerQuitEvent} to handle
     */
    @EventHandler
    public void handle(
            @NotNull PlayerQuitEvent event
    ) {
        var player = event.getPlayer();
        this.userHandler.logout(player.getUniqueId());
    }

}

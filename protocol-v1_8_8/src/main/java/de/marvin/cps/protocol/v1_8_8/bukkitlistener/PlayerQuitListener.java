package de.marvin.cps.protocol.v1_8_8.bukkitlistener;

import de.marvin.cps.protocol.v1_8_8.packetlistener.ClickListenerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Removes player references out of any lists, sets or maps in case they remain after quitting the server.
 */
public class PlayerQuitListener implements Listener {

    private final @NotNull ClickListenerImpl clickListener;

    public PlayerQuitListener(
            @NotNull ClickListenerImpl clickListener
    ) {
        this.clickListener = clickListener;
    }

    /**
     * Remove player out of {@link ClickListenerImpl#isDigging()} in case they somehow remain in set after
     * quitting.
     *
     * @param event {@link PlayerQuitEvent} to handle
     */
    @EventHandler
    public void handle(
            @NotNull PlayerQuitEvent event
    ) {
        this.clickListener.isDigging().remove(event.getPlayer().getUniqueId());
    }

}

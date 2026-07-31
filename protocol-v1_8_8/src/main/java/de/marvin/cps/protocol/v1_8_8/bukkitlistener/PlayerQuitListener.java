package de.marvin.cps.protocol.v1_8_8.bukkitlistener;

import de.marvin.cps.protocol.v1_8_8.packetlistener.ClickListenerImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;


public class PlayerQuitListener implements Listener {

    private final ClickListenerImpl clickListener;

    public PlayerQuitListener(
            @NotNull final ClickListenerImpl clickListener
    ) {
        this.clickListener = clickListener;
    }

    /**
     * Remove player out of {@link ClickListenerImpl#isDigging()}
     * in case they somehow remain in set after quitting.
     *
     * @param event player quit event
     */
    @EventHandler
    public void handle(PlayerQuitEvent event) {
        this.clickListener.isDigging().remove(event.getPlayer().getUniqueId());
    }

}

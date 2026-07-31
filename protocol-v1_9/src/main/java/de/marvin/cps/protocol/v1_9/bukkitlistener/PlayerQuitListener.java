package de.marvin.cps.protocol.v1_9.bukkitlistener;

import de.marvin.cps.protocol.v1_9.packetlistener.ClickListenerImpl;
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
     * Removes player out of {@link ClickListenerImpl#isDigging()} and
     * {@link ClickListenerImpl#rightClickedGround()} in case they
     * somehow remain in there after quitting.
     *
     * @param event player quit event
     */
    @EventHandler
    public void handle(PlayerQuitEvent event) {
        var uniqueId = event.getPlayer().getUniqueId();

        this.clickListener.isDigging().remove(uniqueId);
        this.clickListener.rightClickedGround().remove(uniqueId);
    }

}

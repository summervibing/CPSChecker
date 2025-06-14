package de.marvin.cps.listener;

import de.marvin.cps.click.ClickHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamageByEntityListener implements Listener {

    private final ClickHandler clickHandler;

    public EntityDamageByEntityListener(
            @NotNull final ClickHandler clickHandler
    ) {
        this.clickHandler = clickHandler;
    }

    /**
     * Listens to {@link EntityDamageByEntityEvent} to register
     * attacks.
     *
     * @param event entity damage by entity event
     */
    @EventHandler
    public void handle(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof final Player player)) return;
        this.clickHandler.registerAttack(
                player.getUniqueId()
        );
    }

}

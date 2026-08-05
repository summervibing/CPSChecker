package de.marvin.cps.core.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The class represents an abstract {@link Event} with all necessary methods as a basis for all events
 * of this plugin.
 */
public abstract class AbstractEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public AbstractEvent() {
        this(false);
    }

    public AbstractEvent(boolean async) {
        super(async);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}

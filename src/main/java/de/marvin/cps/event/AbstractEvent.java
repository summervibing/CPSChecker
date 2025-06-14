package de.marvin.cps.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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

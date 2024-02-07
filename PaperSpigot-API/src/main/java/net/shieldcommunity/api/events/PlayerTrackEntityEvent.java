package net.shieldcommunity.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when an {@link Entity} is added to a {@link Player}'s entity tracker.
 * Cancelling the event will prevent the given entity from entering the player's
 * entity tracker.
 */
public class PlayerTrackEntityEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Entity tracked;
    private boolean cancelled;

    /**
     * +     * Creates a new {@code PlayerTrackEntityEvent} for the given player and tracked entity.
     * +     *
     * +     * @param player  the player who the entity is added for
     * +     * @param tracked the entity that is to be added to the player's entity tracker
     * +
     */
    public PlayerTrackEntityEvent(Player player, Entity tracked) {
        super(player);
        this.tracked = tracked;
    }

    /**
     * +     * Returns the list of handlers that handle a {@code PlayerTrackEntityEvent}.
     * +
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * +     * Returns the list of handlers that handle a {@code PlayerTrackEntityEvent}.
     * +
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * +     * Returns the entity that is to be tracked in the player's tracker
     * +
     */
    public Entity getTracked() {
        return tracked;
    }


    /**
     * +     * Returns whether this event is cancelled.
     * +
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * +     * Sets the cancelled status of this event. Cancelling the event will prevent the entity from being added to the
     * +     * player's entity tracker.
     * +     *
     * +     * @param cancel true if you wish to cancel this event
     * +
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
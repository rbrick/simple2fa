package io.dreamz.simple2fa.events;

import io.dreamz.simple2fa.session.Session;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class PlayerAuthenticatedEvent extends PlayerEvent {

    private static final HandlerList handlerList = new HandlerList();

    private Session session;


    public PlayerAuthenticatedEvent(Player who, Session session) {
        super(who);

        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

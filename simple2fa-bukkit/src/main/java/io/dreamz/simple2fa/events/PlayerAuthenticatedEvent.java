package io.dreamz.simple2fa.events;

import io.dreamz.simple2fa.session.Session;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerAuthenticatedEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private Player who;
    private Session session;


    public PlayerAuthenticatedEvent(Player who, Session session) {
        this(who, session, false);
    }

    public PlayerAuthenticatedEvent(Player who, Session session, boolean async) {
        super(async);
        this.who = who;
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public Player getPlayer() {
        return who;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

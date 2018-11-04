package io.dreamz.simple2fa.session;

import org.bukkit.inventory.Inventory;

public interface Session {


    /**
     * This is a snapshot of the player's inventory when they join in.
     *
     * Players will not have access to their inventory when they login, on authentication they will
     * get their items back.
     *
     * @return Their previous inventory
     */
    Inventory getInventorySnapshot();

}

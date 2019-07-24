package io.dreamz.simple2fa.session;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Session {

    /**
     * Checks if the session is authenticated
     *
     * @return Whether or not the session has been authenticated
     */
    boolean isAuthenticated();

    /**
     * Checks if the session needs authentication or not
     *
     * @return Whether or not the session needs to be authenticated
     */
    boolean needsAuthentication();

    /**
     * Authenticates a user
     * @param code The code to enter
     */
    void authenticate(String code, Consumer<Boolean> callback);

    /**
     * This is a snapshot of the player's location when they join and need to authentication
     * <p>
     * Players can optionally be ported to another area or world for authentication then moved back to where
     * they were at.
     *
     * @return Their previous locations.
     */
    Location getLocationSnapshot();

    /**
     * This is a snapshot of the player's inventory when they join in.
     * <p>
     * Players will not have access to their inventory when they login, on authentication they will
     * get their items back.
     *
     * @return Their previous inventory
     */
    ItemStack[] getInventorySnapshot();


    /**
     * This is a snapshot of the player's armor when they join in.
     * <p>
     * Players will not have access to their armor when they login, on authentication they will
     * get their armor back.
     *
     * @return Their previous inventory
     */
    ItemStack[] getArmorSnapshot();

    Player getPlayer();
}

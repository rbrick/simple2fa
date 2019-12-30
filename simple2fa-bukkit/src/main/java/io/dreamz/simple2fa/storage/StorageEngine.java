package io.dreamz.simple2fa.storage;

import io.dreamz.simple2fa.session.Session;
import io.dreamz.simple2fa.session.UserSession;
import io.dreamz.simple2fa.utils.Base32String;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageEngine {
    void save();

    interface Factory {
        StorageEngine create();
    }

    /**
     * Stores a user's secret key
     *
     * @param uniqueId The Unique ID of the user
     * @param secret   The user's secret
     */
    void storeSecret(UUID uniqueId, String secret);

    /**
     * Fetches the user's secret key (if available)
     *
     * @param uniqueId The Unique ID of the user
     * @return The user's secret key
     */
    String getSecret(UUID uniqueId);

    /**
     * Stores a user session in the database.
     *
     * @param ipAddress The IP Address of the user
     * @param uniqueId  The Unique ID of the user
     * @param session   The session to be stored
     */
    void storeSession(String ipAddress, UUID uniqueId, UserSession session);

    /**
     * Fetches an existing session for the player
     * @param player
     * @return
     */
    Session getStoredSession(Player player);

    default byte[] getRawSecret(UUID uniqueId) {
        try {
            return Base32String.decode(this.getSecret(uniqueId));
        } catch (Base32String.DecodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    default boolean hasSecret(UUID uniqueId) {
        return this.getSecret(uniqueId) != null;
    }
}

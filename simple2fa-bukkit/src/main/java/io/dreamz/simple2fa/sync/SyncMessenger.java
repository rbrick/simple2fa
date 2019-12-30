package io.dreamz.simple2fa.sync;

import io.dreamz.simple2fa.session.Session;

import java.util.UUID;

/**
 * The SyncMessenger handles incoming & outgoing messages used for syncing a Session's status across every server.
 */
public interface SyncMessenger {

    /**
     * Syncs a message across all connected servers
     *
     * @param uniqueId - The Unique ID of the player
     * @param session  - The session for the player
     */
    void sync(UUID uniqueId, Session session);


    /**
     * Called when a session is sync'd to this server
     *
     * @param uniqueId - The Unique ID of the player
     * @param session  - The sync'd session
     */
    void onSync(UUID uniqueId, Session session);
}

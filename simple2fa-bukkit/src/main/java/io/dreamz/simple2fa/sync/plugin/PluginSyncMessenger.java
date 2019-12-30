package io.dreamz.simple2fa.sync.plugin;

import io.dreamz.simple2fa.session.Session;
import io.dreamz.simple2fa.sync.SyncMessenger;

import java.util.UUID;

public final class PluginSyncMessenger implements SyncMessenger {
    @Override
    public void sync(UUID uniqueId, Session session) {
    }

    @Override
    public void onSync(UUID uniqueId, Session session) {

    }
}

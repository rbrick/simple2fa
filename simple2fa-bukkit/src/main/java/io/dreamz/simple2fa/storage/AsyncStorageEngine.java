package io.dreamz.simple2fa.storage;

import io.dreamz.simple2fa.session.Session;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface AsyncStorageEngine extends StorageEngine {

    @Override
    default void save() {
        throw new UnsupportedOperationException();
    }

    CompletableFuture<String> getSecretAsync(UUID uniqueId);

    CompletableFuture<Boolean> hasSecretAsync(UUID uniqueId);

    CompletableFuture<Session> getStoredSessionAsync(Player player);
}

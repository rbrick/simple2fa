package io.dreamz.simple2fa.storage;

import java.util.UUID;

public interface StorageEngine {
    interface Factory {
        StorageEngine create();
    }

    void storeSecret(UUID uniqueId, String secret);
}

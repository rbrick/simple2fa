package io.dreamz.simple2fa.storage;

import io.dreamz.simple2fa.utils.Base32String;

import java.util.UUID;

public interface StorageEngine {
    void save();

    interface Factory {
        StorageEngine create();
    }

    void storeSecret(UUID uniqueId, String secret);

    String getSecret(UUID uniqueId);


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

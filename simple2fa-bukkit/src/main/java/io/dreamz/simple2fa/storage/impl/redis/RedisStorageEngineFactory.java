package io.dreamz.simple2fa.storage.impl.redis;

import io.dreamz.simple2fa.storage.StorageEngine;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.JedisPool;

public final class RedisStorageEngineFactory implements StorageEngine.Factory {

    private String uri = "redis://localhost:6379";

    @NotNull
    public RedisStorageEngineFactory withUri(@NotNull String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public RedisStorageEngine create() {
        return new RedisStorageEngine(
                new JedisPool(uri)
        );
    }
}

package io.dreamz.simple2fa.storage;


import io.dreamz.simple2fa.storage.impl.flatfile.FlatFileStorageEngine;
import io.dreamz.simple2fa.storage.impl.mongodb.MongoDBStorageEngineFactory;
import io.dreamz.simple2fa.storage.impl.redis.RedisStorageEngineFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class StorageEngineBuilder {

    @NotNull
    public StorageEngine.Factory flatfile(File file) {
        return new FlatFileStorageEngine.FlatFileStorageEngineFactory(file);
    }

    @NotNull
    public MongoDBStorageEngineFactory mongodb() {
        return new MongoDBStorageEngineFactory();
    }

    @NotNull
    public RedisStorageEngineFactory redis() {
        return new RedisStorageEngineFactory();
    }

    public StorageEngineBuilder() {
    }
}

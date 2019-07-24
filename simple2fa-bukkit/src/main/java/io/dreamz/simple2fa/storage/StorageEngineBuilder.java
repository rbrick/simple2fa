package io.dreamz.simple2fa.storage;


import io.dreamz.simple2fa.storage.impl.flatfile.FlatFileStorageEngine;
import io.dreamz.simple2fa.storage.impl.mongodb.MongoDBStorageEngineFactory;

import java.io.File;

public final class StorageEngineBuilder {

    public StorageEngine.Factory flatfile(File file) {
        return new FlatFileStorageEngine.FlatFileStorageEngineFactory(file);
    }

    public MongoDBStorageEngineFactory  mongodb() {
        return new MongoDBStorageEngineFactory();
    }

    public StorageEngineBuilder() {
    }
}

package io.dreamz.simple2fa.storage.impl.mongodb;

import io.dreamz.simple2fa.storage.StorageEngine;

public final class MongoDBStorageEngineFactory implements StorageEngine.Factory {

    private String uri, database, collection;

    public MongoDBStorageEngineFactory withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public MongoDBStorageEngineFactory withCollection(String collection) {
        this.collection = collection;
        return this;
    }

    public MongoDBStorageEngineFactory withDatabase(String database) {
        this.database = database;
        return this;
    }


    @Override
    public StorageEngine create() {
        return new MongoDBStorageEngine(uri, database, collection);
    }


    // empty
    public MongoDBStorageEngineFactory() {
    }
}

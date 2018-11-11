package io.dreamz.simple2fa.storage;

import io.dreamz.simple2fa.storage.flatfile.FlatFileStorageEngine;

import java.io.File;

public final class StorageEngineBuilder {

    public StorageEngine.Factory withFile(File file) {
        return new FlatFileStorageEngine.FlatFileStorageEngineFactory(file);
    }


    public StorageEngineBuilder() {
    }
}

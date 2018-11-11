package io.dreamz.simple2fa.storage.flatfile;

import io.dreamz.simple2fa.storage.StorageEngine;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class FlatFileStorageEngine implements StorageEngine {

    public static class FlatFileStorageEngineFactory implements StorageEngine.Factory {
        private File file;

        public FlatFileStorageEngineFactory(File file) {
            this.file = file;
        }

        @Override
        public StorageEngine create() {
            return new FlatFileStorageEngine(file, YamlConfiguration.loadConfiguration(file));
        }
    }

    private File originalFile;
    private YamlConfiguration yamlConfiguration;

    private FlatFileStorageEngine(File originalFile, YamlConfiguration yamlConfiguration) {
        this.originalFile = originalFile;
        this.yamlConfiguration = yamlConfiguration;
    }


    @Override
    public void save() {
        try {
            yamlConfiguration.save(originalFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeSecret(UUID uniqueId, String secret) {
        this.yamlConfiguration.set(uniqueId.toString(), secret);
    }

    @Override
    public String getSecret(UUID uniqueId) {
        return this.yamlConfiguration.getString(uniqueId.toString());
    }
}

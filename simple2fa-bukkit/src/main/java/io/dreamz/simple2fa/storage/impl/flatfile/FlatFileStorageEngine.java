package io.dreamz.simple2fa.storage.impl.flatfile;

import io.dreamz.simple2fa.session.Session;
import io.dreamz.simple2fa.session.UserSession;
import io.dreamz.simple2fa.session.player.Sessions;
import io.dreamz.simple2fa.storage.StorageEngine;
import io.dreamz.simple2fa.utils.Hashing;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class FlatFileStorageEngine implements StorageEngine {

    public static class FlatFileStorageEngineFactory implements Factory {
        private File file;

        public FlatFileStorageEngineFactory(File file) {
            this.file = file;

            if (!this.file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    @Override
    public void storeSession(String ipAddress, UUID uniqueId, UserSession session) {
        String sessionId = Hashing.hash("SHA-256", (ipAddress + ":" + uniqueId.toString()).getBytes());

        ConfigurationSection section = this.yamlConfiguration.createSection("sessions." + sessionId);

        section.set("authenticated", session.isAuthenticated());
        section.set("expireAt", session.expireAt());
    }

    @Override
    public Session getStoredSession(Player player) {
        String sessionId = Hashing.hash("SHA-256", (player.getAddress().getAddress().getHostAddress() + ":" + player.getUniqueId().toString()).getBytes());

        ConfigurationSection section = this.yamlConfiguration.getConfigurationSection("sessions." + sessionId);
        if (section != null) {
            return Sessions.ofPlayerWithInfo(player, section.getLong("expireAt"), section.getBoolean("authenticated"));
        }
        return null;
    }

    @Override
    public boolean hasSecret(UUID uniqueId) {
        return this.yamlConfiguration.contains(uniqueId.toString());
    }
}

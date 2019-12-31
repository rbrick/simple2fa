package io.dreamz.simple2fa.storage.impl.redis;

import io.dreamz.simple2fa.Simple2FA;
import io.dreamz.simple2fa.session.Session;
import io.dreamz.simple2fa.session.UserSession;
import io.dreamz.simple2fa.session.player.Sessions;
import io.dreamz.simple2fa.storage.AsyncStorageEngine;
import io.dreamz.simple2fa.utils.Hashing;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public final class RedisStorageEngine implements AsyncStorageEngine {

    private final JedisPool jedisPool;
    private Map<UUID, String> secretCache = new ConcurrentHashMap<>();


    public RedisStorageEngine(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public CompletableFuture<String> getSecretAsync(UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> secretCache.computeIfAbsent((uniqueId), uuid -> {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.get("s2fa:secrets:" + uuid.toString());
            }
        }));
    }

    @Override
    public CompletableFuture<Boolean> hasSecretAsync(UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.exists("s2fa:secrets:" + uniqueId.toString());
            }
        });
    }

    @Override
    public CompletableFuture<Session> getStoredSessionAsync(Player player) {
        return CompletableFuture.supplyAsync(() -> getStoredSession(player));
    }

    @Override
    public void storeSecret(UUID uniqueId, String secret) {
        Bukkit.getScheduler().runTaskAsynchronously(Simple2FA.getInstance(), () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set("s2fa:secrets:" + uniqueId.toString(), secret);
            }
        });
    }

    @Override
    public String getSecret(UUID uniqueId) {
        try {
            return this.getSecretAsync(uniqueId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void storeSession(String ipAddress, UUID uniqueId, UserSession session) {
        Bukkit.getScheduler().runTaskAsynchronously(Simple2FA.getInstance(), () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                String hashed = Hashing.hash("SHA-256", (ipAddress + ":" + uniqueId.toString()).getBytes());
                Map<String, String> map = new HashMap<>();
                {
                    map.put("authenticated", String.valueOf(session.isAuthenticated()));
                    map.put("expireAt", Long.toString(session.expireAt()));
                }

                jedis.hset("s2fa:sessions:" + hashed, map);
            }
        });
    }

    @Override
    public Session getStoredSession(Player player) {
        try (Jedis jedis = jedisPool.getResource()) {
            String hashed = Hashing.hash("SHA-256", (player.spigot().getRawAddress().getHostName() + ":" + player.getUniqueId().toString()).getBytes());

            if (jedis.exists("s2fa:sessions:" + hashed)) {
                Map<String, String> data = jedis.hgetAll("s2fa:sessions:" + hashed);

                return Sessions.ofPlayerWithInfo(player, Long.parseLong(data.get("expireAt")), Boolean.parseBoolean(data.get("authenticated")));
            }
        }
        return null;
    }

}

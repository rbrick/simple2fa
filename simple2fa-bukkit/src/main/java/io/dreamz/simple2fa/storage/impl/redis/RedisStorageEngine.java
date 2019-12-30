package io.dreamz.simple2fa.storage.impl.redis;

import io.dreamz.simple2fa.Simple2FA;
import io.dreamz.simple2fa.storage.AsyncStorageEngine;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class RedisStorageEngine implements AsyncStorageEngine {

    private final JedisPool jedisPool;

    public RedisStorageEngine(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public CompletableFuture<String> getSecretAsync(UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.get("s2fa:secrets:" + uniqueId.toString());
            }
        });
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
}

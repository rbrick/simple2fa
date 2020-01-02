package io.dreamz.simple2fa.storage.impl.mongodb;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.dreamz.simple2fa.Simple2FA;
import io.dreamz.simple2fa.session.Session;
import io.dreamz.simple2fa.session.UserSession;
import io.dreamz.simple2fa.session.player.Sessions;
import io.dreamz.simple2fa.storage.AsyncStorageEngine;
import io.dreamz.simple2fa.utils.Hashing;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public final class MongoDBStorageEngine implements AsyncStorageEngine {
    private static final ReplaceOptions UPSERT_OPTION = new ReplaceOptions().upsert(true);

    private MongoCollection<Document> mongoCollection;

    MongoDBStorageEngine(String mongoUri, String database, String collectionName) {
        MongoClient mongoClient = MongoClients.create(mongoUri);

        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);

        this.mongoCollection = mongoDatabase.getCollection(collectionName);
    }

    @Override
    public void save() {
    }

    @Override
    public void storeSecret(UUID uniqueId, String secret) {
        Document insertMe = new Document("uniqueId", uniqueId.toString())
                .append("secret", secret);
        CompletableFuture<UpdateResult> lol = new CompletableFuture<>();

        this.mongoCollection
                .replaceOne(Filters.eq("uniqueId", uniqueId.toString()), insertMe, UPSERT_OPTION).subscribe(new CompletableSubscriber<>(lol));
        Simple2FA.getInstance().getKeyCache().put(uniqueId, secret);
    }

    @Override
    public CompletableFuture<String> getSecretAsync(UUID uniqueId) {
        if ( Simple2FA.getInstance().getKeyCache().containsKey(uniqueId)) {
            return CompletableFuture.completedFuture( Simple2FA.getInstance().getKeyCache().get(uniqueId));
        }

        CompletableFuture<Document> completableFuture = new CompletableFuture<>();
        CompletableSubscriber<Document> completableSubscriber = new CompletableSubscriber<>(completableFuture);

        this.mongoCollection.find(Filters.eq("uniqueId", uniqueId.toString()))
                .first().subscribe(completableSubscriber);

        return completableFuture.whenComplete((result, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                Simple2FA.getInstance().getKeyCache().put(uniqueId, result.getString("secret"));
            }
        }).thenApply((document -> document.getString("secret")));
    }

    @Override
    public CompletableFuture<Boolean> hasSecretAsync(UUID uniqueId) {
        if (! Simple2FA.getInstance().getKeyCache().containsKey(uniqueId)) {
            CompletableFuture<Long> completableFuture = new CompletableFuture<>();
            CompletableSubscriber<Long> completableSubscriber = new CompletableSubscriber<>(completableFuture);

            this.mongoCollection.countDocuments(Filters.eq("uniqueId", uniqueId.toString()))
                    .subscribe(completableSubscriber);
            return completableFuture.thenApply(c -> c > 0);
        }
        return CompletableFuture.completedFuture(true);
    }


    @Override
    public String getSecret(UUID uniqueId) {
        try {
            // blocking
            return this.getSecretAsync(uniqueId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void storeSession(String ipAddress, UUID uniqueId, UserSession session) {
        String sessionId = Hashing.hash("SHA-256", (ipAddress + ":" + uniqueId.toString()).getBytes());
        Document insertMe = new Document("sessionId", sessionId)
                .append("expireAt", session.expireAt())
                .append("authenticated", session.isAuthenticated());
        CompletableFuture<UpdateResult> lol = new CompletableFuture<>();

        this.mongoCollection.replaceOne(Filters.eq("sessionId", sessionId), insertMe, UPSERT_OPTION).subscribe(new CompletableSubscriber<>(lol));
    }

    @Override
    public Session getStoredSession(Player player) {
        try {
            return this.getStoredSessionAsync(player).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CompletableFuture<Session> getStoredSessionAsync(Player player) {
        String sessionId = Hashing.hash("SHA-256", (player.getAddress().getAddress().getHostAddress() + ":" + player.getUniqueId().toString()).getBytes());

        CompletableFuture<Document> completableFuture = new CompletableFuture<>();
        this.mongoCollection.find(Filters.eq("sessionId", sessionId)).first().subscribe(new CompletableSubscriber<>(completableFuture));

        return completableFuture.thenApply((document -> {
            if (document != null) {
                return Sessions.ofPlayerWithInfo(player, document.getLong("expireAt"), document.getBoolean("authenticated", false));
            }
            return null;
        }));
    }


    private static class CompletableSubscriber<T> implements Subscriber<T> {
        private CompletableFuture<T> future;
        private T result;

        CompletableSubscriber(CompletableFuture<T> future) {
            this.future = future;
        }

        @Override
        public void onSubscribe(Subscription s) {
            s.request(1);
        }

        @Override
        public void onNext(T result) {
            this.result = result;
        }

        @Override
        public void onError(Throwable t) {
            this.future.completeExceptionally(t);
        }

        @Override
        public void onComplete() {
            this.future.complete(this.result);
        }
    }
}

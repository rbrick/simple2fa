package io.dreamz.simple2fa.storage.impl.mongodb;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.dreamz.simple2fa.storage.AsyncStorageEngine;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public final class MongoDBStorageEngine implements AsyncStorageEngine {
    private static final ReplaceOptions UPSERT_OPTION = new ReplaceOptions().upsert(true);
    private Map<UUID, String> cache = new ConcurrentHashMap<>();

    private MongoClient mongoClient;
    private MongoCollection<Document> mongoCollection;

    MongoDBStorageEngine(String mongoUri, String database, String collectionName) {
        this.mongoClient = MongoClients.create(mongoUri);

        MongoDatabase mongoDatabase = this.mongoClient.getDatabase(database);

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
        this.cache.put(uniqueId, secret);
    }

    @Override
    public CompletableFuture<String> getSecretAsync(UUID uniqueId) {
        if (this.cache.containsKey(uniqueId)) {
            return CompletableFuture.completedFuture(this.cache.get(uniqueId));
        }

        CompletableFuture<Document> completableFuture = new CompletableFuture<>();
        CompletableSubscriber<Document> completableSubscriber = new CompletableSubscriber<>(completableFuture);

        this.mongoCollection.find(Filters.eq("uniqueId", uniqueId.toString()))
                .first().subscribe(completableSubscriber);

        return completableFuture.whenComplete((result, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                this.cache.put(uniqueId, result.getString("secret"));
            }
        }).thenApply((document -> document.getString("secret")));
    }

    @Override
    public CompletableFuture<Boolean> hasSecretAsync(UUID uniqueId) {
        if (!this.cache.containsKey(uniqueId)) {
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

    private class CompletableSubscriber<T> implements Subscriber<T> {
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

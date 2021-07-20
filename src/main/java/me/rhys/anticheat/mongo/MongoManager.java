package me.rhys.anticheat.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.rhys.anticheat.Anticheat;
import org.bson.Document;


public class MongoManager {

    public MongoDatabase mongoDatabase;
    public MongoClient mongoClient;
    public MongoCollection<Document> collection;

    public MongoManager() {
        mongoClient = new MongoClient(Anticheat.getInstance().getConfigValues().getHostName());
        mongoDatabase = mongoClient.getDatabase(Anticheat.getInstance().getConfigValues().getDatabase());
        collection = mongoDatabase.getCollection(Anticheat.getInstance().getConfigValues().getLogs());
    }

    public MongoCollection<Document> getLogs() {
        return collection;
    }
}
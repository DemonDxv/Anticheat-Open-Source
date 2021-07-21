package me.rhys.anticheat.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.config.ConfigValues;
import org.bson.Document;

import java.util.Collections;


public class MongoManager {

    public MongoDatabase mongoDatabase;
    public MongoClient mongoClient;
    public MongoCollection<Document> collection;

    public MongoManager() {
        ConfigValues configValue = Anticheat.getInstance().getConfigValues();

        mongoClient = new MongoClient(configValue.getHostName());
        mongoDatabase = mongoClient.getDatabase(configValue.getDatabase());
        collection = mongoDatabase.getCollection(configValue.getLogs());


        if (Anticheat.getInstance().getConfigValues().isMongoEnabled()) {
            MongoCredential credential = MongoCredential.createCredential(configValue.getMUsername(),
                    configValue.getDatabase(),
                    configValue.getMPassword().toCharArray());



        } else {
            mongoClient = new MongoClient(configValue.getHostName());
        }

    }

    public MongoCollection<Document> getLogs() {
        return collection;
    }
}
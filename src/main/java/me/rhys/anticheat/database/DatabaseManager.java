package me.rhys.anticheat.database;

import lombok.Getter;
import me.rhys.anticheat.database.api.DatabaseInterface;
import me.rhys.anticheat.database.api.InputData;
import me.rhys.anticheat.database.impl.MongoManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class DatabaseManager {
    private final DatabaseInterface database = new MongoManager();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final List<InputData> logQueue = new LinkedList<>();

    public void setup() {
        this.database.initManager();

        //so no db spam
        this.executorService.scheduleAtFixedRate(() -> {

            //Fixes a bug where the logs would not upload...??
            synchronized (logQueue) {
                this.logQueue.forEach(this.database::addViolation);
            }

            this.logQueue.clear();
        }, 5L, 5L, TimeUnit.SECONDS);
    }

    public void shutdown() {
        database.shutdown();
        this.executorService.shutdownNow();
    }
}

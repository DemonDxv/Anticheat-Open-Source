package me.rhys.anticheat.util;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * Credits to dawson/funkemunky for this
 *
 */
public class PastLocation {
    private List<PlayerLocation> previousLocations = new CopyOnWriteArrayList<>();

    public PlayerLocation getPreviousLocation(long time) {
        return previousLocations.stream().min(Comparator.comparingLong(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time)))).orElse(previousLocations.get(previousLocations.size() - 1));
    }

    public List<PlayerLocation> getEstimatedLocation(long time, long delta) {
        List<PlayerLocation> locs = new ArrayList<>();

        previousLocations.stream()
                .sorted(Comparator.comparingLong(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time))))
                .filter(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time)) < delta)
                .forEach(locs::add);
        return locs;
    }

    public void addLocation(Location location) {
        if (previousLocations.size() >= 20) {
            previousLocations.remove(0);
        }

        previousLocations.add(new PlayerLocation(location));
    }

    public void addLocation(PlayerLocation location) {
        if (previousLocations.size() >= 20) {
            previousLocations.remove(0);
        }

        previousLocations.add(location);
    }

    public List<PlayerLocation> getPreviousLocations() {
        return previousLocations;
    }
}
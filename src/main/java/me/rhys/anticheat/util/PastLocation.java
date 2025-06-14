package me.rhys.anticheat.util;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 *
 * Credits to dawson/funkemunky for this
 *
 */
public class PastLocation {
    private final List<PlayerLocation> previousLocations = new CopyOnWriteArrayList<>();
    private final List<CustomLocation> previousLocations2 = new CopyOnWriteArrayList<>();

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

    public List<PlayerLocation> getEstimatedLocation(long time, long ping, long delta) {
        return this.previousLocations
                .stream()
                .filter(loc -> time - loc.getTimeStamp() < ping + delta)
                .collect(Collectors.toList());
    }

    public List<PlayerLocation> getEstimatedLocation(long time) {
        return this.previousLocations
                .stream()
                .filter(loc -> time - loc.getTimeStamp() > 0 && time - loc.getTimeStamp() < 700)
                .collect(Collectors.toList());
    }


    public List<CustomLocation> getEstimatedCustomLocation(long time, long ping, long delta) {
        return this.previousLocations2
                .stream()
                .filter(loc -> time - loc.getTimestamp() > 0 && time - loc.getTimestamp() < ping + delta)
                .collect(Collectors.toList());
    }

    public void addCustomLocation(CustomLocation location) {
        if (previousLocations2.size() >= 8) {
            previousLocations2.remove(0);
        }

        previousLocations2.add(new CustomLocation(location));
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

    public void addLocationLow(PlayerLocation location) {
        if (previousLocations.size() >= 20) {
            previousLocations.remove(0);
        }

        previousLocations.add(location);
    }

    public List<PlayerLocation> getPreviousLocations() {
        return previousLocations;
    }
}
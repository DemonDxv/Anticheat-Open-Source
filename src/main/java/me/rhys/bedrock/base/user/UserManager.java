package me.rhys.bedrock.base.user;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class UserManager {
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();

    public void addUser(Player player) {
        this.userMap.put(player.getUniqueId(), new User(player));
    }

    public User getUser(Player player) {
        return this.userMap.getOrDefault(player.getUniqueId(), null);
    }

    public void removeUser(Player player) {
        this.userMap.remove(player.getUniqueId());
    }
}

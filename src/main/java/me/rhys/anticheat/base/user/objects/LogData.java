package me.rhys.anticheat.base.user.objects;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;

@Getter
@Setter
public class LogData {
    public LogObject getUser(String uuid) {
        for (LogObject user : Anticheat.getInstance().getLogObjectList()) {
            if (user.getUuid().equalsIgnoreCase(uuid)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(LogObject user) {
        if (!Anticheat.getInstance().getLogObjectList().contains(user)) {
            Anticheat.getInstance().getLogObjectList().add(user);
        }
    }

    public void removeUser(LogObject user) {
        if (Anticheat.getInstance().getLogObjectList().contains(user)) {
            Anticheat.getInstance().getLogObjectList().remove(user);
        }
    }
}
package me.rhys.anticheat.base.event;

import me.rhys.anticheat.base.user.User;

public interface CallableEvent {
    void onPacket(PacketEvent event);
    void setupTimers(User user);
    void onConnection(User user);
}

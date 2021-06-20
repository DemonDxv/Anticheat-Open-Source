package me.rhys.bedrock.base.event;

import me.rhys.bedrock.base.user.User;

public interface CallableEvent {
    void onPacket(PacketEvent event);
    void setupTimers(User user);
    void onConnection(User user);
}

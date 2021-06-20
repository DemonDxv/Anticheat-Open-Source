package me.rhys.bedrock.base.event;

import lombok.AllArgsConstructor;
import me.rhys.bedrock.base.user.User;

@AllArgsConstructor
public class EventManager {
    private final User user;

    public void processProcessors(PacketEvent packetEvent) {
        this.user.getProcessorManager().getProcessors().forEach(processor ->
                processor.onPacket(packetEvent));
    }

    public void processChecks(PacketEvent packetEvent) {
        this.user.getCheckManager().getCheckList().forEach(check ->
                check.onPacket(packetEvent));
    }
}

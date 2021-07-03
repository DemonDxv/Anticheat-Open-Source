package me.rhys.anticheat.checks.misc.pingspoof;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "PingSpoof", checkType = "B", lagBack = false, canPunish = false, description = "Detects Ping Spoofing")
public class PingSpoofB extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                if (user.shouldCancel() && user.getTick() < 60) {
                    return;
                }

                int pingK = user.getConnectionProcessor().getPing() + 250,
                        pingT = user.getConnectionProcessor().getTransPing();

                if (pingT > pingK) {
                    if (threshold++ > 20) {
                        flag(user, "Ping Spoofing "+pingK + " "+pingT);
                    }
                } else {
                    threshold -= Math.min(threshold, 0.25);
                }

                break;
            }
        }
    }
}
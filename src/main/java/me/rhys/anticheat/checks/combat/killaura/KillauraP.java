package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;

@CheckInformation(checkName = "Killaura", checkType = "P", canPunish = false, description = "Invalid movement while attacking")
public class KillauraP extends Check {

    private double lastYawDiff, lastDeltaXZ, threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (packet.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    double yaw = user.getMovementProcessor().getCurrentLocation().getYaw() -
                            user.getMovementProcessor().getLastLocation().getYaw();

                    double lastYaw = Math.abs(yaw - this.lastYawDiff);
                    double deltaXZ = user.getMovementProcessor().getDeltaXZ();
                    double difference = Math.abs(deltaXZ - this.lastDeltaXZ);

                    if (difference > 0 && difference < 0.001 && lastYaw > 7) {
                        if (++this.threshold > 2) {
                            this.flag(user, "Invalid motion with head movements");
                        }
                    } else {
                        this.threshold -= Math.min(this.threshold, 0.25);
                    }

                    this.lastDeltaXZ = deltaXZ;
                    this.lastYawDiff = yaw;
                }

                break;
            }


            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                if (user.getCombatProcessor().getUseEntityTimer().passed(0)) {
                    threshold = 0;
                }
                break;
            }
        }
    }
}
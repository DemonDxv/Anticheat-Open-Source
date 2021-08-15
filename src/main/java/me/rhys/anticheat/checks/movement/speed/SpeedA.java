package me.rhys.anticheat.checks.movement.speed;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Speed", punishmentVL = 8, description = "shit limit check")
public class SpeedA extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double maxSpeed = 0.36F;

                if (user.getMovementProcessor().getGroundTicks() > 7) {
                    maxSpeed = 0.2873D;

                    if (user.getBlockData().iceTimer.hasNotPassed(20)) {
                        maxSpeed = 0.4F;
                    }
                }

                if (user.getMovementProcessor().getGroundTicks() < 7) {
                    maxSpeed = 0.5;

                    if (user.getBlockData().iceTimer.hasNotPassed(20)) {
                        maxSpeed = 0.6F;
                    }

                    if (user.getBlockData().underBlockTicks > 0) {
                        maxSpeed = 0.5F;
                    }

                    if (user.getBlockData().underBlockTicks > 0 && user.getBlockData().iceTimer.hasNotPassed(20)) {
                        maxSpeed = 1.1F;
                    }
                }

                if (!user.getMovementProcessor().isOnGround() && user.getMovementProcessor().isLastGround()) {
                    maxSpeed = 0.62F;

                    if (user.getBlockData().underBlockTicks > 0) {
                        maxSpeed = 0.7F;
                    }

                    if (user.getBlockData().underBlockTicks > 0 && user.getBlockData().iceTimer.hasNotPassed(20)) {
                        maxSpeed = 1.2F;
                    }

                    if (user.getBlockData().iceTimer.hasNotPassed(20)) {
                        maxSpeed = 0.7F;
                    }
                }

                if (user.getMovementProcessor().isOnGround() && !user.getMovementProcessor().isLastLastGround()) {
                    maxSpeed = 0.42F;

                    if (user.getBlockData().stairSlabTimer.hasNotPassed(20)) {
                        maxSpeed = 0.45F;
                    }

                    if (user.getBlockData().iceTimer.hasNotPassed(20)) {
                        maxSpeed = 0.7F;
                    }

                    if (user.getBlockData().underBlockTicks > 0) {
                        maxSpeed = 0.5F;
                    }

                    if (user.getBlockData().underBlockTicks > 0 && user.getBlockData().iceTimer.hasNotPassed(20)) {
                        maxSpeed = 0.9F;
                    }

                }


                if (deltaXZ > maxSpeed) {

                }

                break;
            }
        }
    }
}
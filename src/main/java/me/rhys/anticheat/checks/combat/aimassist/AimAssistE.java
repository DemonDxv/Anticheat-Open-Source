package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "AimAssist", checkType = "E", lagBack = false, punishmentVL = 25, canPunish = false)
public class AimAssistE extends Check {

    private double offset = Math.pow(2.0, 24.0);

    private double mouseX, lastDeltaYaw, yawGCD;

    /**
     * This check is experimental so it may false or not work.
     */

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    float yawDifference = MathUtil.wrapAngleTo180_float(Math.abs(user.getCurrentLocation().getYaw()
                            - user.getLastLocation().getYaw()));

                    if (yawDifference > 99.99 && user.getMovementProcessor().getDeltaXZ() > 0.005) {
                        if (yawDifference != 360) {
                            flag(user, "Head Snapping", ""+yawDifference);
                        }
                    }
                }
                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

      /*          float yawDifference = Math.abs(user.getCurrentLocation().getYaw()
                        - user.getLastLocation().getYaw());

                yawGCD = MathUtil.gcd((long) (yawDifference * offset), (long) (lastDeltaYaw * offset));
                double yawGcd = yawGCD / offset;
                mouseX = (int) (Math.abs((yawDifference)) / yawGcd);

                lastDeltaYaw = yawDifference; */

                break;
            }

        }
    }
}

package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "AimAssist", checkType = "H", lagBack = false, punishmentVL = 5)
public class AimAssistH extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.USE_ENTITY: {

                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                    if (user.getTick() < 60 || user.shouldCancel()) {
                        return;
                    }

                    double deltaYaw = user.getMovementProcessor().getYawDeltaClamped();

                    double deltaMouse = Math.abs(deltaYaw - user.getMouseDeltaX());

                    if (deltaMouse > 60.0 && deltaMouse != 360 && deltaMouse < 360) {
                        if (deltaYaw >= 100) {
                            flag(user, "Head Snapping", "dm="+deltaMouse + ", y=" + deltaYaw);
                        }
                    }

                    break;
                }
            }
        }
    }
}

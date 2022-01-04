package me.rhys.anticheat.checks.combat.aimassist;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@CheckInformation(checkName = "AimAssist", checkType = "I", punishmentVL = 10, canPunish = false)
public class AimAssistI extends Check {

    private double threshold;

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

                    double deltaPitch = Math.abs(user.getCurrentLocation().getPitch() - user.getLastLocation().getPitch());

                    double mouseY = user.getMouseDeltaY();

                    if (mouseY > 10000 && deltaPitch < 4 && deltaPitch > 0.2) {
                        if (threshold++ > 5) {
                            flag(user, "Invalid pitch changes");
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.6f);
                    }

                }
                break;
            }
        }
    }
}

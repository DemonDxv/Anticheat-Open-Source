package me.rhys.anticheat.checks.movement.sneak;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.TimeUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Sneak", checkType = "B", canPunish = false, description = "Checks consistency of the players Sneaks")
public class SneakB extends Check {

    private List<Long> sneakList = new ArrayList<>();
    private double lastSTD;

    @Override
    public void onPacket(PacketEvent event) {

        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket actionPacket =
                        new WrappedInEntityActionPacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel()
                        || !user.isChunkLoaded()
                        || user.getTick() < 60) {
                    return;
                }

                if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING) {
                    sneakList.add(System.currentTimeMillis());

                    if (sneakList.size() == 25) {
                        double std = MathUtil.getStandardDeviation(sneakList);

                        double stdDiff = Math.abs(std - lastSTD);

                        if (stdDiff < .7) {
                            flag(user, "Invalid Sneaking");
                        }

                        lastSTD = std;
                        sneakList.clear();
                    }
                }

                break;
            }
        }
    }
}

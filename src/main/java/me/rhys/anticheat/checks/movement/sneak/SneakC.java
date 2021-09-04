package me.rhys.anticheat.checks.movement.sneak;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import me.rhys.anticheat.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Sneak", checkType = "C", canPunish = false, description = "Checks consistency of the players Sneaks")
public class SneakC extends Check {

    private List<Long> sneakList = new ArrayList<>();
    private double lastSTD, threshold;

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
                    this.sneakList.add(System.currentTimeMillis());

                    if (this.sneakList.size() == 5) {
                        double std = MathUtil.getStandardDeviation(this.sneakList);

                        double stdDiff = Math.abs(std - this.lastSTD);

                        if (stdDiff < 1) {
                            if (++threshold > 2) {
                                flag(user, "Invalid Sneaking");
                            }
                        } else {
                            threshold -= Math.min(threshold, 0.26);
                        }

                        this.lastSTD = std;
                        this.sneakList.clear();
                    }
                }

                break;
            }
        }
    }
}

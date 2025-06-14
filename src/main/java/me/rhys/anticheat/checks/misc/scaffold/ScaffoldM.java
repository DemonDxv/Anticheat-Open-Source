package me.rhys.anticheat.checks.misc.scaffold;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.TimeUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Scaffold", checkType = "M", lagBack = false, punishmentVL = 5)
public class ScaffoldM extends Check {

    private long time, lastTime;
    private double lastSTD;
    private final List<Long> placeTimes = new ArrayList<>();

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        if (event.getType().equals(Packet.Client.BLOCK_PLACE)) {
            WrappedInBlockPlacePacket blockPlace =
                new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

            if (user.shouldCancel() || user.getTick() < 60) {
                return;
            }


            if (blockPlace.getItemStack().getType().isBlock()) {
                if (user.getPlayer().getEyeLocation().add(0, -1, 0).getBlock().getType() == Material.AIR) {

                    int faceInt = blockPlace.getFace().b();

                    if (faceInt >= 0 && faceInt <= 3) {

                        time = System.currentTimeMillis();

                        long currentTime = TimeUtils.elapsed(time - lastTime);

                        placeTimes.add(currentTime);

                        if (placeTimes.size() > 9) {

                            double std = MathUtil.getStandardDeviation(placeTimes);

                            if (Math.abs(std - lastSTD) < 0.01) {
                                flag(user, "Consistent Time Between Packets Sent");
                            }

                            lastSTD = std;
                            placeTimes.clear();
                        }

                        lastTime = time;
                    } else {
                        lastSTD = Double.MIN_VALUE;
                        placeTimes.clear();
                    }
                }
            }
        }
    }
}

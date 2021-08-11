package me.rhys.anticheat.checks.combat.killaura;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Killaura", checkType = "K", lagBack = false, description = "Autoblock Delay Check", punishmentVL = 3)
public class KillauraK extends Check {

    private List<Integer> delays = new ArrayList<>();
    private int movements;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                movements++;
                break;
            }

            case Packet.Client.BLOCK_DIG: {

                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    if (movements < 10) {
                        delays.add(movements);

                        if (delays.size() == 25) {
                            double std = MathUtil.getStandardDeviation(delays);

                            if (std < 0.34) {
                                if (threshold++ > 1) {
                                    flag(user, "Blocking to consistent");
                                }
                            } else {
                                threshold -= Math.min(threshold, .5);
                            }

                            delays.clear();
                        }

                    }
                    movements = 0;
                }
                break;
            }
        }
    }
}
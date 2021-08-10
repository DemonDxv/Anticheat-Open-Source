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

    private Long lastSneak;
    private boolean lastSneaking;
    private List<Long> sneakList = new ArrayList<>();

    @Override
    public void onPacket(PacketEvent event) {

        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.POSITION:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LOOK: {

                sneakList.add(lastSneak);

                if (sneakList.size() == 25) {
                    double std = MathUtil.getStandardDeviation(sneakList);

                    Bukkit.broadcastMessage("" + std);

                    sneakList.clear();
                }

                break;
            }

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket actionPacket =
                        new WrappedInEntityActionPacket(event.getPacket(), user.getPlayer());

                if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING) {
                    lastSneak = System.currentTimeMillis();
                } else if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SNEAKING) {
                    lastSneak = System.currentTimeMillis();
                }

                break;
            }
        }
    }
}

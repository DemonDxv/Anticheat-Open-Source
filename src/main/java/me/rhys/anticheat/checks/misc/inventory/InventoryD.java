package me.rhys.anticheat.checks.misc.inventory;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInWindowClickPacket;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.TimeUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Inventory", checkType = "D", lagBack = false, punishmentVL = 9)
public class InventoryD extends Check {

    private int shiftClickTicks;
    private Long lastClickWindow;
    private List<Long> delays = new ArrayList<>();
    private double lastStd, threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.WINDOW_CLICK: {
                User user = event.getUser();

                WrappedInWindowClickPacket clickPacket = new WrappedInWindowClickPacket(event.getPacket(), user.getPlayer());

                if (user.shouldCancel() || user.getTick() < 60 || !user.isChunkLoaded()) {
                    return;
                }

                if (clickPacket.getAction().isShiftClick()) {
                    shiftClickTicks++;
                }

                if (clickPacket.getAction() == WrappedInWindowClickPacket.ClickType.DRAG || shiftClickTicks > 1) {
                    return;
                }

                if (clickPacket.getAction().isLeftClick() || clickPacket.getAction().isShiftClick()) {
                    long time = System.currentTimeMillis();

                    if (lastClickWindow != null) {
                        long change = TimeUtils.elapsed((time - lastClickWindow));

                        delays.add(change);

                        if (delays.size() == 10) {
                            double std = MathUtil.getStandardDeviation(delays);

                            double changeSTD = Math.abs(std - lastStd);

                            if (changeSTD < 3.0) {
                                if (threshold++ > 3) {
                                    flag(user, "Clicking abnormal in inventory");
                                }
                            } else {
                                threshold = Math.min(threshold, 0.7);
                            }

                            delays.clear();

                            lastStd = std;
                        }
                    }

                    lastClickWindow = time;
                }

                break;
            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                shiftClickTicks = 0;
                break;
            }
        }
    }
}

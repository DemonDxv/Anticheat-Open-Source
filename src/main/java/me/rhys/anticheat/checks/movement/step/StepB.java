package me.rhys.anticheat.checks.movement.step;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "Step", checkType = "B", description = "Checks if player goes up blocks non-legitimate", canPunish = false)
public class StepB extends Check {
}
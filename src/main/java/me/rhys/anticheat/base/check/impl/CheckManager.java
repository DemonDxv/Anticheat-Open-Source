package me.rhys.anticheat.base.check.impl;

import lombok.Getter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.checks.combat.aimassist.*;
import me.rhys.anticheat.checks.combat.autoclicker.*;
import me.rhys.anticheat.checks.combat.killaura.*;
import me.rhys.anticheat.checks.misc.badpackets.*;
import me.rhys.anticheat.checks.combat.velocity.*;
import me.rhys.anticheat.checks.misc.pingspoof.*;
import me.rhys.anticheat.checks.misc.scaffold.*;
import me.rhys.anticheat.checks.misc.timer.*;
import me.rhys.anticheat.checks.movement.flight.*;
import me.rhys.anticheat.checks.movement.speed.*;
import me.rhys.anticheat.checks.movement.step.*;
import me.rhys.anticheat.util.file.ChecksFile;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter
public class CheckManager {
    private final List<Check> checkList = new LinkedList<>();

    public void setupChecks(User user) {
        this.checkList.addAll(Anticheat.getInstance().getCheckManager().cloneChecks());
        this.checkList.forEach(check -> check.setupTimers(user));
    }
}
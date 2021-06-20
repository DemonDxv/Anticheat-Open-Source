package me.rhys.bedrock.base.check.impl;

import lombok.Getter;
import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.checks.combat.aimassist.*;
import me.rhys.bedrock.checks.combat.killaura.*;
import me.rhys.bedrock.checks.misc.badpackets.*;
import me.rhys.bedrock.checks.combat.velocity.*;
import me.rhys.bedrock.checks.misc.scaffold.*;
import me.rhys.bedrock.checks.misc.timer.*;
import me.rhys.bedrock.checks.movement.flight.*;
import me.rhys.bedrock.checks.movement.speed.*;

import java.util.LinkedList;
import java.util.List;

@Getter
public class CheckManager {
    private final List<Check> checkList = new LinkedList<>();

    public void setupChecks(User user) {
        //Velocity still under development
        this.checkList.add(new VelocityA());
        this.checkList.add(new VelocityB());
        this.checkList.add(new VelocityC());
        this.checkList.add(new VelocityD());
        this.checkList.add(new VelocityE());

        this.checkList.add(new KillauraA());
        this.checkList.add(new KillauraB());
        this.checkList.add(new KillauraC());

        this.checkList.add(new TimerA());
        this.checkList.add(new TimerB());

        this.checkList.add(new AimAssistA());

        this.checkList.add(new FlightA());
        this.checkList.add(new FlightB());
        this.checkList.add(new FlightC());
        this.checkList.add(new FlightD());
        this.checkList.add(new FlightE());

        this.checkList.add(new Speed());

        this.checkList.add(new ScaffoldA());
        this.checkList.add(new ScaffoldB());
        this.checkList.add(new ScaffoldC());
        this.checkList.add(new ScaffoldD());
        this.checkList.add(new ScaffoldE());

        this.checkList.add(new BadPacketsA());
        this.checkList.add(new BadPacketsB());
        this.checkList.add(new BadPacketsC());
        //this.checkList.add(new BadPacketsD());

        this.checkList.forEach(check -> {
            check.setup();
            check.setupTimers(user);
        });
    }
}

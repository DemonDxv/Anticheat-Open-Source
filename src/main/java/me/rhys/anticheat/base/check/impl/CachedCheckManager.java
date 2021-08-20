package me.rhys.anticheat.base.check.impl;

import lombok.Getter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.checks.combat.aimassist.*;
import me.rhys.anticheat.checks.combat.autoclicker.*;
import me.rhys.anticheat.checks.combat.hitbox.*;
import me.rhys.anticheat.checks.combat.killaura.*;
import me.rhys.anticheat.checks.combat.reach.*;
import me.rhys.anticheat.checks.combat.velocity.*;
import me.rhys.anticheat.checks.misc.badpackets.*;
import me.rhys.anticheat.checks.misc.inventory.*;
import me.rhys.anticheat.checks.misc.pingspoof.*;
import me.rhys.anticheat.checks.misc.scaffold.*;
import me.rhys.anticheat.checks.misc.timer.*;
import me.rhys.anticheat.checks.movement.flight.*;
import me.rhys.anticheat.checks.movement.jesus.*;
import me.rhys.anticheat.checks.movement.phase.*;
import me.rhys.anticheat.checks.movement.sneak.*;
import me.rhys.anticheat.checks.movement.speed.*;
import me.rhys.anticheat.checks.movement.sprint.*;
import me.rhys.anticheat.checks.movement.step.*;
import me.rhys.anticheat.checks.movement.strafe.*;
import me.rhys.anticheat.util.file.ChecksFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
public class CachedCheckManager {
    private final List<Check> checkList = new LinkedList<>();

    public void setup() {
        this.checkList.add(new VelocityA());
        this.checkList.add(new VelocityB());
        this.checkList.add(new VelocityC());
        this.checkList.add(new VelocityD());
        this.checkList.add(new VelocityE());
        this.checkList.add(new VelocityF());
       // this.checkList.add(new VelocityG());

        this.checkList.add(new AutoClickerA());
        this.checkList.add(new AutoClickerB());
        this.checkList.add(new AutoClickerC());
        this.checkList.add(new AutoClickerD());
        this.checkList.add(new AutoClickerE());
        this.checkList.add(new AutoClickerF());
        this.checkList.add(new AutoClickerG());

        this.checkList.add(new AimAssistA());
        this.checkList.add(new AimAssistB());
        this.checkList.add(new AimAssistC());
        this.checkList.add(new AimAssistD());
        this.checkList.add(new AimAssistE());
        this.checkList.add(new AimAssistF());
        this.checkList.add(new AimAssistG());
        this.checkList.add(new AimAssistH());
        this.checkList.add(new AimAssistI());
        this.checkList.add(new AimAssistJ());
        this.checkList.add(new AimAssistK());
        this.checkList.add(new AimAssistL());
        this.checkList.add(new AimAssistM());

        this.checkList.add(new KillauraA());
        this.checkList.add(new KillauraB());
        this.checkList.add(new KillauraC());
        this.checkList.add(new KillauraD());
        this.checkList.add(new KillauraE());
        this.checkList.add(new KillauraF());
        this.checkList.add(new KillauraG());
        this.checkList.add(new KillauraH());
        this.checkList.add(new KillauraI());
        this.checkList.add(new KillauraJ());
        this.checkList.add(new KillauraK());
        this.checkList.add(new KillauraL());
        this.checkList.add(new KillauraM());

        this.checkList.add(new ReachA());
        this.checkList.add(new ReachB());

        this.checkList.add(new HitboxA());

        this.checkList.add(new TimerA());

        this.checkList.add(new FlightA());
        this.checkList.add(new FlightB());
        this.checkList.add(new FlightC());
        this.checkList.add(new FlightD());
        this.checkList.add(new FlightE());
        this.checkList.add(new FlightF());

        this.checkList.add(new Speed());

        this.checkList.add(new PhaseA());
        this.checkList.add(new PhaseB());

        this.checkList.add(new Strafe());

        this.checkList.add(new StepA());
     //   this.checkList.add(new StepB());

        this.checkList.add(new JesusA());

        this.checkList.add(new SneakA());
       // this.checkList.add(new SneakB());

        this.checkList.add(new SprintA());
        this.checkList.add(new SprintB());

        this.checkList.add(new ScaffoldA());
        this.checkList.add(new ScaffoldB());
        this.checkList.add(new ScaffoldC());
        this.checkList.add(new ScaffoldD());
        this.checkList.add(new ScaffoldE());
        this.checkList.add(new ScaffoldF());
        this.checkList.add(new ScaffoldG());
        this.checkList.add(new ScaffoldH());

        this.checkList.add(new BadPacketsA());
     //   this.checkList.add(new BadPacketsB());
        this.checkList.add(new BadPacketsC());
        this.checkList.add(new BadPacketsD());
        this.checkList.add(new BadPacketsE());
        this.checkList.add(new BadPacketsF());
        this.checkList.add(new BadPacketsG());
        this.checkList.add(new BadPacketsH());
        this.checkList.add(new BadPacketsI());
        this.checkList.add(new BadPacketsJ());

        this.checkList.add(new InventoryA());
        this.checkList.add(new InventoryB());
        this.checkList.add(new InventoryC());
        this.checkList.add(new InventoryD());
        this.checkList.add(new InventoryE());
        this.checkList.add(new InventoryF());

        this.checkList.add(new PingSpoofA());
        this.checkList.add(new PingSpoofB());
        this.checkList.add(new PingSpoofC());

        //

        this.checkList.forEach(Check::setup);
        ChecksFile.getInstance().setup(Anticheat.getInstance());
        this.getFromFile();
        this.checkList.forEach(this::setupFile);
        ChecksFile.getInstance().saveData();
    }

    void getFromFile() {
        this.checkList.forEach(check -> {
            String enabledPath = String.format("Check.%s.enabled", check.getFriendlyName());
            String punishPath = String.format("Check.%s.violation.punish", check.getFriendlyName());
            String maxPath = String.format("Check.%s.violation.max", check.getFriendlyName());
            String lagbackPath = String.format("Check.%s.violation.lagback", check.getFriendlyName());

            if (check.getClass().isAnnotationPresent(CheckInformation.class)) {
                CheckInformation checkInformation = check.getClass().getAnnotation(CheckInformation.class);

                if (ChecksFile.getInstance().getData().contains(enabledPath)) {
                    check.setEnabled(ChecksFile.getInstance().getData().getBoolean(enabledPath));
                } else {
                    check.setEnabled(checkInformation.enabled());
                }

                if (ChecksFile.getInstance().getData().contains(punishPath)) {
                    check.setCanPunish(ChecksFile.getInstance().getData().getBoolean(punishPath));
                } else {
                    check.setCanPunish(checkInformation.canPunish());
                }

                if (ChecksFile.getInstance().getData().contains(maxPath)) {
                    check.setMaxViolation(ChecksFile.getInstance().getData().getInt(maxPath));
                } else {
                    check.setMaxViolation(checkInformation.punishmentVL());
                }

                if (ChecksFile.getInstance().getData().contains(lagbackPath)) {
                    check.setLagBack(ChecksFile.getInstance().getData().getBoolean(lagbackPath));
                } else {
                    check.setLagBack(checkInformation.lagBack());
                }
            }
        });
    }

    void setupFile(Check check) {
        ChecksFile.getInstance().getData().set(
                String.format("Check.%s.enabled", check.getFriendlyName()),
                check.isEnabled()
        );

        ChecksFile.getInstance().getData().set(
                String.format("Check.%s.violation.punish", check.getFriendlyName()),
                check.isCanPunish()
        );

        ChecksFile.getInstance().getData().set(
                String.format("Check.%s.violation.max", check.getFriendlyName()),
                check.getMaxViolation()
        );

        ChecksFile.getInstance().getData().set(
                String.format("Check.%s.violation.lagback", check.getFriendlyName()),
                check.isLagBack()
        );
    }

    public List<Check> cloneChecks() {
        List<Check> checks = new ArrayList<>();
        this.checkList.forEach(check -> checks.add(check.clone()));
        return checks;
    }

    public void saveChecks() {
        ChecksFile.getInstance().setup(Anticheat.getInstance());
        this.checkList.forEach(this::setupFile);
        ChecksFile.getInstance().saveData();
    }
}

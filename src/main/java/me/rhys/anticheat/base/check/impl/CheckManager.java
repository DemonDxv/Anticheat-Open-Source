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
        //  this.checkList.add(new VelocityE());


        this.checkList.add(new AutoClickerA());

        this.checkList.add(new AimAssistA());
        this.checkList.add(new AimAssistB());
        this.checkList.add(new AimAssistC());
        this.checkList.add(new AimAssistD());

        this.checkList.add(new KillauraA());
        this.checkList.add(new KillauraB());
        this.checkList.add(new KillauraC());
        this.checkList.add(new KillauraD());

        this.checkList.add(new TimerA());
        this.checkList.add(new TimerB());

        this.checkList.add(new FlightA());
        this.checkList.add(new FlightB());
        this.checkList.add(new FlightC());
        this.checkList.add(new FlightD());
        this.checkList.add(new FlightE());
        this.checkList.add(new FlightF());
        this.checkList.add(new FlightG());

        this.checkList.add(new Speed());

        this.checkList.add(new StepA());

        this.checkList.add(new ScaffoldA());
        this.checkList.add(new ScaffoldB());
        this.checkList.add(new ScaffoldC());
        this.checkList.add(new ScaffoldD());
        this.checkList.add(new ScaffoldE());

        this.checkList.add(new BadPacketsA());
        this.checkList.add(new BadPacketsB());
        this.checkList.add(new BadPacketsC());
        //this.checkList.add(new BadPacketsD());
        this.checkList.add(new BadPacketsE());
        this.checkList.add(new BadPacketsF());

        this.checkList.add(new PingSpoofA());
        this.checkList.add(new PingSpoofB());
        this.checkList.add(new PingSpoofC());


        this.checkList.forEach(check -> {
            check.setup();
            check.setupTimers(user);

            saveCheckConfig();
            loadCheckConfig();
        });

        ChecksFile.getInstance().reloadConfig();
    }


    /**
     * TODO: need to make a better/a more simple ver of this.
     */

    //Loads Check Config.

    public void loadCheckConfig() {
        ChecksFile.getInstance().setup(Anticheat.getInstance());

        for (Check check : checkList) {
            if (ChecksFile.getInstance().getData().get("check." + check.getCheckName() + ((check.getCheckType() != null)
                    ? check.getCheckType() : "A") + ".enabled") != null && !ChecksFile.getInstance().getData().
                    getBoolean("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.
                            getCheckName() : "A") + ".enabled")) {
                check.setEnabled(false);
            }
            if (ChecksFile.getInstance().getData().get("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A") + ".enabled") != null) {
                check.setEnabled(ChecksFile.getInstance().getData().getBoolean("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A") + ".enabled"));
                check.setMaxViolation(ChecksFile.getInstance().getData().getInt("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A") + ".vl"));
                check.setCanPunish(ChecksFile.getInstance().getData().getBoolean("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A") + ".ban"));
            } else {
                ChecksFile.getInstance().getData().set("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A") + ".enabled", check.isEnabled());
                ChecksFile.getInstance().saveData();
            }
        }
    }

    //Saves Check Config.
   public void saveCheckConfig() {
        ChecksFile.getInstance().saveConfig();
        for (Check check : checkList) {
            if (!ChecksFile.getInstance().getData().contains("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A"))) {
                ChecksFile.getInstance().getData()
                        .set("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A") + ".enabled", check.isEnabled());
                ChecksFile.getInstance().getData().set("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A") + ".vl", check.getMaxViolation());
                ChecksFile.getInstance().getData().set("check." + check.getCheckName() + ((check.getCheckType() != null) ? check.getCheckType() : "A") + ".ban", check.isCanPunish());
                ChecksFile.getInstance().saveData();
            }
        }
    }

    /**
     * TODO: make this work lol
     */

    public static void updateCheckState(User user, String n, String cd, boolean enabled) {

      /*  Check found = user.getCheckManager().getCheckList().parallelStream().filter(check ->
                Bukkit.broadcastMessage(""+check.getCheckName() + " "+check.getCheckType())).;
               // check.getCheckName().equalsIgnoreCase(n) &&
             //           check.getCheckType().equalsIgnoreCase(cd)).findAny().orElse(null);

        assert found != null;

        found.setEnabled(enabled);

        ChecksFile.getInstance().getData().set("check." + n + cd + ".enabled", enabled);
        ChecksFile.getInstance().saveData();*/

    }
}
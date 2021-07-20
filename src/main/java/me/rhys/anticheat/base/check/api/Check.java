package me.rhys.anticheat.base.check.api;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.event.CallableEvent;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.util.TPSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter @Setter
public class Check implements CallableEvent, Cloneable {
    public String checkName, checkType, description;
    public boolean enabled, punished, lagBack, canPunish;
    public int violation, maxViolation;


    public void setup() {
        if (getClass().isAnnotationPresent(CheckInformation.class)) {
            CheckInformation checkInformation = getClass().getAnnotation(CheckInformation.class);
            this.checkName = checkInformation.checkName();
            this.checkType = checkInformation.checkType();
            this.description = checkInformation.description();
        } else {
            Anticheat.getInstance().getLogger().warning("Unable to find CheckInformation annotation" +
                    " in the class: " + getClass().getSimpleName());
        }
    }

    public void flag(User user, String... data) {
        StringBuilder stringBuilder = new StringBuilder();

        if (data.length > 0) {
            for (String s : data) {
                stringBuilder.append(s).append(", ");
            }
        }

        if (TPSUtil.getTPS() <= 19.0) {
            return;
        }

        if (Anticheat.getInstance().getConfigValues().isPunish() && this.canPunish && this.violation > this.maxViolation) {
            this.violation = 0;

            punishPlayer(user);
        }

        String alert = Anticheat.getInstance().getConfigValues().getPrefix()
                + " " + ChatColor.RED + user.getPlayer().getName() +
                ChatColor.GRAY + " Failed " + ChatColor.RED + this.checkName
                + ChatColor.DARK_GRAY + " (" + ChatColor.RED + this.checkType + ChatColor.DARK_GRAY + ")"
                + ChatColor.DARK_GRAY + " " + ChatColor.RED + "x" + (this.violation++)
                + (data.length > 0 ? ChatColor.GRAY + " ["
                + ChatColor.GRAY + stringBuilder.toString().trim() + ChatColor.GRAY + "]" : "");

        if (Anticheat.getInstance().isBungeeCord()) {
            Anticheat.getInstance().sendMessageAlertBungee(user.getPlayer(), alert);

        } else {
      /*           Bukkit.getServer().getOnlinePlayers().parallelStream().filter(player ->
                       user.isAlerts() && (player.hasPermission("anticheat.alerts") ||
                             player.isOp())).forEach(player -> player.sendMessage(alert));*/
        }

        if (Anticheat.getInstance().getConfigValues().isLagBack()) {
            // LOL
            user.getMovementProcessor().setLagBackTicks((this.lagBack ? 3 : 0));
        }
    }

    @Override
    public void onPacket(PacketEvent event) {
        //
    }

    @Override
    public void setupTimers(User user) {
        //
    }

    @Override
    public void onConnection(User user) {
        //
    }

    public String getFriendlyName() {
        return this.checkName + this.checkType;
    }

    public Check clone() {
        try {
            return (Check) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void punishPlayer(User user) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Anticheat.getInstance().getConfigValues()
                        .getPunishCommand().replace("%PLAYER%", user.getPlayer().getName())
                        .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix())
                        .replaceFirst("/", ""));

                if (Anticheat.getInstance().getConfigValues().isAnnounce()) {
                    Bukkit.broadcastMessage(Anticheat.getInstance().getConfigValues().getAnnounceMessage()
                            .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix())
                            .replace("%PLAYER%", user.getPlayer().getName()));
                }
            }
        }.runTask(Anticheat.getInstance());
    }
}

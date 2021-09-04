package me.rhys.anticheat.base.check.api;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.event.CallableEvent;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.database.api.InputData;
import me.rhys.anticheat.discord.DiscordWebhook;
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
    private int violation, maxViolation;


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

        if (TPSUtil.getTPS() <= 19.0
                || (user.getPlayer().hasPermission("anticheat.bypass") && !user.getPlayer().isOp()
                || user.getPlayer().isOp() && Anticheat.getInstance().getConfigValues().isAllowOp()
                && user.getPlayer().hasPermission("anticheat.bypass"))) {
            return;
        }

        if (Anticheat.getInstance().getConfigValues().isPunish() && !user.isBanned()
                && this.canPunish && this.violation > this.maxViolation) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Anticheat.getInstance().getConfigValues()
                            .getPunishCommand()
                            .replace("%MAX-VL%", String.valueOf(maxViolation))
                            .replace("%CHECK%", checkName)
                            .replace("%CHECKTYPE%", checkType)
                            .replace("%VL%", String.valueOf(violation))
                            .replace("%PLAYER%", user.getPlayer().getName())
                            .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix())
                            .replaceFirst("/", ""));

                    if (Anticheat.getInstance().getConfigValues().isAnnounce()) {
                        Bukkit.broadcastMessage(Anticheat.getInstance().getConfigValues().getAnnounceMessage()
                                .replace("%MAX-VL%", String.valueOf(maxViolation))
                                .replace("%CHECK%", checkName)
                                .replace("%CHECKTYPE%", checkType)
                                .replace("%VL%", String.valueOf(violation))
                                .replace("%PLAYER%", user.getPlayer().getName())
                                .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix()));
                    }
                }
            }.runTask(Anticheat.getInstance());
            user.setBanned(true);
            this.violation = 0;
        }

        String alert = Anticheat.getInstance().getConfigValues().getAlertsMessage()
                .replace("%MAX-VL%", String.valueOf(maxViolation))
                .replace("%PLAYER%", user.getPlayer().getName())
                .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix())
                .replace("%CHECK%", checkName)
                .replace("%CHECKTYPE%", checkType)
                .replace("%VL%", String.valueOf(violation))
                .replace("%DEBUG%", (data.length > 0 ? ChatColor.GRAY + " ["
                        + ChatColor.GRAY + stringBuilder.toString().trim() + ChatColor.GRAY + "]" : ""));

        String discordAlert = Anticheat.getInstance().getConfigValues().getDiscordAlerts()
                .replace("%MAX-VL%", String.valueOf(maxViolation))
                .replace("%PLAYER%", user.getPlayer().getName())
                .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix())
                .replace("%CHECK%", checkName)
                .replace("%CHECKTYPE%", checkType)
                .replace("%VL%", String.valueOf(violation))
                .replace("%DEBUG%", (data.length > 0 ? " ["
                       + stringBuilder.toString().trim()  + "]" : ""));

        if (isCanPunish() && !user.isBanned()) {
            violation++;
        }


        if (Anticheat.getInstance().getConfigValues().isConsoleAlerts()) {
            Anticheat.getInstance().getServer().getConsoleSender().sendMessage(alert);
        }

        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            User staff = Anticheat.getInstance().getUserManager().getUser(player);

            if (staff != null) {
                if (staff.isAlerts() && (staff.getPlayer().hasPermission("anticheat.alerts")
                        || staff.getPlayer().isOp())) {
                    staff.getPlayer().sendMessage(alert);
                }
            }
        });


        if (Anticheat.getInstance().getConfigValues().isDiscord()) {
            Anticheat.getInstance().getDiscordWebhook().addEmbed(
                    new DiscordWebhook.EmbedObject().setDescription(discordAlert));

            try {
                Anticheat.getInstance().getDiscordWebhook().execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (Anticheat.getInstance().getConfigValues().isLagBack()) {
            // LOL
            user.getMovementProcessor().setLagBackTicks((this.lagBack ? 3 : 0));
        }

        int ping = user.getConnectionProcessor().getTransPing();
        boolean banned = user.isBanned();

        if (Anticheat.getInstance().getConfigValues().isLogs()) {
            user.getLogObject().logUtil.addLog(user.getPlayer().getName(),
                    this.checkName, checkType, violation, maxViolation, !canPunish, ping, banned);
        }

        if (user.isBanned()) {
            user.getLogObject().logUtil.addLogString(user.getPlayer().getName()
                    + " has been banned for unfair advantages.");
        }

        if (user.getFlaggedChecks().containsKey(this)) {
            user.getFlaggedChecks().put(this, user.getFlaggedChecks().get(this) + 1);
        } else user.getFlaggedChecks().put(this, 1);

        Anticheat.getInstance().getDatabaseManager().getLogQueue().add(
                new InputData(
                        user.getPlayer().getUniqueId().toString(),
                        user.getPlayer().getName(),
                        this.checkName,
                        this.checkType,
                        user.getFlaggedChecks().getOrDefault(this, 1),
                        false
                )
        );
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
        user.setBanned(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/ban %PLAYER% %PREFIX% Unfair Advantage!"
                        .replace("%MAX-VL%", String.valueOf(maxViolation))
                        .replace("%CHECK%", checkName)
                        .replace("%CHECKTYPE%", checkType)
                        .replace("%VL%", String.valueOf(violation))
                        .replace("%PLAYER%", user.getPlayer().getName())
                        .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix())
                        .replaceFirst("/", ""));

                if (Anticheat.getInstance().getConfigValues().isAnnounce()) {
                    Bukkit.broadcastMessage(Anticheat.getInstance().getConfigValues().getAnnounceMessage()
                            .replace("%MAX-VL%", String.valueOf(maxViolation))
                            .replace("%CHECK%", checkName)
                            .replace("%CHECKTYPE%", checkType)
                            .replace("%VL%", String.valueOf(violation))
                            .replace("%PLAYER%", user.getPlayer().getName())
                            .replace("%PREFIX%", Anticheat.getInstance().getConfigValues().getPrefix()));
                }
            }
        }.runTask(Anticheat.getInstance());
    }
}

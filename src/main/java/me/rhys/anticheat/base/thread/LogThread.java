package me.rhys.anticheat.base.thread;

import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.util.file.LogsFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LogThread {
    public List<String> list = new ArrayList<>();

    public LogThread() {

        Anticheat.getInstance().getLogService().scheduleAtFixedRate(() ->

                Anticheat.getInstance().getUserManager().getUserMap().forEach((uuid, user) -> {

                    LogsFile.getInstance().setup(Anticheat.getInstance(), user.getPlayer().getName());

                    list = LogsFile.getInstance().getData().getStringList("Logs");

                    list.addAll(user.getLogObject().logUtil.getLogs());

                    LogsFile.getInstance().getData().set("Logs", list);

                    LogsFile.getInstance().saveData();
                    user.getLogObject().logUtil.clear();
                }), Anticheat.getInstance().getConfigValues().getLogTime(), 1L, TimeUnit.SECONDS);
    }
}

package me.rhys.anticheat.base.check.impl;

import lombok.Getter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.user.User;

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
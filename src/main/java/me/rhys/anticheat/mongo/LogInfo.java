package me.rhys.anticheat.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class LogInfo {

    @Getter
    private static List<LogInfo> queue = new ArrayList<>();
    private String player;
    private String check;
    private String checktype;
    private int violation;
}
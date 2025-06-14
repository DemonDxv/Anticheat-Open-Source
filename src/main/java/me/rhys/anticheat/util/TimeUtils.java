package me.rhys.anticheat.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by FlyCode on 30/04/2018 Package cc.flycode.Supreme.util
 */
public class TimeUtils {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    public static long CurrentMS() {
        return System.currentTimeMillis();
    }

    public static boolean Passed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static boolean elapsed(long time, long needed) {
        return Math.abs(System.currentTimeMillis() - time) >= needed;
    }

    public static String GetDate() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return String.valueOf(format.format(now));
    }

    public static long Remainder(long start, long required) {
        return required + start - System.currentTimeMillis();
    }

    public static long elapsed(long time) {
        return System.currentTimeMillis() - time;
    }

    public static long secondsFromLong(long time) {
        long now = System.currentTimeMillis();
        long date = now - time;
        return date / 1000 % 60;
    }

    public static String getSystemTime() {
        String out;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        out = formatter.format(date);
        return out;
    }
}

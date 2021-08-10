package me.rhys.anticheat.util.math;

import java.util.function.Consumer;

public class VanillaMath {
    private static final float[] SIN = make(new float[65536], arrf -> {
        for (int i = 0; i < arrf.length; ++i) {
            arrf[i] = (float) Math.sin((double) i * 3.141592653589793 * 2.0 / 65536.0);
        }
    });

    public static float sin(float f) {
        return SIN[(int) (f * 10430.378f) & 0xFFFF];
    }

    public static float cos(float f) {
        return SIN[(int) (f * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }

    public static <T> T make(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }
}

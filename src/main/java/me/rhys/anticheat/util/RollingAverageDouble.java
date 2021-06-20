package me.rhys.anticheat.util;


import java.util.Arrays;

public class RollingAverageDouble
{
    private final int size;
    private final double[] array;
    private int index;
    private double average;

    public RollingAverageDouble(int size, double initial) {
        this.size = size;
        this.array = new double[size];
        this.average = initial;
        initial /= size;
        Arrays.fill(this.array, initial);
    }

    public void add(double value) {
        value /= this.size;
        this.average -= this.array[this.index];
        this.average += value;
        this.array[this.index] = value;
        this.index = (this.index + 1) % this.size;
    }

    public double getAverage() {
        return this.average;
    }
}

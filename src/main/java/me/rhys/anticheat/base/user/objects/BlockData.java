package me.rhys.anticheat.base.user.objects;

import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.util.EventTimer;

public class BlockData {
    public boolean door, lillyPad, skull, cake, carpet, collidesHorizontal, onGround = false, lastOnGround = false, nearWater, nearLava, nearIce, climbable, slime, piston, snow, fence,
            bed, stair, slab, underBlock, web, shulker, insideBlock;
    public int lillyPadTicks, lavaTicks, waterTicks, pistonTicks, skullTicks, cakeTicks, carpetTicks, liquidTicks, climbableTicks, iceTicks, slimeTicks, snowTicks, fenceTicks, bedTicks,
            stairTicks, slabTicks, underBlockTicks, webTicks, shulkerTicks;
    public double lastBlockY;
    public EventTimer movingUpTimer, climbableTimer, iceTimer, slimeTimer, stairSlabTimer, blockAboveTimer;

    public void setupTimers(User user) {
        this.movingUpTimer = new EventTimer(20, user);
        this.climbableTimer = new EventTimer(60, user);
        this.iceTimer = new EventTimer(100, user);
        this.slimeTimer = new EventTimer(60, user);
        this.stairSlabTimer = new EventTimer(100, user);
        this.blockAboveTimer = new EventTimer(60, user);
    }
}

package me.rhys.anticheat.base.processor.impl.processors;

import lombok.Getter;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.processor.api.Processor;
import me.rhys.anticheat.base.processor.api.ProcessorInformation;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutExplosionPacket;
import me.rhys.anticheat.tinyprotocol.packet.types.MathHelper;
import me.rhys.anticheat.util.*;
import me.rhys.anticheat.util.math.OptifineMath;
import me.rhys.anticheat.util.math.VanillaMath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;

@ProcessorInformation(name = "Prediction")
@Getter
public class PredictionProcessor extends Processor {

    private double motionXZ;
    private float blockFriction = 0.91F;

    private boolean hit = false, useSword, dropItem;
    private EventTimer lastSlotChange;
    public boolean fastMath, fMath = false;
    private double lastExpX, lastExpZ, explosionSpeed, lastDeltaXZ, lastDeltaX, lastDeltaZ;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getMovementProcessor().isLastLastGround()) {

                    blockFriction = 0.91F * 0.6F;

                    if (user.getBlockData().slimeTimer.hasNotPassed(20)
                            || user.getMovementProcessor().isBouncedOnSlime()) {
                        blockFriction = 0.91F * 0.8F;
                    }

                    if (user.getBlockData().iceTimer.hasNotPassed(20)) {
                        blockFriction = 0.91F * 0.98F;
                    }

                    if (user.getBlockData().iceTimer.hasNotPassed(20)
                            && user.getBlockData().slimeTimer.hasNotPassed(20)) {
                        blockFriction = (0.91F * 0.8F) * 0.98F;
                    }

                } else {
                    blockFriction = 0.91F;
                }

                if (dropItem) {
                    useSword = false;
                }

                dropItem = false;


                if (lastSlotChange.hasNotPassed(9)) {
                    useSword = false;
                }

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(20)) {
                    hit = true;
                } else {
                    hit = false;
                }

                double deltaX = Math.abs(Math.abs(user.getCurrentLocation().getX())
                        - Math.abs(user.getLastLocation().getX()));
                double deltaZ = Math.abs(Math.abs(user.getCurrentLocation().getZ())
                        - Math.abs(user.getLastLocation().getZ()));

                //Assume the player is sprinting because checking if the player is sprinting is unreliable.
                double deltaXZ = Math.hypot(deltaX, deltaZ);

                double prediction = lastDeltaXZ * blockFriction;

                PlayerLocation to = user.getMovementProcessor().getCurrentLocation(),
                        from = user.getMovementProcessor().getLastLocation();

                double preD = 0.01D;

                double mx = to.getX() - from.getX();
                double mz = to.getZ() - from.getZ();

                float motionYaw = (float) (Math.atan2(mz, mx) * 180.0D / Math.PI) - 90.0F;

                int direction;

                motionYaw -= to.getYaw();

                while (motionYaw > 360.0F)
                    motionYaw -= 360.0F;
                while (motionYaw < 0.0F)
                    motionYaw += 360.0F;

                motionYaw /= 45.0F;

                float moveS = 0.0F;
                float moveF = 0.0F;

                if (Math.abs(Math.abs(mx) + Math.abs(mz)) > preD) {
                    direction = (int) new BigDecimal(motionYaw).setScale(1, RoundingMode.HALF_UP).doubleValue();

                    if (direction == 1) {
                        moveF = 1F;
                        moveS = -1F;


                    } else if (direction == 2) {
                        moveS = -1F;


                    } else if (direction == 3) {
                        moveF = -1F;
                        moveS = -1F;


                    } else if (direction == 4) {
                        moveF = -1F;

                    } else if (direction == 5) {
                        moveF = -1F;
                        moveS = 1F;

                    } else if (direction == 6) {
                        moveS = 1F;

                    } else if (direction == 7) {
                        moveF = 1F;
                        moveS = 1F;

                    } else if (direction == 8) {
                        moveF = 1F;

                    } else if (direction == 0) {
                        moveF = 1F;
                    }
                }

                moveS *= 0.98F;
                moveF *= 0.98F;

                float strafe = moveS, forward = moveF;
                float f = strafe * strafe + forward * forward;

                float friction;

                float var3 = (0.6F * 0.91F);
                float getAIMoveSpeed = MathUtil.getWalkSpeed(user.getPlayer()) + .00000001F;


            /*    if (user.getPotionProcessor().getSpeedTicks() > 0) {
                    switch (MathUtil.getPotionEffectLevel(user.getPlayer(), PotionEffectType.SPEED)) {
                        case 0: {
                            getAIMoveSpeed = 0.23400002F;
                            break;
                        }


                        case 1: {
                            getAIMoveSpeed = 0.156F;
                            break;
                        }

                        case 2: {
                            getAIMoveSpeed = 0.18200001F;
                            break;
                        }

                        case 3: {
                            getAIMoveSpeed = 0.208F;
                            break;
                        }

                        case 4: {
                            getAIMoveSpeed = 0.23400001F;
                            break;
                        }

                    }
                }*/

                float var4 = 0.16277136F / (var3 * var3 * var3);

                if (user.getMovementProcessor().isLastGround()) {
                    friction = getAIMoveSpeed * var4;
                } else {
                    friction = 0.026F;
                }

                float f4 = 0.02F;
                float f5 = 0.8F;

                if (user.getBlockData().nearWater) {


                    if (user.getPlayer().getInventory().getBoots() != null
                            && user.getPlayer().getInventory().getBoots().getEnchantments() != null) {

                        float f3 = user.getPlayer().getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);

                        if (f3 > 3.0F) {
                            f3 = 3.0F;
                        }

                        if (!user.getMovementProcessor().isLastGround()) {
                            f3 *= 0.5F;
                        }

                        if (f3 > 0.0F) {
                            f5 += (0.54600006F - f5) * f3 / 3.0F;
                            f4 += (getAIMoveSpeed * 1.0F - f4) * f3 / 3.0F;
                        }

                        friction = f4;

                        blockFriction = f5;
                    }

                    if (user.getBlockData().liquidTicks < 20) {
                        prediction += 0.03F;
                    }
                }


                if (f >= 1.0E-4F) {
                    f = (float) Math.sqrt(f);
                    if (f < 1.0F) {
                        f = 1.0F;
                    }
                    f = friction / f;
                    strafe = strafe * f;
                    forward = forward * f;

                    float f1 = (float) Math.sin(user.getMovementProcessor().getYawDeltaClamped()
                            * (float) Math.PI / 180.0F);
                    float f2 = (float) Math.cos(user.getMovementProcessor().getYawDeltaClamped()
                            * (float) Math.PI / 180.0F);

                    float motionXAdd = (strafe * f2 - forward * f1);
                    float motionZAdd = (forward * f2 + strafe * f1);
                    prediction += Math.hypot(motionXAdd, motionZAdd);
                }

                double deltaY = user.getMovementProcessor().getDeltaY();

                double jumpHeight = 0.42F;

                if (!user.getMovementProcessor().isOnGround()
                        && user.getMovementProcessor().isLastGround()
                        && (deltaY == jumpHeight || deltaY > 0.4044f && deltaY < .406F)) {
                    prediction += 0.2F;
                }

                if (!user.getMovementProcessor().isOnGround()
                        && user.getMovementProcessor().isLastGround() && deltaY > 0.42F) {
                    prediction += 0.2F;
                }


                if (user.getBlockData().pistonTicks > 0) {
                    prediction += .5;
                }

                if (user.getLastSuffocationTimer().hasNotPassed(5 + user.getConnectionProcessor().getClientTick())
                        && user.getBlockData().insideBlock) {
                    prediction += 0.1;
                }

                if (user.getBlockData().collideSlimeTimer.hasNotPassed(20)) {
                    prediction += 0.5;
                }

                if (user.getCombatProcessor().getVelocityTicks() <= (5
                        + (user.getConnectionProcessor().getClientTick() + 5))) {
                    prediction += user.getCombatProcessor().getVelocityHNoTrans();
                }

                if (user.getLastFallDamageTimer().hasNotPassed(20 + user.getConnectionProcessor().getClientTick())) {
                    prediction += 0.4;
                }

                if (user.getBlockData().carpetTicks > 0) {
                    prediction += 0.1F;
                }

                if (user.getBlockData().climbable) {
                    prediction += 0.1f;
                }

                if (user.getLastExplosionTimer().hasNotPassed(20 + user.getConnectionProcessor().getClientTick())
                                && user.getConnectionProcessor().getClientTick() < 20) {
                    prediction += explosionSpeed;
                }

                if (user.getLastBlockPlaceTimer().hasNotPassed(10
                        + user.getConnectionProcessor().getClientTick())
                        && !user.getMovementProcessor().isOnGround() && user.getMovementProcessor().isLastGround()) {
                    prediction += 0.04f;
                }

                motionXZ = deltaXZ - prediction;


                this.lastDeltaX = deltaX;
                this.lastDeltaZ = deltaZ;

                lastDeltaXZ = Math.hypot(lastDeltaX, lastDeltaZ);
                break;
            }

            case Packet.Server.EXPLOSION: {
                WrappedOutExplosionPacket explosionPacket =
                        new WrappedOutExplosionPacket(event.getPacket(), user.getPlayer());

                double expX = explosionPacket.getMotionX(), expZ = explosionPacket.getMotionZ();

                double expDeltaX = Math.abs(Math.abs(expX)
                        - Math.abs(lastExpX));
                double expDeltaZ = Math.abs(Math.abs(expZ)
                        - Math.abs(lastExpZ));

                explosionSpeed = Math.hypot(expDeltaX, expDeltaZ);


                this.lastExpX = expX;
                this.lastExpZ = expZ;
                break;
            }

            case Packet.Client.BLOCK_PLACE: {

                WrappedInBlockPlacePacket wrappedInBlockPlacePacket = new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (!wrappedInBlockPlacePacket.getItemStack().getType().isBlock()) {

                    if (wrappedInBlockPlacePacket.getPosition().getX() == -1
                            && wrappedInBlockPlacePacket.getPosition().getY() == -1 && wrappedInBlockPlacePacket.getPosition().getZ() == -1) {

                        if (wrappedInBlockPlacePacket.getItemStack().getType().name().toLowerCase().contains("sword")) {
                            if (!hit) {
                                useSword = true;
                            }
                        }
                    }
                }

                break;
            }

            case Packet.Client.BLOCK_DIG: {

                WrappedInBlockDigPacket wrappedInBlockDigPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                useSword = false;

                if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    useSword = false;
                } else if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.DROP_ITEM) {
                    dropItem = true;
                }

                break;
            }

            case Packet.Server.HELD_ITEM:
            case Packet.Client.HELD_ITEM_SLOT: {
                useSword = false;
                lastSlotChange.reset();
                break;
            }

            case Packet.Client.USE_ENTITY: {

                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    hit = true;
                }

                break;
            }
        }
    }

    @Override
    public void setupTimers(User user) {
        this.lastSlotChange = new EventTimer(20, user);
    }
}
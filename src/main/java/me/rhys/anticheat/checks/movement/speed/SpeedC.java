package me.rhys.anticheat.checks.movement.speed;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.processor.impl.processors.MovementProcessor;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.util.MathUtil;

@CheckInformation(checkName = "Speed", checkType = "C", punishmentVL = 21)
public class SpeedC extends Check {

    private double nextPredSpeed;
    private double lastJumpGroundY = 0;
    private double nextTickBuffer;
    private double normalFrictionBuffer;

    private double buffer;


    // Shoutout the man with the fastest spartan speed!

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getTick() < 60
                        || user.getVehicleTicks() > 0
                        || user.shouldCancel()
                        || user.getLastTeleportTimer().hasNotPassed(5 + user.getConnectionProcessor().getClientTick())
                        || user.getMovementProcessor().isBouncedOnSlime()
                        || user.getActionProcessor().getRespawnTimer().hasNotPassed(20)
                        || user.getPlayer().isDead()
                        || !user.isChunkLoaded()
                        || user.getBlockData().waterTicks > 0
                        || user.getPlayer().getWalkSpeed() != 0.2F
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
                        || user.getElytraProcessor().isUsingElytra()) {
                    return;
                }

                double pred;

                MovementProcessor movementProcessor = user.getMovementProcessor();
                boolean jump = movementProcessor.getAirTicks() == 1;

                if (movementProcessor.getAirTicks() > 0) {
                    pred = movementProcessor.getLastDeltaXZ();

                    if (movementProcessor.getAirTicks() == 1 && movementProcessor.getDeltaY() > 0) {
                        pred = (pred - 0.66f * (pred - getGroundSpeedIgnoreSprint(user))) * 1.77969935f; // magic value :scream:
                        if (movementProcessor.getLastLocation().getY() - 0.5 == lastJumpGroundY ||
                                movementProcessor.getLastLocation().getY() - 1 == lastJumpGroundY) { // account for yes
                            pred *= 1.03f;
                        }
                        if (movementProcessor.isSprinting() && movementProcessor.getAirTicks() == 1) {
                            pred *= 1.05f;
                        }
                        if (user.getBlockData().underBlockTicks <= 4) {
                            pred *= 1.085f;
                        }

                        if (movementProcessor.getLastLocation().getY() % 0.015625 == 0) {
                            lastJumpGroundY = movementProcessor.getLastLocation().getY();
                        }

                        nextPredSpeed = pred * 0.91f;
                        nextPredSpeed += 0.026f;

                        if (movementProcessor.getDeltaXZ() > pred) {
                            if (!shouldExempt(user)) {
                                if (++buffer >= 4) {
                                    this.flag(user, "Friction", "predicted= " + pred, "motionXZ= " + movementProcessor.getDeltaXZ(),
                                            String.valueOf(movementProcessor.getAirTicks()),
                                            String.valueOf(movementProcessor.getGroundTicks()));
                                }
                            }
                        } else {
                            buffer -= Math.min(buffer, 0.15);
                        }
                        return;
                    } else {
                        pred *= 0.91f;
                        pred += 0.026f;
                        if (movementProcessor.isLastGround() && pred <= getStrictGroundSpeed(user) * 1.3f) {
                            pred *= 1.5f;
                        }
                    }
                } else { // ground shit
                    pred = getGroundSpeed(user);
                    if (movementProcessor.getGroundTicks() > 10) {
                        pred = getStrictGroundSpeed(user);
                    }


                    if (user.getBlockData().iceTicks <= 20 && movementProcessor.getGroundTicks() == 1) {
                        pred *= 1.01f;
                    }

                    if (movementProcessor.getGroundTicks() <= 5 && movementProcessor.getGroundTicks() > 1) {
                        pred += getGroundSpeed(user) / (movementProcessor.getGroundTicks() * 2);
                    } else if (movementProcessor.getGroundTicks() == 1) {
                        pred += getGroundSpeed(user) * 0.1f;
                    }

                    if (user.getBlockData().underBlockTicks > 16) {
                        if (user.getBlockData().iceTicks > 16) {
                            if (movementProcessor.getDeltaXZ() * 0.7f > pred) {
                                pred *= 2f;
                            } else {
                                pred *= 1.8f;
                            }
                        } else {
                            pred *= 1.25f;
                        }
                    }
                }

                if (movementProcessor.getAirTicks() == 1 && movementProcessor.isSprinting() &&
                        movementProcessor.getDeltaY() > 0) return; // already checked so return


                if (movementProcessor.getDeltaXZ() > pred) {
                    if (!shouldExempt(user)) {
                        if (normalFrictionBuffer++ >= 3) {
                            this.flag(user, "Friction", "predicted= " + pred, "motionXZ= " + movementProcessor.getDeltaXZ(),
                                    String.valueOf(movementProcessor.getAirTicks()),
                                    String.valueOf(movementProcessor.getGroundTicks()));
                        }
                    }
                } else if (movementProcessor.getDeltaXZ() > nextPredSpeed && nextPredSpeed >= (movementProcessor.getAirTicks() > 0 ? 0.33 : 0.29f) &&
                        !movementProcessor.isOnGround()) {
                    if (!shouldExempt(user)) {
                        if (nextTickBuffer++ >= 3) {
                            this.flag(user, "Next Tick Friction", "predicted= " +
                                            nextPredSpeed,
                                    "motionXZ= " + movementProcessor.getDeltaXZ(),
                                    String.valueOf(movementProcessor.getAirTicks()),
                                    String.valueOf(movementProcessor.getGroundTicks()));
                        }
                    }
                } else {
                    nextTickBuffer-= 0.025;
                }

                normalFrictionBuffer = MathUtil.clampDouble(normalFrictionBuffer - 0.1, 0, 3);
                nextTickBuffer = MathUtil.clampDouble(nextTickBuffer, 0, 3);
                nextPredSpeed *= 0.91f;
                nextPredSpeed += 0.026;

                break;
            }
        }
    }


    private boolean shouldExempt(User user) {
        return user.getBlockData().waterTicks > 0 ||
                user.getLastTeleportTimer().hasNotPassed(20) || user.getBlockData().web ||
                user.getBlockData().climbableTicks > 0 || user.getTick() <= 50 ||
                user.getBlockData().iceTicks > 0 || user.getCombatProcessor().getVelocityTicks() <= 20 ||
                user.getMovementProcessor().getAirTicks() == 1
                        && user.getMovementProcessor().getDeltaXZ() <= 0.27f;
    }

    public double getGroundSpeed(User user) {
        double baseSpeed = (user.getPlayer().isSprinting() ? 0.3f : 0.262f)
                + (user.getPlayer().getWalkSpeed() > 0.2 ? user.getPlayer().getWalkSpeed() +
                ((user.getPlayer().getWalkSpeed() * 10) * 0.03) : user.getPlayer().getWalkSpeed()) - 0.2;
        baseSpeed = applyPotionModifiers(user, baseSpeed);
        return baseSpeed;
    }

    public double getGroundSpeedIgnoreSprint(User user) {
        double baseSpeed = 0.3f + (user.getPlayer().getWalkSpeed() > 0.2 ? user.getPlayer().getWalkSpeed() +
                ((user.getPlayer().getWalkSpeed() * 10) * 0.03) : user.getPlayer().getWalkSpeed()) - 0.2;
        return applyPotionModifiers(user, baseSpeed);
    }

    public double getStrictGroundSpeed(User user) {
        double baseSpeed = 0.0865857605525953 + user.getPlayer().getWalkSpeed() + ((user.getPlayer().getWalkSpeed() * 10) * 0.031);
        return applyPotionModifiers(user, baseSpeed);
    }

    public double applyPotionModifiers(User user, double baseSpeed) {
        if (user.getPotionProcessor().isHasSpeed()) {
            double amplifier = user.getPotionProcessor().getSpeedAmplifier();
            baseSpeed *= (1.0D + 0.2 * (amplifier + 0.5));
        }
        return baseSpeed;
    }
}
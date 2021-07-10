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
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.box.ReflectionUtil;
import me.rhys.anticheat.util.box.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

@ProcessorInformation(name = "Prediction")
@Getter
public class PredictionProcessor extends Processor {

    private double motionXZ;
    private float blockFriction = 0.91F;

    private boolean hit = false, useSword, dropItem;

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

                    if (user.getBlockData().slimeTimer.hasNotPassed(20)) {
                        blockFriction = 0.91F * 0.8F;
                    }

                    if (user.getBlockData().iceTimer.hasNotPassed(20)) {
                        blockFriction = 0.91F * 0.98F;
                    }

                } else {
                    blockFriction = 0.91F;
                }

                if (dropItem) {
                    useSword = false;
                }

                dropItem = false;

                if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(20)) {
                    hit = true;
                } else {
                    hit = false;
                }

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();
                double lastDeltaXZ = user.getMovementProcessor().getLastDeltaXZ();

                double deltaY = user.getMovementProcessor().getDeltaY();

                double prediction = lastDeltaXZ * blockFriction;

                prediction += MathUtil.movingFlyingV3(user);

                boolean jumpCheck = deltaY == 0.42f || deltaY >= .404f && deltaY <= .406f;

                if (deltaY > 0.005 && user.getMovementProcessor().isServerYGround()
                        && user.getMovementProcessor().isLastGround()) {
                    prediction += 0.2F;
                }

                if (!user.getMovementProcessor().isOnGround()
                        && user.getMovementProcessor().isLastGround() && jumpCheck) {
                    prediction += 0.2F;
                }

                if (user.getLastExplosionTimer().hasNotPassed(20)) {

                }

                if (user.getCombatProcessor().getVelocityTicks() <= 20) {
                    prediction += user.getCombatProcessor().getVelocityH();
                }

                if (user.getCombatProcessor().getVelocityTicks() <= 5
                        && user.getLastFallDamageTimer().hasNotPassed(5)) {
                    prediction += user.getCombatProcessor().getVelocityH();
                }

                if (user.getBlockData().carpetTicks > 0) {
                    prediction += 0.1F;
                }

                double totalPredictedSpeed = deltaXZ - prediction;

                motionXZ = totalPredictedSpeed;

                break;
            }

            case Packet.Client.BLOCK_PLACE: {

                WrappedInBlockPlacePacket wrappedInBlockPlacePacket = new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                if (!wrappedInBlockPlacePacket.getItemStack().getType().isBlock()) {

                    if (wrappedInBlockPlacePacket.getPosition().getX() == -1
                            && wrappedInBlockPlacePacket.getPosition().getY() == -1 && wrappedInBlockPlacePacket.getPosition().getZ() == -1) {

                        if (wrappedInBlockPlacePacket.getItemStack().getType().name().toLowerCase().contains("sword")
                                || wrappedInBlockPlacePacket.getItemStack().getType().name().toLowerCase().contains("bow")) {
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
}
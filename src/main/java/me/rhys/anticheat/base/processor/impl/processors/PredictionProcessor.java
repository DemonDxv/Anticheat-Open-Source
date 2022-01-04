package me.rhys.anticheat.base.processor.impl.processors;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.processor.api.Processor;
import me.rhys.anticheat.base.processor.api.ProcessorInformation;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInFlyingPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutExplosionPacket;
import me.rhys.anticheat.tinyprotocol.packet.types.MathHelper;
import me.rhys.anticheat.util.EventTimer;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.PlayerLocation;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ProcessorInformation(name = "Prediction")
@Getter
@Setter
public class PredictionProcessor extends Processor {

    private double motionXZ;
    private float blockFriction = 0.91F;

    private boolean hit = false, lastUseSword, lastUseItem, useSword, useItem, dropItem;
    private EventTimer lastSlotChange;
    public boolean validMotion = true, fastMath, fMath = false;
    private double lastExpX, lastExpZ, explosionSpeed, lastDeltaXZ, lastDeltaX, lastDeltaZ;
    private WrappedInFlyingPacket isLastPos;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                WrappedInFlyingPacket flyingPacket = new WrappedInFlyingPacket(event.getPacket(), user.getPlayer());

                //We must get the friction of the block the player is currently on.
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
                    //When the player is in the air their friction is always 0.91F
                    blockFriction = 0.91F;
                }
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

                        if (user.isSword(user.getPlayer().getItemInHand()) && user.getPlayer().getItemInHand() != null) {
                            if (!hit) {
                                useSword = useItem = true;
                            }
                        }
                    }
                }

                break;
            }

            case Packet.Client.BLOCK_DIG: {

                WrappedInBlockDigPacket wrappedInBlockDigPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    useSword = useItem = false;
                } else if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.DROP_ITEM) {
                    dropItem = true;
                    useSword = useItem = false;
                }

                break;
            }

            case Packet.Server.HELD_ITEM: {
                useSword = useItem = false;
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
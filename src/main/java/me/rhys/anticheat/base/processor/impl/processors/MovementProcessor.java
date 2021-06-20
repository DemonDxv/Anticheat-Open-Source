package me.rhys.anticheat.base.processor.impl.processors;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.processor.api.Processor;
import me.rhys.anticheat.base.processor.api.ProcessorInformation;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInFlyingPacket;
import me.rhys.anticheat.tinyprotocol.packet.out.WrappedOutPositionPacket;
import me.rhys.anticheat.util.*;
import me.rhys.anticheat.util.block.BlockChecker;
import me.rhys.anticheat.util.box.BoundingBox;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

@ProcessorInformation(name = "Movement")
@Getter @Setter
public class MovementProcessor extends Processor {
    private EventTimer lastGroundTimer, lastBlockPlacePacketTimer;

    private boolean wasFlying, onGround, lastGround, positionYGround, lastPositionYGround, bouncedOnSlime, dead, sprinting,
            lastSprinting;
    private int groundTicks, airTicks, lagBackTicks, serverAirTicks, serverGroundTicks, ignoreServerPositionTicks;
    private double deltaY, lastDeltaY, deltaXZ, lastDeltaXZ, deltaX, deltaZ, serverPositionSpeed, serverPositionDeltaY;
    private PlayerLocation lastSlimeLocation;

    private int stupidTicks, bypassTicks;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {

            case Packet.Server.POSITION: {
                if (this.ignoreServerPositionTicks < 1) {
                    // user.getActionProcessor().add(ActionProcessor.Actions.SERVER_POSITION);
                }

                WrappedOutPositionPacket wrappedOutPositionPacket = new WrappedOutPositionPacket(event.getPacket()
                        , event.getUser().getPlayer());

                user.getActionProcessor().getServerPositionTimer().reset();

                double x = wrappedOutPositionPacket.getX();
                double y = wrappedOutPositionPacket.getY();
                double z = wrappedOutPositionPacket.getZ();


                //calculate speed
                this.serverPositionSpeed = Math.hypot(user.getCurrentLocation().getX() - x,
                        user.getCurrentLocation().getZ() - z);

                //Delta Y
                this.serverPositionDeltaY = Math.abs(user.getCurrentLocation().getY() - y);
                break;
            }

            case Packet.Client.BLOCK_PLACE: {
                this.lastBlockPlacePacketTimer.reset();
                break;
            }

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket actionPacket =
                        new WrappedInEntityActionPacket(event.getPacket(), user.getPlayer());

                if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING) {
                    sprinting = true;
                } else if (actionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING) {
                    sprinting = false;
                }

                break;

            }

            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(event.getPacket(),
                        this.user.getPlayer());

                double x = wrappedInFlyingPacket.getX();
                double y = wrappedInFlyingPacket.getY();
                double z = wrappedInFlyingPacket.getZ();
                float yaw = wrappedInFlyingPacket.getYaw();
                float pitch = wrappedInFlyingPacket.getPitch();
                boolean ground = wrappedInFlyingPacket.isGround();

                lastSprinting = sprinting;

                this.bypassTicks -= this.bypassTicks > 0 ? 1 : 0;

                this.dead = user.getPlayer().isDead();
                this.ignoreServerPositionTicks -= (this.ignoreServerPositionTicks > 0 ? 1 : 0);

                if (wrappedInFlyingPacket.isPos()) {

                    user.setLastLastLocation(user.getLastLocation());
                    user.setLastLocation(user.getCurrentLocation());
                    user.setCurrentLocation(new PlayerLocation(user.getPlayer().getWorld(), x, y, z,
                            yaw, pitch, ground));

                    this.lastDeltaY = this.deltaY;
                    this.deltaY = (user.getCurrentLocation().getY() - user.getLastLocation().getY());

                    this.lastPositionYGround = this.positionYGround;
                    this.positionYGround = y % 0.015625 < 0.009;

                    this.lastGround = this.onGround;
                    this.onGround = ground;

                    if (ground) {
                        this.lastGroundTimer.reset();
                        this.airTicks = 0;
                        if (this.groundTicks < 20) this.groundTicks++;
                    } else {
                        this.groundTicks = 0;
                        if (this.airTicks < 20) this.airTicks++;
                    }
                }

                this.deltaX = Math.abs(Math.abs(user.getCurrentLocation().getX())
                        - Math.abs(user.getLastLocation().getX()));
                this.deltaZ = Math.abs(Math.abs(user.getCurrentLocation().getZ())
                        - Math.abs(user.getLastLocation().getZ()));

                this.lastDeltaXZ = this.deltaXZ;
                this.deltaXZ = Math.hypot(this.deltaX, this.deltaZ);

                this.processBlocks();
                this.user.setTick(this.user.getTick() + 1);

                if (this.lagBackTicks-- > 0 && user.getTick() % 5 == 0) {
                    Location groundLocation = MathUtil.getGroundLocation(user);

                    //Have to teleport off main thread because spigot retarded
                    if (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_9_4)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                user.getPlayer().teleport(groundLocation,
                                        PlayerTeleportEvent.TeleportCause.PLUGIN);
                            }
                        }.runTask(Anticheat.getInstance());
                    } else {
                        user.getPlayer().teleport(groundLocation,
                                PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                }
                break;
            }
        }
    }

    void processBlocks() {
        boolean badVector = Math.abs(user.getCurrentLocation().toVector().length()
                - user.getLastLocation().toVector().length()) >= 1;

        user.setBoundingBox(new BoundingBox((badVector ? user.getCurrentLocation().toVector()
                : user.getLastLocation().toVector()), user.getCurrentLocation().toVector())
                .grow(0.3f, 0, 0.3f).add(0, 0, 0, 0, 1.84f, 0));
        this.cacheInformation(new BlockChecker(this.user));
    }

    void processPrediction(User user) {

    }

    void cacheInformation(BlockChecker blockChecker) {
        blockChecker.processBlocks();

        user.setChunkLoaded(BlockUtil.isChunkLoaded(user.getCurrentLocation().toBukkitLocation(user.getPlayer().getWorld())));

        if ((user.getPlayer().isFlying() || user.getPlayer().getAllowFlight()) && !wasFlying) {
            wasFlying = true;
        } else if (wasFlying && !(user.getPlayer().isFlying() || user.getPlayer().getAllowFlight())) {
            if (user.getBlockData().onGround && user.getBlockData().lastOnGround) {
                wasFlying = false;
            }
        }

        user.getBlockData().lastOnGround = user.getBlockData().onGround;
        user.getBlockData().onGround = blockChecker.isOnGround();
        user.getBlockData().nearLiquid = blockChecker.isNearLiquid();
        user.getBlockData().climbable = blockChecker.isClimbable();
        user.getBlockData().nearIce = blockChecker.isNearIce();
        user.getBlockData().slime = blockChecker.isSlime();
        user.getBlockData().piston = blockChecker.isPiston();
        user.getBlockData().snow = blockChecker.isSnow();
        user.getBlockData().fence = blockChecker.isFence();
        user.getBlockData().bed = blockChecker.isBed();
        user.getBlockData().stair = blockChecker.isStair();
        user.getBlockData().slab = blockChecker.isSlab();
        user.getBlockData().underBlock = blockChecker.isUnderBlock();
        user.getBlockData().web = blockChecker.isWeb();
        user.getBlockData().shulker = blockChecker.isShulker();
        user.getBlockData().insideBlock = blockChecker.isInsideBlock();

        if (user.getBlockData().onGround) {
            if (this.serverGroundTicks < 20) this.serverGroundTicks++;
            this.serverAirTicks = 0;
        } else {
            this.serverGroundTicks = 0;
            if (this.serverAirTicks < 20) this.serverAirTicks++;
        }

        if (this.isOnGround() && user.getBlockData().slime) {
            this.lastSlimeLocation = user.getCurrentLocation().clone();
            this.bouncedOnSlime = true;
        }

        if (this.bouncedOnSlime) {
            if (this.isOnGround() && this.isLastGround() && user.getBlockData().slimeTicks < 1) {
                this.bouncedOnSlime = false;
            }

            if (this.lastSlimeLocation.distanceSquaredXZ(user.getCurrentLocation()) > 70) {
                this.bouncedOnSlime = false;
            }
        }

        this.updateTicks();
    }

    void updateTicks() {

        if (user.getBlockData().shulker) {
            user.getBlockData().shulkerTicks += (user.getBlockData().shulkerTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().shulkerTicks -= (user.getBlockData().shulkerTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().web) {
            user.getBlockData().webTicks += (user.getBlockData().webTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().webTicks -= (user.getBlockData().webTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().underBlock) {
            user.getBlockData().blockAboveTimer.reset();
            user.getBlockData().underBlockTicks += (user.getBlockData().underBlockTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().underBlockTicks -= (user.getBlockData().underBlockTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().stair) {
            user.getBlockData().stairSlabTimer.reset();
            user.getBlockData().stairTicks += (user.getBlockData().stairTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().stairTicks -= (user.getBlockData().stairTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().slab) {
            user.getBlockData().stairSlabTimer.reset();
            user.getBlockData().slabTicks += (user.getBlockData().slabTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().slabTicks -= (user.getBlockData().slabTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().bed) {
            user.getBlockData().bedTicks += (user.getBlockData().bedTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().bedTicks -= (user.getBlockData().bedTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().fence) {
            user.getBlockData().fenceTicks += (user.getBlockData().fenceTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().fenceTicks -= (user.getBlockData().fenceTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().snow) {
            user.getBlockData().snowTicks += (user.getBlockData().snowTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().snowTicks -= (user.getBlockData().snowTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().slime) {
            user.getBlockData().slimeTimer.reset();
            user.getBlockData().slimeTicks += (user.getBlockData().slimeTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().slimeTicks -= (user.getBlockData().slimeTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().nearIce) {
            user.getBlockData().iceTimer.reset();
            user.getBlockData().iceTicks += (user.getBlockData().iceTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().iceTicks -= (user.getBlockData().iceTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().climbable) {
            user.getBlockData().climbableTimer.reset();
            user.getBlockData().climbableTicks += (user.getBlockData().climbableTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().climbableTicks -= (user.getBlockData().climbableTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().nearLiquid) {
            user.getBlockData().liquidTicks += (user.getBlockData().liquidTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().liquidTicks -= (user.getBlockData().liquidTicks > 0 ? 1 : 0);
        }
    }

    @Override
    public void setupTimers(User user) {
        this.lastGroundTimer = new EventTimer(20, user);
        this.lastBlockPlacePacketTimer = new EventTimer(20, user);
        this.lastSlimeLocation = new PlayerLocation(user.getPlayer().getWorld(), 0, 0, 0, 0,
                0, false);
    }
}

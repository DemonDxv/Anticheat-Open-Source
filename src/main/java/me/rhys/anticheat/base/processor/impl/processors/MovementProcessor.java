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
import me.rhys.anticheat.tinyprotocol.api.TinyProtocolHandler;
import me.rhys.anticheat.tinyprotocol.packet.in.*;
import me.rhys.anticheat.tinyprotocol.packet.out.*;
import me.rhys.anticheat.util.*;
import me.rhys.anticheat.util.block.BlockChecker;
import me.rhys.anticheat.util.box.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

@ProcessorInformation(name = "Movement")
@Getter @Setter
public class MovementProcessor extends Processor {
    private EventTimer respawnTimer, lastGroundTimer, lastBlockPlacePacketTimer, lastBlockDigTimer;

    private boolean inInventory, lastLastGround, wasFlying, onGround, lastGround, positionYGround, lastPositionYGround, bouncedOnSlime, dead, sprinting,
            lastSprinting, serverYGround, isDigging;
    private int groundTicks, airTicks, lagBackTicks, serverAirTicks, serverGroundTicks, ignoreServerPositionTicks;
    private double deltaY, lastDeltaY, deltaXZ, lastDeltaXZ, deltaX, deltaZ, serverPositionSpeed, serverPositionDeltaY;
    private PlayerLocation lastSlimeLocation;
    private Location lastGroundLocation;

    private short respawnID = -69;

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

            case Packet.Client.BLOCK_DIG: {
                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(event.getPacket(), user.getPlayer());

                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                    isDigging = true;
                    lastBlockDigTimer.reset();
                } else if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                    isDigging = false;
                } else if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                    isDigging = false;
                }
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

            case Packet.Server.RESPAWN: {

                respawnID--;

                if (respawnID < -9000) {
                    respawnID = 69;
                }

                WrappedOutTransaction transaction = new WrappedOutTransaction(0, respawnID, false);

                TinyProtocolHandler.sendPacket(user.getPlayer(), transaction.getObject());

                break;
            }

            case Packet.Client.TRANSACTION: {
                WrappedInTransactionPacket transactionPacket = new WrappedInTransactionPacket(event.getPacket(), user.getPlayer());

                if (transactionPacket.getAction() == respawnID) {
                    respawnTimer.reset();
                }

                break;
            }

            case Packet.Server.ATTACH: {
                user.getVehicleTimer().reset();
                break;
            }

            case Packet.Client.STEER_VEHICLE: {
                user.getVehicleTimer().reset();

                if (user.getCombatProcessor().getVelocityV() > 0.0) {
                    user.setVehicleTicks(20);
                }
                break;
            }

            case Packet.Client.CLIENT_COMMAND: {
                WrappedInClientCommand clientCommand = new WrappedInClientCommand(event.getPacket(), user.getPlayer());

                if (clientCommand.getCommand() == WrappedInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    inInventory = true;
                }

                break;
            }

            case Packet.Client.CLOSE_WINDOW: {
                inInventory = false;
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

                this.dead = user.getPlayer().isDead();
                this.ignoreServerPositionTicks -= (this.ignoreServerPositionTicks > 0 ? 1 : 0);

                if (y % 0.015625 == 0.0
                        || y % 0.015625 == 0.00625) {
                    serverYGround = true;
                } else {
                    serverYGround = false;
                }

                if (serverYGround && onGround && lastGround
                        && user.getBlockData().onGround && user.getBlockData().lastOnGround) {
                    setLastGroundLocation(user.getPlayer().getLocation());
                }



                this.lastLastGround = this.lastGround;
                this.lastGround = this.onGround;
                this.onGround = ground;


                if (wrappedInFlyingPacket.isPos()) {

                    if (user.getLastLocation() != null) {
                        user.setLastLastLocation(user.getLastLocation());
                    }

                    if (user.getCurrentLocation() != null) {
                        user.setLastLocation(user.getCurrentLocation());
                    }

                    user.setCurrentLocation(new PlayerLocation(user.getPlayer().getWorld(), x, y, z,
                            yaw, pitch, ground, System.currentTimeMillis()));

                    this.lastDeltaY = this.deltaY;
                    this.deltaY = (user.getCurrentLocation().getY() - user.getLastLocation().getY());

                    this.lastPositionYGround = this.positionYGround;
                    this.positionYGround = y % 0.015625 < 0.009;

                   // this.lastGround = this.onGround;
                  //  this.onGround = ground;

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

                this.user.getConnectionProcessor()
                        .setFlyingTick(this.user.getConnectionProcessor().getFlyingTick() + 1);

                this.user.getConnectionProcessor()
                        .setDropTick(0);

                if (this.lagBackTicks-- > 0 && user.getTick() % 3 == 0) {
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
        user.getBlockData().skull = blockChecker.isSkull();
        user.getBlockData().lillyPad = blockChecker.isLillyPad();
        user.getBlockData().onGround = blockChecker.isOnGround();
        user.getBlockData().collidesHorizontal = blockChecker.isCollideHorizontal();
        user.getBlockData().carpet = blockChecker.isCarpet();
        user.getBlockData().cake = blockChecker.isCake();
        user.getBlockData().nearWater = blockChecker.isNearWater();
        user.getBlockData().nearLava = blockChecker.isNearLava();
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

        if (user.getBlockData().carpet) {
            if (user.getBlockData().carpetTicks < 20) this.user.getBlockData().carpetTicks++;
        } else {
            if (user.getBlockData().carpetTicks > 0) {
                this.user.getBlockData().carpetTicks--;
            }
        }

        if (user.getBlockData().skull) {
            if (user.getBlockData().skullTicks < 20) this.user.getBlockData().skullTicks++;
        } else {
            if (user.getBlockData().skullTicks > 0) {
                this.user.getBlockData().skullTicks--;
            }
        }

        if (user.getBlockData().cake) {
            if (user.getBlockData().cakeTicks < 20) this.user.getBlockData().cakeTicks++;
        } else {
            if (user.getBlockData().cakeTicks > 0) {
                this.user.getBlockData().cakeTicks--;
            }
        }

        if (user.getPlayer().isInsideVehicle()) {

            if (user.getVehicleTicks() < 20) {
                user.setVehicleTicks(user.getVehicleTicks() + 1);
            }
        } else {
            if (user.getVehicleTicks() > 0) {
                user.setVehicleTicks(user.getVehicleTicks() - 1);
            }
        }


        if (user.getBlockData().slimeTicks > 0) {
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

        if (user.getBlockData().piston) {
            user.getBlockData().pistonTicks += (user.getBlockData().pistonTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().pistonTicks -= (user.getBlockData().pistonTicks > 0 ? 1 : 0);
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

        if (user.getBlockData().nearWater) {
            user.getBlockData().waterTicks += (user.getBlockData().waterTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().waterTicks -= (user.getBlockData().waterTicks > 0 ? 1 : 0);
        }

        if (user.getBlockData().nearLava) {
            user.getBlockData().lavaTicks += (user.getBlockData().lavaTicks < 20 ? 1 : 0);
        } else {
            user.getBlockData().lavaTicks -= (user.getBlockData().lavaTicks > 0 ? 1 : 0);
        }
    }

    @Override
    public void setupTimers(User user) {
        this.lastGroundTimer = new EventTimer(20, user);
        this.respawnTimer = new EventTimer(20, user);
        this.lastBlockPlacePacketTimer = new EventTimer(20, user);
        this.lastBlockDigTimer = new EventTimer(20, user);
        this.lastSlimeLocation = new PlayerLocation(user.getPlayer().getWorld(), 0, 0, 0, 0,
                0, false, System.currentTimeMillis());
    }
}

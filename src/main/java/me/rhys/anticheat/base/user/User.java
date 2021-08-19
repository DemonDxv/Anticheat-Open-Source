package me.rhys.anticheat.base.user;

import lombok.Getter;
import lombok.Setter;
import me.rhys.anticheat.Anticheat;
import me.rhys.anticheat.base.check.impl.CheckManager;
import me.rhys.anticheat.base.event.EventManager;
import me.rhys.anticheat.base.processor.impl.ProcessorManager;
import me.rhys.anticheat.base.processor.impl.processors.*;
import me.rhys.anticheat.base.user.objects.BlockData;
import me.rhys.anticheat.base.user.objects.LogData;
import me.rhys.anticheat.base.user.objects.LogObject;
import me.rhys.anticheat.tinyprotocol.api.TinyProtocolHandler;
import me.rhys.anticheat.util.*;
import me.rhys.anticheat.util.evicting.EvictingList;
import me.rhys.anticheat.util.evicting.EvictingMap;
import me.rhys.anticheat.util.box.BoundingBox;
import me.rhys.anticheat.util.math.TrigHandler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter @Setter
public class User {
    private final Player player;
    private final UUID uuid;
    private final CheckManager checkManager = new CheckManager();
    private final EventManager eventManager;
    private final ExecutorService executorService;
    private final BlockData blockData = new BlockData();

    private GhostBlockProcessor ghostBlockProcessor;
    private ConnectionProcessor connectionProcessor;
    private PredictionProcessor predictionProcessor;
    private MovementProcessor movementProcessor;
    private ProcessorManager processorManager;
    private ActionProcessor actionProcessor;
    private PotionProcessor potionProcessor;
    private CombatProcessor combatProcessor;
    private ElytraProcessor elytraProcessor;
    private ReachProcessor reachProcessor;

    private TrigHandler trigHandler;

    private Block blockPlaced;

    private final Map<Long, Long> connectionMap = new EvictingMap<>(100);
    private final Map<Long, Long> connectionMap2 = new EvictingMap<>(100);
    private int tick, vehicleTicks;

    public PastLocation previousLocations = new PastLocation();
    private Deque<CustomLocation> customLocations = new LinkedList<>();

    private boolean chunkLoaded = false, alerts = true, banned = false;

    private double mouseDeltaY, mouseDeltaX, lastAimHDeltaPitch, lastAimHDeltaYaw;

    private BoundingBox boundingBox = new BoundingBox(0f, 0f, 0f, 0f, 0f, 0f);
    private PlayerLocation currentLocation = new PlayerLocation(null, 0, 0, 0, 0, 0,
            false, System.currentTimeMillis());
    private PlayerLocation lastLocation = currentLocation, lastLastLocation = lastLocation;

    private EventTimer lastFlaggedFlightCTimer = new EventTimer(20, this), lastFlightToggleTimer = new EventTimer(20, this),
            lastSuffocationTimer = new EventTimer(20, this),
            lastBlockBreakTimer = new EventTimer(20, this),
            vehicleTimer = new EventTimer(40, this),
            lastExplosionTimer = new EventTimer(40, this),
            lastShotByArrowTimer = new EventTimer(20, this),
            lastAttackByEntityTimer = new EventTimer(20, this),
            lastFireTickTimer = new EventTimer(20, this),
            lastBlockPlaceCancelTimer = new EventTimer(20, this),
            lastBlockPlaceTimer = new EventTimer(20, this),
            lastFallDamageTimer = new EventTimer(20, this),
            lastTeleportTimer = new EventTimer(20, this),
            lastUnknownTeleportTimer = new EventTimer(20, this);

    private LogObject logObject;

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.checkManager.setupChecks(this);
        this.eventManager = new EventManager(this);
        this.processorManager = new ProcessorManager(this);
        this.processorManager.setup();
        this.setupProcessors();
        this.blockData.setupTimers(this);

        if (Anticheat.getInstance().getLogData() != null && Anticheat.getInstance().getLogObjectList() != null) {
            Anticheat.getInstance().getLogData().addUser(new LogObject(this.uuid.toString()));

            this.logObject = Anticheat.getInstance().getLogData().getUser(this.uuid.toString());
            this.logObject.name = player.getName();
        }


        eventManager.processTime();

        if (customLocations.size() >= 8) {
            customLocations.removeLast();
        }

        trigHandler = new TrigHandler(this);
    }

    private void setupProcessors() {
        this.connectionProcessor = (ConnectionProcessor) this.processorManager.forClass(ConnectionProcessor.class);
        this.ghostBlockProcessor = (GhostBlockProcessor) this.processorManager.forClass(GhostBlockProcessor.class);
        this.movementProcessor = (MovementProcessor) this.processorManager.forClass(MovementProcessor.class);
        this.predictionProcessor = (PredictionProcessor) this.processorManager.forClass(PredictionProcessor.class);
        this.actionProcessor = (ActionProcessor) this.processorManager.forClass(ActionProcessor.class);
        this.potionProcessor = (PotionProcessor) this.processorManager.forClass(PotionProcessor.class);
        this.combatProcessor = (CombatProcessor) this.processorManager.forClass(CombatProcessor.class);
        this.elytraProcessor = (ElytraProcessor) this.processorManager.forClass(ElytraProcessor.class);
        this.reachProcessor = (ReachProcessor) this.processorManager.forClass(ReachProcessor.class);
    }

    public void sendPacket(Object packet) {
        TinyProtocolHandler.sendPacket(this.player, packet);
    }

    public boolean shouldCancel() {
        return !this.chunkLoaded || TPSUtil.getTPS() <= 19.0 || this.lastFlightToggleTimer.hasNotPassed(20
                + connectionProcessor.getClientTick()) || this.player.getAllowFlight()
                || this.player.isFlying() || this.player.getGameMode() == GameMode.CREATIVE
                || this.player.getGameMode() == GameMode.SPECTATOR;
    }

    public boolean isSword(ItemStack itemStack) {
        return itemStack.getType() == Material.WOOD_SWORD
                || itemStack.getType() == Material.STONE_SWORD
                || itemStack.getType() == Material.GOLD_SWORD
                || itemStack.getType() == Material.IRON_SWORD
                || itemStack.getType() == Material.DIAMOND_SWORD;
    }
}

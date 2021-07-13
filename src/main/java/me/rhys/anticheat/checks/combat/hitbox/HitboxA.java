package me.rhys.anticheat.checks.combat.hitbox;

import me.rhys.anticheat.base.check.api.Check;
import me.rhys.anticheat.base.check.api.CheckInformation;
import me.rhys.anticheat.base.event.PacketEvent;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.tinyprotocol.api.Packet;
import me.rhys.anticheat.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import me.rhys.anticheat.util.MathUtil;
import me.rhys.anticheat.util.PastLocation;
import me.rhys.anticheat.util.PlayerLocation;
import me.rhys.anticheat.util.Verbose;
import me.rhys.anticheat.util.block.RayTrace;
import me.rhys.anticheat.util.box.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Hitbox", lagBack = false, punishmentVL = 25, canPunish = false)
public class HitboxA extends Check {

    private PastLocation hitBoxPastLocations = new PastLocation();
    private Verbose threshold = new Verbose();

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.getCombatProcessor().getLastAttackedEntity() != null) {
                    hitBoxPastLocations.addLocation(user.getCombatProcessor().getLastAttackedEntity().getLocation());
                }

                break;
            }

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket attack = new WrappedInUseEntityPacket(event.getPacket(), user.getPlayer());

                if (attack.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {


                    List<BoundingBox> boundingBoxList = new ArrayList<>();
                    List<PlayerLocation> pastLocation = hitBoxPastLocations
                            .getEstimatedLocation(user.getConnectionProcessor().getTransPing(),
                                    (user.getConnectionProcessor().getDropTransTime() + 200));


                    if (pastLocation.size() > 0) {


                        boundingBoxList.clear();
                        pastLocation.clear();
                    }
                }
            }
        }
    }
}

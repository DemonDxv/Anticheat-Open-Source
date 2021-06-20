package me.rhys.bedrock.base.processor.impl.processors;

import lombok.Getter;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.processor.api.Processor;
import me.rhys.bedrock.base.processor.api.ProcessorInformation;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@ProcessorInformation(name = "Potion")
@Getter
public class PotionProcessor extends Processor {

    private boolean hasSpeed, hasJump;
    private double speedAmplifier, jumpAmplifier;
    private int speedTicks, jumpTicks;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {
                User user = event.getUser();

                if ((this.hasSpeed = user.getPlayer().hasPotionEffect(PotionEffectType.SPEED))) {
                    this.speedAmplifier = this.getPotionEffectLevel(user, PotionEffectType.SPEED);
                    this.speedTicks += (this.speedTicks < 20 ? 1 : 0);
                } else {
                    this.speedTicks -= (this.speedTicks > 0 ? 1 : 0);
                }

                if ((this.hasJump = user.getPlayer().hasPotionEffect(PotionEffectType.JUMP))) {
                    this.jumpAmplifier = this.getPotionEffectLevel(user, PotionEffectType.JUMP);
                    this.jumpTicks += (this.jumpTicks < 20 ? 1 : 0);
                } else {
                    this.jumpTicks -= (this.jumpTicks > 0 ? 1 : 0);
                }
            }
        }
    }

    int getPotionEffectLevel(User user, PotionEffectType potionEffectType) {
        PotionEffect potionEffect = user.getPlayer().getActivePotionEffects()
                .stream().filter(effect -> effect.getType().equals(potionEffectType)).findAny().orElse(null);
        return (potionEffect != null ? potionEffect.getAmplifier() + 1 : 0);
    }
}

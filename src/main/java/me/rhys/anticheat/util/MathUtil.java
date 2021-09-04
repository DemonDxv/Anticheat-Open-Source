package me.rhys.anticheat.util;

import com.google.common.util.concurrent.AtomicDouble;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.util.box.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MathUtil {


    public static Map<EntityType, Vector> entityDimensions;


    public MathUtil() {
        entityDimensions = new HashMap<>();
        entityDimensions.put(EntityType.WOLF, new Vector(0.31, 0.8, 0.31));
        entityDimensions.put(EntityType.SHEEP, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.COW, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.PIG, new Vector(0.45, 0.9, 0.45));
        entityDimensions.put(EntityType.MUSHROOM_COW, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.WITCH, new Vector(0.31, 1.95, 0.31));
        entityDimensions.put(EntityType.BLAZE, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.PLAYER, new Vector(0.3, 1.8, 0.3));
        entityDimensions.put(EntityType.VILLAGER, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.CREEPER, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.GIANT, new Vector(1.8, 10.8, 1.8));
        entityDimensions.put(EntityType.SKELETON, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.ZOMBIE, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.SNOWMAN, new Vector(0.35, 1.9, 0.35));
        entityDimensions.put(EntityType.HORSE, new Vector(0.7, 1.6, 0.7));
        entityDimensions.put(EntityType.ENDER_DRAGON, new Vector(1.5, 1.5, 1.5));

        entityDimensions.put(EntityType.ENDERMAN, new Vector(0.31, 2.9, 0.31));
        entityDimensions.put(EntityType.CHICKEN, new Vector(0.2, 0.7, 0.2));
        entityDimensions.put(EntityType.OCELOT, new Vector(0.31, 0.7, 0.31));
        entityDimensions.put(EntityType.SPIDER, new Vector(0.7, 0.9, 0.7));
        entityDimensions.put(EntityType.WITHER, new Vector(0.45, 3.5, 0.45));
        entityDimensions.put(EntityType.IRON_GOLEM, new Vector(0.7, 2.9, 0.7));
        entityDimensions.put(EntityType.GHAST, new Vector(2, 4, 2));
    }

    public static Vector getDirection(PlayerLocation loc) {
        Vector vector = new Vector();
        double rotX = loc.getYaw();
        double rotY = loc.getPitch();
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }

    public static Vector getDirection(CustomLocation loc) {
        Vector vector = new Vector();
        double rotX = loc.getYaw();
        double rotY = loc.getPitch();
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }

    public static double yawCheck( double yaw, double lastYaw) {
        double perc_value = lastYaw;
        double numb_value = yaw;

        double rslt_value;
        rslt_value = perc_value * numb_value / 100.0;
        rslt_value = 1000.0 * rslt_value / 1000.0;
        return Double.parseDouble(String.valueOf(rslt_value));
    }

    /**
     * Gets the angle between {@param from} and {@param to} and subtracts with the direction of {@param to}
     * @param from The from location
     * @param to The to location
     * @return The move angle
     */
    public static float getMoveAngle(PlayerLocation from, PlayerLocation to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        float moveAngle = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90F); // have to subtract by 90 because minecraft does it

        return Math.abs(wrapAngleTo180_float(moveAngle - to.getYaw()));
    }

    public static Location getGroundLocation(User user) {
        World world = user.getPlayer().getWorld();

        Location location = user.getCurrentLocation().toBukkitLocation(world);
        int i = 0;
        while (!BlockUtil.getBlock(location).getRelative(BlockFace.DOWN).getType().isSolid()
                && location.getY() != 0) {
            if (i++ > 20) {
                break;
            }
            location.add(0, -1, 0);
        }


        if (location.getY() == 0){
            return user.getCurrentLocation().toBukkitLocation(world);
        }

        location.add(0, .05, 0);

        location.setYaw(user.getCurrentLocation().getYaw());
        location.setPitch(user.getCurrentLocation().getPitch());

        return location;
    }

    public static double getCPS(Collection<? extends Number> values) {
        return 20 / getAverage(values);
    }


    public static float wrapAngleTo180_float(float value) {
        value %= 360F;

        if (value >= 180.0F)
            value -= 360.0F;

        if (value < -180.0F)
            value += 360.0F;

        return value;
    }

    public static float getBaseSpeed(Player player) {
        return 0.26f + (getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public static float getBaseSpeed_2(Player player) {
        return 0.23f + (getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public static float getWalkSpeed(Player player) {
        return (getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.0260001f) + player.getWalkSpeed() * 0.65F;
    }
    public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (pe.getType().getName().equalsIgnoreCase(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

   /* public static double gcd(double a, double b) {
        if (a < b) {
            return gcd(b, a);
        } else if (Math.abs(b) < 0.001) {
            return a;
        } else {
            return gcd(b, a - Math.floor(a / b) * b);
        }
    }*/

    public static double getAverage(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0D);
    }

    public static double getStandardDeviation(Collection<? extends Number> values) {
        double average = getAverage(values);

        AtomicDouble variance = new AtomicDouble(0D);

        values.forEach(delay -> variance.getAndAdd(Math.pow(delay.doubleValue() - average, 2D)));

        return Math.sqrt(variance.get() / values.size());
    }


    public static long convertToNanos(long number) {
        return TimeUnit.MILLISECONDS.toNanos(number);
    }

    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < var2 ? var2 - 1 : var2;
    }

    public static double movingFlyingV3(User user) {
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

        float strafe = 0.98F, forward = 0.98F;
        float f = strafe * strafe + forward * forward;

        float friction;

        float var3 = (0.6F * 0.91F);
        float getAIMoveSpeed = 0.13000001F;


        if (user.getPotionProcessor().getSpeedTicks() > 0) {
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
        }

        float var4 = 0.16277136F / (var3 * var3 * var3);

        if (from.isClientGround()) {
            friction = getAIMoveSpeed * var4;
        } else {
            friction = 0.026F;
        }

        if (f >= 1.0E-4F) {
            f = (float) Math.sqrt(f);
            if (f < 1.0F) {
                f = 1.0F;
            }
            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = (float) Math.sin(to.getYaw() * (float) Math.PI / 180.0F);
            float f2 = (float) Math.cos(to.getYaw() * (float) Math.PI / 180.0F);
            float motionXAdd = (strafe * f2 - forward * f1);
            float motionZAdd = (forward * f2 + strafe * f1);
            return Math.hypot(motionXAdd, motionZAdd);
        }

        return 0;
    }

    public static BoundingBox getHitbox(LivingEntity entity, PlayerLocation l, User user) {
        float d = (float) user.getMovementProcessor().getDeltaXZ();
        Vector dimensions = MathUtil.entityDimensions.getOrDefault(entity.getType(), new Vector(0.4, 2, 0.4));
        return new BoundingBox(0, 0, 0, 0, 0, 0).add((float) l.getX(), (float) l.getY(), (float) l.getZ()).grow((float) dimensions.getX(), (float) dimensions.getY(), (float) dimensions.getZ()).grow(.3f, 0.1f, .3f)
                .grow((entity.getVelocity().getY() > 0 ? 0.15f : 0) + d / 1.25f, 0, (entity.getVelocity().getY() > 0 ? 0.15f : 0) + d / 1.25f);
    }

    public static BoundingBox getHitboxV2(LivingEntity entity, PlayerLocation l, User user) {
        float d = (float) user.getMovementProcessor().getDeltaXZ();
        Vector dimensions = MathUtil.entityDimensions.getOrDefault(entity.getType(), new Vector(0.42, 2, 0.42));
        return new BoundingBox(0, 0, 0, 0, 0, 0).add((float) l.getX(), (float) l.getY(), (float) l.getZ()).grow((float) dimensions.getX(), (float) dimensions.getY(), (float) dimensions.getZ()).grow(0.1f, 0.1f, 0.1f)
                .grow((entity.getVelocity().getY() > 0 ? 0.15f : 0) + d / 1.25f, 0, (entity.getVelocity().getY() > 0 ? 0.15f : 0) + d / 1.25f);
    }


    public static long gcd(long current, long last) {
        if (last <= 16384) return current;
        return gcd(last, current % last);
    }

}

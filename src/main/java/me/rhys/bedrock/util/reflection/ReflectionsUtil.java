package me.rhys.bedrock.util.reflection;

import me.rhys.bedrock.tinyprotocol.api.ProtocolVersion;
import me.rhys.bedrock.tinyprotocol.reflection.FieldAccessor;
import me.rhys.bedrock.tinyprotocol.reflection.Reflection;
import me.rhys.bedrock.util.BlockUtil;
import me.rhys.bedrock.util.box.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class ReflectionsUtil {

    public static Class<?> blockPosition = null;
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    public static Class<?> EntityPlayer = getNMSClass("EntityPlayer");
    public static Class<?> Entity = getNMSClass("Entity");
    public static Class<?> CraftPlayer = getCBClass("entity.CraftPlayer");
    public static Class<?> CraftEntity = getCBClass("entity.CraftEntity");
    public static Class<?> CraftWorld = getCBClass("CraftWorld");
    private static final Class<?> craftServer = getCBClass("CraftServer");
    public static Class<?> World = getNMSClass("World");
    public static Class<?> worldServer = getNMSClass("WorldServer");
    public static Class<?> playerConnection = getNMSClass("PlayerConnection");
    public static Class<?> networkManager = getNMSClass("NetworkManager");
    public static Class<?> minecraftServer = getNMSClass("MinecraftServer");
    public static Class<?> packet = getNMSClass("Packet");
    public static Class<?> iBlockData = null;
    public static Class<?> iBlockAccess = null;
    private static final Class<?> vanillaBlock = getNMSClass("Block");
    private static final Method getCubes = getMethod(World, "a", getNMSClass("AxisAlignedBB"));
    private static final Method getCubes1_12 = getMethod(World, "getCubes", getNMSClass("Entity"), getNMSClass("AxisAlignedBB"));

    private static boolean bb;

    public static boolean doesClassExist(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static Object getEntityPlayer(Player player) {
        return getMethodValue(getMethod(CraftPlayer, "getHandle"), player);
    }

    public static Object getEntity(org.bukkit.entity.Entity entity) {
        return getMethodValue(getMethod(CraftEntity, "getHandle"), entity);
    }

    public static Object getExpandedBoundingBox(Object box, double x, double y, double z) {
        return getMethodValue(getMethod(box.getClass(), "grow", double.class, double.class, double.class), box, x, y, z);
    }

    private static final FieldAccessor<Integer> accessor = Reflection.getField("{nms}.Entity", "entityCount", int.class);

    public static int incrementEntityCount() {
        int count = accessor.get(null);
        accessor.set(null, count + 1);
        return count;
    }

    public static Object modifyBoundingBox(Object box, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double newminX = (double) getFieldValue(getFieldByName(box.getClass(), "a"), box) - minX;
        double newminY = (double) getFieldValue(getFieldByName(box.getClass(), "b"), box) - minY;
        double newminZ = (double) getFieldValue(getFieldByName(box.getClass(), "c"), box) - minZ;
        double newmaxX = (double) getFieldValue(getFieldByName(box.getClass(), "d"), box) + maxX;
        double newmaxY = (double) getFieldValue(getFieldByName(box.getClass(), "e"), box) + maxY;
        double newmaxZ = (double) getFieldValue(getFieldByName(box.getClass(), "f"), box) + maxZ;

        return newInstance(getNMSClass("AxisAlignedBB"), newminX, newminY, newminZ, newmaxX, newmaxY, newmaxZ);
    }

    private static Vector getBoxMin(Object box) {
        if (hasField(box.getClass(), "a")) {
            double x = (double) getFieldValue(getFieldByName(box.getClass(), "a"), box);
            double y = (double) getFieldValue(getFieldByName(box.getClass(), "b"), box);
            double z = (double) getFieldValue(getFieldByName(box.getClass(), "c"), box);
            return new Vector(x, y, z);
        } else {
            double x = (double) getFieldValue(getFieldByName(box.getClass(), "minX"), box);
            double y = (double) getFieldValue(getFieldByName(box.getClass(), "minY"), box);
            double z = (double) getFieldValue(getFieldByName(box.getClass(), "minZ"), box);
            return new Vector(x, y, z);
        }
    }

    public static Object getMinecraftServer() {
        return getMethodValue(getMethod(craftServer, "getServer"), Bukkit.getServer());
    }

    private static Vector getBoxMax(Object box) {
        if (hasField(box.getClass(), "d")) {
            double x = (double) getFieldValue(getFieldByName(box.getClass(), "d"), box);
            double y = (double) getFieldValue(getFieldByName(box.getClass(), "e"), box);
            double z = (double) getFieldValue(getFieldByName(box.getClass(), "f"), box);
            return new Vector(x, y, z);
        } else {
            double x = (double) getFieldValue(getFieldByName(box.getClass(), "maxX"), box);
            double y = (double) getFieldValue(getFieldByName(box.getClass(), "maxY"), box);
            double z = (double) getFieldValue(getFieldByName(box.getClass(), "maxZ"), box);
            return new Vector(x, y, z);
        }
    }

    public static BoundingBox toBoundingBox(Object aaBB) {
        Vector min = getBoxMin(aaBB);
        Vector max = getBoxMax(aaBB);

        return new BoundingBox((float) min.getX(), (float) min.getY(), (float) min.getZ(), (float) max.getX(), (float) max.getY(), (float) max.getZ());
    }

    public static float getBlockDurability(Block block) {
        Object vanillaBlock = getVanillaBlock(block);
        return (float) getFieldValue(getFieldByName(getNMSClass("Block"), "strength"), vanillaBlock);
    }

    public static boolean canDestroyBlock(Player player, Block block) {
        Object inventory = getVanillaInventory(player);
        return (boolean) getMethodValue(getMethod(getNMSClass("PlayerInventory"), "b", getNMSClass("Block")), inventory, ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9) ? getBlockData(block) : getVanillaBlock(block));
    }

    public static Object getVanillaInventory(Player player) {
        return getMethodValue(getMethod(getCBClass("inventory.CraftInventoryPlayer"), "getInventory"), player.getInventory());
    }

    public static float getFriction(Block block) {
        Object blockNMS = getVanillaBlock(block);

        return (float) getFieldValue(getFieldByName(vanillaBlock, "frictionFactor"), blockNMS);
    }

    public static Object getBlockPosition(Location location) {
        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            return newInstance(blockPosition, location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }
        return null;
    }

    public static List<org.bukkit.entity.Entity> getEntitiesInWorld(org.bukkit.World world) {
        Object worldHandle = getWorldHandle(world);
        List<org.bukkit.entity.Entity> toReturn = new ArrayList<>();
        List<Object> entityList = new ArrayList<>((List<Object>) getFieldValue(getFieldByName(getNMSClass("World"), "entityList"), worldHandle));

        Class<?> entity = getNMSClass("Entity");
        entityList.forEach(object -> {
            Object bEntity = getMethodValue(getMethod(entity, "getBukkitEntity"), object);
            if(bEntity != null) {
                toReturn.add((org.bukkit.entity.Entity) bEntity);
            }
        });
        return toReturn;
    }

    public static BoundingBox getBlockBoundingBox(Block block) {
        try {
            if (!isBukkitVerison("1_7")) {
                Object bPos = blockPosition.getConstructor(int.class, int.class, int.class).newInstance(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                Object world = getWorldHandle(block.getWorld());
                Object data = getMethodValue(getMethod(world.getClass(), "getType", blockPosition), world, bPos);
                Object blockNMS = getMethodValue(getMethod(getNMSClass("IBlockData"), "getBlock"), data);

                if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
                    if (!isNewVersion()) {

                        if (getMethodValueNoST(getMethodNoST(blockNMS.getClass(), "a", World, blockPosition, iBlockData), blockNMS, world, bPos, data) != null
                                && !BlockUtil.isSlab(block)) {
                            BoundingBox box = toBoundingBox(getMethodValue(getMethod(blockNMS.getClass(), "a", World, blockPosition, iBlockData), blockNMS, world, bPos, data));

                            if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
                                if (block.getType().toString().contains("STEP") && !block.getType().toString().contains("WOOD")) {
                                    Step slab = (Step) block.getType().getNewData(block.getData());

                                    box.minY = block.getY();
                                    box.maxY = block.getY();
                                    if (slab.isInverted()) {
                                        box = box.add(0, 0.5f, 0, 0, 1f, 0);
                                    } else {
                                        box = box.add(0, 0f, 0, 0, 0.5f, 0);
                                    }
                                } else if (block.getType().toString().contains("STEP")) {
                                    WoodenStep slab = (WoodenStep) block.getType().getNewData(block.getData());

                                    box.minY = block.getY();
                                    box.maxY = block.getY();
                                    if (slab.isInverted()) {
                                        box = box.add(0, 0.5f, 0, 0, 1f, 0);
                                    } else {
                                        box = box.add(0, 0f, 0, 0, 0.5f, 0);
                                    }
                                }
                            }
                            return box;
                        } else if (getMethodValueNoST(getMethodNoST(vanillaBlock, "a", World, blockPosition, iBlockData), blockNMS, world, bPos, data) != null) {
                            BoundingBox box = toBoundingBox(getMethodValue(getMethod(vanillaBlock, "a", World, blockPosition, iBlockData), blockNMS, world, bPos, data));

                            if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
                                if (block.getType().toString().contains("STEP") && !block.getType().toString().contains("WOOD")) {
                                    Step slab = (Step) block.getType().getNewData(block.getData());

                                    box.minY = block.getY();
                                    box.maxY = block.getY();
                                    if (slab.isInverted()) {
                                        box = box.add(0, 0.5f, 0, 0, 1f, 0);
                                    } else {
                                        box = box.add(0, 0f, 0, 0, 0.5f, 0);
                                    }
                                } else if (block.getType().toString().contains("STEP")) {
                                    WoodenStep slab = (WoodenStep) block.getType().getNewData(block.getData());

                                    box.minY = block.getY();
                                    box.maxY = block.getY();
                                    if (slab.isInverted()) {
                                        box = box.add(0, 0.5f, 0, 0, 1f, 0);
                                    } else {
                                        box = box.add(0, 0f, 0, 0, 0.5f, 0);
                                    }
                                }
                            }
                            return box;
                        } else {
                            return new BoundingBox(block.getX(), block.getY(), block.getZ(), block.getX(), block.getY(), block.getZ());
                        }
                    } else {
                        if (getMethodValueNoST(getMethodNoST(blockNMS.getClass(), "a", iBlockData, getNMSClass("IBlockAccess"), blockPosition), blockNMS, data, world, bPos) != null) {
                            return toBoundingBox(getMethodValue(getMethod(blockNMS.getClass(), "a", iBlockData, getNMSClass("IBlockAccess"), blockPosition), blockNMS, data, world, bPos)).add(block.getX(), block.getY(), block.getZ(), block.getX(), block.getY(), block.getZ());
                        } else if (getMethodValueNoST(getMethodNoST(vanillaBlock, "a", iBlockData, getNMSClass("IBlockAccess"), blockPosition), blockNMS, data, world, bPos) != null) {
                            return toBoundingBox(getMethodValue(getMethod(vanillaBlock, "a", iBlockData, getNMSClass("IBlockAccess"), blockPosition), blockNMS, data, world, bPos)).add(block.getX(), block.getY(), block.getZ(), block.getX(), block.getY(), block.getZ());
                        } else {
                            return new BoundingBox(block.getX(), block.getY(), block.getZ(), block.getX(), block.getY(), block.getZ());
                        }
                    }
                } else {
                    Object voxelShape = getMethodValue(getMethod(vanillaBlock, "a", iBlockData, getNMSClass("IBlockAccess"), blockPosition), blockNMS, data, world, bPos);
                    Object axisAlignedBB = getMethodValue(getMethod(getNMSClass("VoxelShape"), "a"), voxelShape);


                    return toBoundingBox(axisAlignedBB);

                }
            } else {
                Object blockNMS = getVanillaBlock(block);
                Object world = getWorldHandle(block.getWorld());
                if (getMethodValueNoST(getMethodNoST(vanillaBlock, "a", getNMSClass("World"), int.class, int.class, int.class), blockNMS, world, block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()) != null) {
                    return toBoundingBox(getMethodValue(getMethod(vanillaBlock, "a", getNMSClass("World"), int.class, int.class, int.class), blockNMS, world, block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()));
                } else {
                    //Bukkit.broadcastMessage(block.getType().name());
                    return new BoundingBox(block.getX(), block.getY(), block.getZ(), block.getX(), block.getY(), block.getZ());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error occured with block: " + block.getType().toString());
            e.printStackTrace();
        }
        return null;
    }


    public static double getTPS(Server server) {
        Object handle = getMethodValue(getMethod(getCBClass("CraftServer"), "getHandle"), server);

        return (int) getFieldValue(getFieldByName(getNMSClass("MinecraftServer"), "TPS"), handle);
    }

    public static float getBlockHardness(final Material material) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (!material.isBlock())
            return 0;

        final int blockId = material.getId();
        final Object nmsBlock = getNMSClass("Block").getMethod("getById", Integer.TYPE).invoke(null, blockId);

        try {
            final Field field = nmsBlock.getClass().getDeclaredField("strength");
            field.setAccessible(true);
            return (float) field.get(nmsBlock);
        } catch (final NoSuchFieldException e) {
            return 0.0F;
        }
    }

    public static float getDestroySpeed(Block block, Player player) {

        if (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)) {
            Object item = getVanillaItem(player.getItemInHand());
            return (float) getMethodValue(getMethod(getNMSClass("Item"), "getDestroySpeed", getNMSClass("ItemStack"), getNMSClass("IBlockData")), item, getVanillaItemStack(player.getItemInHand()), getBlockData(block));
        } else {
            Object item = getVanillaItem(player.getInventory().getItemInHand());
            return (float) getMethodValue(getMethod(getNMSClass("Item"), "getDestroySpeed", getNMSClass("ItemStack"), getNMSClass("Block")), item, getVanillaItemStack(player.getInventory().getItemInHand()), getVanillaBlock(block));
        }
    }

    public static Object getVanillaItem(ItemStack itemStack) {
        return getMethodValue(getMethod(getNMSClass("ItemStack"), "getItem"), getVanillaItemStack(itemStack));
    }

    public static Object getVanillaItemStack(ItemStack itemStack) {
        return getMethodValue(getMethod(getCBClass("inventory.CraftItemStack"), "asNMSCopy", getClass("org.bukkit.inventory.ItemStack")), itemStack, itemStack);
    }

    public static Object getVanillaBlock(Block block) {

        if (!isBukkitVerison("1_7")) {
            Object getType = getBlockData(block);
            return getMethodValue(getMethod(iBlockData, "getBlock"), getType);
        } else {
            Object world = getWorldHandle(block.getWorld());
            return getMethodValue(getMethod(worldServer, "getType", int.class, int.class, int.class), world, block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
        }
    }

    public static Object getBlockData(Block block) {
        try {
            if (!isBukkitVerison("1_7")) {
                Object bPos = blockPosition.getConstructor(int.class, int.class, int.class).newInstance(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                Object world = getWorldHandle(block.getWorld());
                return getMethodValue(getMethod(worldServer, "getType", blockPosition), world, bPos);
            } else {
                Object world = getWorldHandle(block.getWorld());
                return getMethodValue(getMethod(worldServer, "getType", int.class, int.class, int.class), world, block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getBelowBox(Player player, double below) {
        Object box = getBoundingBox(player);
        double minX = (double) getFieldValue(getFieldByName(box.getClass(), "a"), box);
        double minY = (double) getFieldValue(getFieldByName(box.getClass(), "b"), box) - below;
        double minZ = (double) getFieldValue(getFieldByName(box.getClass(), "c"), box);
        double maxX = (double) getFieldValue(getFieldByName(box.getClass(), "d"), box);
        double maxY = (double) getFieldValue(getFieldByName(box.getClass(), "e"), box);
        double maxZ = (double) getFieldValue(getFieldByName(box.getClass(), "f"), box);

        try {
            return getNMSClass("AxisAlignedBB").getConstructor(double.class, double.class, double.class, double.class, double.class, double.class).newInstance(minX, minY, minZ, maxX, maxY, maxZ);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getBoundingBox(Player player) {
        return getBoundingBox((org.bukkit.entity.Entity) player);
    }

    public static Object getBoundingBox(org.bukkit.entity.Entity entity) {
        return isBukkitVerison("1_7") ? getFieldValue(getFieldByName(Entity, "boundingBox"), getEntity(entity)) : getMethodValue(getMethod(Entity, "getBoundingBox"), getEntity(entity));
    }

    public static boolean isBukkitVerison(String version) {
        return ProtocolVersion.getGameVersion().getServerVersion().contains(version);
    }

    public static boolean isNewVersion() {
        return isBukkitVerison("1_9") || isBukkitVerison("1_1");
    }

    public static Class<?> getCBClass(String string) {
        return getClass("org.bukkit.craftbukkit." + version + "." + string);
    }


    public static Object newAxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        try {
            return isBukkitVerison("1_7") ? getMethodValue(getMethod(getNMSClass("AxisAlignedBB"), "a", double.class, double.class, double.class, double.class, double.class, double.class), null, minX, minY, minZ, maxX, maxY, maxZ) : getNMSClass("AxisAlignedBB").getConstructor(double.class, double.class, double.class, double.class, double.class, double.class).newInstance(minX, minY, minZ, maxX, maxY, maxZ);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object newVoxelShape(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        try {
            return getNMSClass("AxisAlignedBB").getConstructor(double.class, double.class, double.class, double.class, double.class, double.class).newInstance(minX, minY, minZ, maxX, maxY, maxZ);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double getMotionY(Player player) {
        double motionY = 0;
        try {
            motionY = (double) ReflectionsUtil.getEntityPlayer(player).getClass().getField("motY").get(ReflectionsUtil.getEntityPlayer(player));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return motionY;
    }

    public static Object newAxisAlignedBB(Location from, Location to) {
        double minX = Math.min(from.getX(), to.getX());
        double minY = Math.min(from.getY(), to.getY());
        double minZ = Math.min(from.getZ(), to.getZ());
        double maxX = Math.max(from.getX(), to.getX());
        double maxY = Math.max(from.getY(), to.getY());
        double maxZ = Math.max(from.getZ(), to.getZ());

        try {
            return getNMSClass("AxisAlignedBB").getConstructor(double.class, double.class, double.class, double.class, double.class, double.class).newInstance(minX, minY, minZ, maxX, maxY, maxZ);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getClass(String string) {
        try {
            return Class.forName(string);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Enum<?> getEnum(Class<?> clazz, String enumName) {
        return Enum.valueOf((Class<Enum>) clazz, enumName);
    }

    public static Field getFieldByName(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName) != null ? clazz.getDeclaredField(fieldName) : clazz.getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return field;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object setFieldValue(Object object, Field field, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return field.getDeclaringClass();
    }


    public static boolean inBlock(Player player, Object axisAlignedBB) {
        return getCollidingBlocks(player, axisAlignedBB).size() > 0;
    }

    /**
     * Method removed in 1.12 and later versions in NMS
     **/
    public static Collection<?> getCollidingBlocks(Player player, Object axisAlignedBB) {
        Object world = getWorldHandle(player.getWorld());
        return (Collection<?>) (isNewVersion()
                ? getMethodValue(getCubes1_12, world, null, axisAlignedBB)
                : getMethodValue(getCubes, world, axisAlignedBB));
    }

    public static Object getWorldHandle(org.bukkit.World world) {
        return getMethodValue(getMethod(CraftWorld, "getHandle"), world);
    }

    public static Object getServerHandle(Server server) {
        return getMethodValue(getMethod(minecraftServer, "getServer"), server);
    }


    public static Field getFirstFieldByType(Class<?> clazz, Class<?> type) {
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType().equals(type)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        try {
            Method method = clazz.getMethod(methodName, args);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method getMethodNoST(Class<?> clazz, String methodName, Class<?>... args) {
        try {
            Method method = clazz.getMethod(methodName, args);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean hasMethod(Class clazz, Method method) {
        return Arrays.stream(clazz.getMethods()).anyMatch(methodLoop -> methodLoop.getName().equals(method.getName()));
    }

    public static boolean hasMethod(Class clazz, String methodName) {
        return Arrays.stream(clazz.getMethods()).anyMatch(methodLoop -> methodLoop.getName().equals(methodName));
    }

    public static Object getMethodValue(Method method, Object object, Object... args) {
        try {
            return method.invoke(object, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasField(Class<?> object, String fieldName) {
        return Arrays.stream(object.getFields()).anyMatch(field -> field.getName().equalsIgnoreCase(fieldName));
    }

    public static Object getMethodValueNoST(Method method, Object object, Object... args) {
        try {
            return method.invoke(object, args);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getFieldValue(Field field, Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFieldValueNoST(Field field, Object object) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    public static Field getFieldByNameNoST(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName) != null ? clazz.getDeclaredField(fieldName) : clazz.getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return field;
        } catch (Exception e) {
            return null;
        }
    }

    public static Object newInstance(Class<?> objectClass, Object... args) {
        try {
            return objectClass.getConstructor(args.getClass()).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getNMSClass(String string) {
        return getClass("net.minecraft.server." + version + "." + string);
    }

    static {
        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            iBlockData = getNMSClass("IBlockData");
            blockPosition = getNMSClass("BlockPosition");
            iBlockAccess = getNMSClass("IBlockAccess");
        }
    }
}
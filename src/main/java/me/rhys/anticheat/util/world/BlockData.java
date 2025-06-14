package me.rhys.anticheat.util.world;

import lombok.val;
import me.rhys.anticheat.tinyprotocol.api.ProtocolVersion;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.Reflections;
import me.rhys.anticheat.tinyprotocol.api.packets.reflections.types.WrappedClass;
import me.rhys.anticheat.util.MiscUtils;
import me.rhys.anticheat.util.world.blocks.*;
import me.rhys.anticheat.util.world.types.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Cake;
import org.bukkit.material.Gate;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Vine;

import java.util.*;
import java.util.stream.Stream;

public enum BlockData {
    _VINE((v, block) -> {
        Vine data = (Vine) block.getType().getNewData(block.getData());

        if(data.isOnFace(BlockFace.UP))
            return new SimpleCollisionBox(0., 0.9375, 0.,
                    1., 1., 1.);

        if(data.isOnFace(BlockFace.NORTH))
            return new SimpleCollisionBox(0., 0., 0.,
                    1., 1., 0.0625);

        if(data.isOnFace(BlockFace.EAST))
            return new SimpleCollisionBox(0.9375, 0., 0.,
                    1., 1., 1.);

        if(data.isOnFace(BlockFace.SOUTH))
            return new SimpleCollisionBox(0., 0., 0.9375,
                    1., 1., 1.);

        if(data.isOnFace(BlockFace.WEST))
            return new SimpleCollisionBox(0., 0., 0.,
                    0.0625, 1., 1.);

        return new SimpleCollisionBox(0,0,0,1.,1.,1.);
    }, XMaterial.VINE.parseMaterial()),

    _LIQUID(new SimpleCollisionBox(0, 0, 0, 1f, 0.9f, 1f),
            XMaterial.WATER.parseMaterial(), XMaterial.LAVA.parseMaterial(),
            MiscUtils.match("STATIONARY_LAVA"), MiscUtils.match("STATIONARY_WATER")),

    _BREWINGSTAND(new ComplexCollisionBox(
            new SimpleCollisionBox(0, 0, 0, 1, 0.125, 1),                      //base
            new SimpleCollisionBox(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625) //top
    ), XMaterial.BREWING_STAND.parseMaterial()),

   /* _RAIL((protocol, b) -> ReflectionUtil2.getBlockBoundingBox(b).toCollisionBox(),Arrays.stream(Material.values())
            .filter(mat -> mat.name().toLowerCase().contains("rail"))
            .toArray(Material[]::new)),*/

    _ANVIL((protocol, b) -> {
        BlockState state = b.getState();
        b.setType(XMaterial.ANVIL.parseMaterial());
        int dir = state.getData().getData() & 0b01;
        CollisionBox box;
        if (dir == 1) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        } else {
            box = new SimpleCollisionBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        }
        return box;
    }, XMaterial.ANVIL.parseMaterial())

    ,_WALL(new DynamicWall(), Arrays.stream(XMaterial.values())
            .filter(mat -> mat.name().contains("WALL"))
            .map(BlockData::m)
            .toArray(Material[]::new)),

    _SKULL((protocol, b) -> {
        int rotation = b.getState().getData().getData() & 7;

        CollisionBox box;
        switch (rotation) {
            case 1:
            default:
                box = new SimpleCollisionBox(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;
            case 2:
                box = new SimpleCollisionBox(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;
            case 3:
                box = new SimpleCollisionBox(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;
            case 4:
                box = new SimpleCollisionBox(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case 5:
                box = new SimpleCollisionBox(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }
        return box;
    }, XMaterial.SKELETON_SKULL.parseMaterial(), XMaterial.WITHER_SKELETON_SKULL.parseMaterial(),
            XMaterial.WITHER_SKELETON_WALL_SKULL.parseMaterial(), XMaterial.WITHER_SKELETON_SKULL.parseMaterial()),

    _DOOR(new DoorHandler(), Arrays.stream(Material.values())
            .filter(mat -> !mat.name().contains("TRAP") && mat.name().contains("DOOR"))
            .toArray(Material[]::new)),

    _HOPPER(new HopperBounding(), XMaterial.HOPPER.parseMaterial()),
    _CAKE((protocol, block) -> {
        Cake cake = (Cake) block.getType().getNewData(block.getData());

        double f1 = (1 + cake.getSlicesEaten() * 2) / 16D;

        return new SimpleCollisionBox(f1, 0, 0.0625, 1 - 0.0625, 0.5, 1 - 0.0625);
    }, MiscUtils.match("CAKE"), MiscUtils.match("CAKE_BLOCK")),

    _LADDER((protocol, b) -> {
        CollisionBox box = NoCollisionBox.INSTANCE;
        float var3 = 0.125F;

        byte data = b.getState().getData().getData();
        if (data == 2) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 1.0F - var3, 1.0F, 1.0F, 1.0F);
        } else if (data == 3) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var3);
        } else if (data == 4) {
            box = new SimpleCollisionBox(1.0F - var3, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else if (data == 5) {
            box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, var3, 1.0F, 1.0F);
        }
        return box;
    }, XMaterial.LADDER.parseMaterial()),

    _FENCE_GATE((protocol, b) -> {
        byte var5 = b.getState().getData().getData();

        CollisionBox box = NoCollisionBox.INSTANCE;
        if (!((Gate) b.getState().getData()).isOpen()) {
            if (var5 != 2 && var5 != 0) {
                box = new SimpleCollisionBox(0.375F, 0.0F, 0.0F, 0.625F, 1.5F, 1.0F);
            } else {
                box = new SimpleCollisionBox(0.0F, 0.0F, 0.375F, 1.0F, 1.5F, 0.625F);
            }
        }
        return box;
    }, Arrays.stream(XMaterial.values()).filter(mat -> mat.name().contains("FENCE") && mat.name().contains("GATE"))
            .map(XMaterial::parseMaterial)
            .toArray(Material[]::new)),

    _FENCE(new DynamicFence(), Arrays.stream(XMaterial.values())
            .filter(mat -> mat.name().equals("FENCE") || mat.name().endsWith("FENCE"))
            .map(BlockData::m)
            .toArray(Material[]::new)),
    _PANE(new DynamicPane(), MiscUtils.match("THIN_GLASS"), MiscUtils.match("STAINED_GLASS_PANE"),
            MiscUtils.match("IRON_FENCE")),


    _SNOW((protocol, b) -> {
        MaterialData state = b.getState().getData();
        int height = (state.getData() & 0b1111);
        if (height == 0) return new SimpleCollisionBox(0, 0, 0, 1, 0, 1); // return NoCollisionBox.INSTANCE;
        return new SimpleCollisionBox(0, 0, 0, 1, height * 0.125, 1);
    }, XMaterial.SNOW.parseMaterial()),

    _SLAB((protocol, b) -> {
        MaterialData state = b.getState().getData();
        if ((state.getData() & 8) == 0)
            return new SimpleCollisionBox(0, 0, 0, 1, .5, 1);
        else return new SimpleCollisionBox(0, .5, 0, 1, 1, 1);
    }, Arrays.stream(Material.values()).filter(mat ->
            mat.name().contains("STEP") || mat.name().contains("SLAB"))
            .filter(mat -> !mat.name().contains("DOUBLE"))
            .toArray(Material[]::new)),

    _STAIR((protocol, b) -> {
        MaterialData state = b.getState().getData();
        boolean inverted = (state.getData() & 4) != 0;
        int dir = (state.getData() & 0b11);
        SimpleCollisionBox top;
        SimpleCollisionBox bottom = new SimpleCollisionBox(0, 0, 0, 1, .5, 1);
        if (dir == 0) top = new SimpleCollisionBox(.5, .5, 0, 1, 1, 1);
        else if (dir == 1) top = new SimpleCollisionBox(0, .5, 0, .5, 1, 1);
        else if (dir == 2) top = new SimpleCollisionBox(0, .5, .5, 1, 1, 1);
        else top = new SimpleCollisionBox(0, .5, 0, 1, 1, .5);
        if (inverted) {
            top.offset(0, -.5, 0);
            bottom.offset(0, .5, 0);
        }
        return new ComplexCollisionBox(top, bottom);
    }, Arrays.stream(XMaterial.values()).filter(mat -> mat.name().contains("STAIRS"))
            .map(BlockData::m)
            .toArray(Material[]::new)),

    _CHEST(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.125, 1).expand(-0.125, 0, -0.125),
            XMaterial.CHEST.parseMaterial(),
            XMaterial.TRAPPED_CHEST.parseMaterial(),
            XMaterial.ENDER_CHEST.parseMaterial()),
    _ETABLE(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.25, 1),
            MiscUtils.match("ENCHANTMENT_TABLE")),
    _FRAME(new SimpleCollisionBox(0, 0, 0, 1, 1 - (0.0625 * 3), 1),
            MiscUtils.match("ENDER_PORTAL_FRAME")),

    _CARPET(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F), MiscUtils.match("CARPET")),
    _Daylight(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375, 1.0F),
            MiscUtils.match("DAYLIGHT_DETECTOR"), MiscUtils.match("DAYLIGHT_DETECTOR_INVERTED")),
    _LILIPAD((v, b) -> {
        if (v.isBelow(ProtocolVersion.V1_9))
            return new SimpleCollisionBox(0.0f, 0.0F, 0.0f, 1.0f, 0.015625F, 1.0f);
        return new SimpleCollisionBox(0.0625, 0.0F, 0.0625, 0.9375, 0.015625F, 0.9375);
    }, MiscUtils.match("WATER_LILY")),

    _BED(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625, 1.0F),
            Arrays.stream(XMaterial.values()).filter(mat -> mat.name().contains("BED") && !mat.name().contains("ROCK"))
                    .map(BlockData::m)
                    .toArray(Material[]::new)),


    _TRAPDOOR(new TrapDoorHandler(), Arrays.stream(Material.values())
            .filter(mat -> mat.name().contains("TRAP_DOOR")).toArray(Material[]::new)),

    _STUPID(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F),
            MiscUtils.match("DIODE_BLOCK_OFF"), MiscUtils.match("DIODE_BLOCK_ON"),
            MiscUtils.match("REDSTONE_COMPARATOR_ON"), MiscUtils.match("REDSTONE_COMPARATOR_OFF")),

    _STRUCTURE_VOID(new SimpleCollisionBox(0.375, 0.375, 0.375,
            0.625, 0.625, 0.625),
            XMaterial.STRUCTURE_VOID.parseMaterial()),

    _END_ROD(new DynamicRod(), XMaterial.END_ROD.parseMaterial()),
    _CAULDRON(new CouldronBounding(), XMaterial.CAULDRON.parseMaterial()),
    _CACTUS(new SimpleCollisionBox(0.0625, 0, 0.0625,
            1 - 0.0625, 1 - 0.0625, 1 - 0.0625), XMaterial.CACTUS.parseMaterial()),


    _PISTON_BASE(new PistonBaseCollision(), m(XMaterial.PISTON), m(XMaterial.STICKY_PISTON)),

    _PISTON_ARM(new PistonDickCollision(), m(XMaterial.PISTON_HEAD)),

    _SOULSAND(new SimpleCollisionBox(0, 0, 0, 1, 0.875, 1),
            XMaterial.SOUL_SAND.parseMaterial()),
    _PICKLE((version, block) -> {
        val wrapped = new WrappedClass(block.getClass());
        val getBlockData = wrapped.getMethod("getBlockData");
        val pickleClass = Reflections.getNMSClass("SeaPickle");
        Object pickle = getBlockData.invoke(block);

        int pickles = pickleClass.getMethod("getPickles").invoke(pickle);

        switch(pickles) {
            case 1:
                return new SimpleCollisionBox(6.0D / 15, 0.0, 6.0D / 15,
                        10.0D / 15, 6.0D / 15, 10.0D / 15);
            case 2:
                return new SimpleCollisionBox(3.0D / 15, 0.0D, 3.0D / 15,
                        13.0D / 15, 6.0D / 15, 13.0D / 15);
            case 3:
                return new SimpleCollisionBox(2.0D / 15, 0.0D, 2.0D / 15,
                        14.0D / 15, 6.0D / 15, 14.0D / 15);
            case 4:
                return new SimpleCollisionBox(2.0D / 15, 0.0D, 2.0D / 15,
                        14.0D / 15, 7.0D / 15, 14.0D / 15);
        }
        return NoCollisionBox.INSTANCE;
    }, XMaterial.SEA_PICKLE.parseMaterial()),
    _POT(new SimpleCollisionBox(0.3125, 0.0, 0.3125, 0.6875, 0.375, 0.6875),
            XMaterial.FLOWER_POT.parseMaterial()),

    _WALL_SIGN((version, block) -> {

        byte data = block.getData();
        double var4 = 0.28125;
        double var5 = 0.78125;
        double var6 = 0;
        double var7 = 1.0;
        double var8 = 0.125;

        BlockFace face;
        switch(data) {
            case 2:
                face = BlockFace.SOUTH;
                break;
            case 3:
                face = BlockFace.NORTH;
                break;
            case 4:
                face = BlockFace.EAST;
                break;
            case 5:
                face = BlockFace.WEST;
                break;
            default:
                face = BlockFace.DOWN;
                break;
        }

        face = !face.equals(BlockFace.DOWN) ? face.getOppositeFace() : BlockFace.DOWN;

        switch(face) {
            case NORTH:
                return new SimpleCollisionBox(var6, var4, 1.0 - var8, var7, var5, 1.0);
            case SOUTH:
                return new SimpleCollisionBox(var6, var4, 0.0, var7, var5, var8);
            case WEST:
                return new SimpleCollisionBox(1.0 - var8, var4, var6, 1.0, var5, var7);
            case EAST:
                return new SimpleCollisionBox(0.0, var4, var6, var8, var5, var7);
            default:
                return new SimpleCollisionBox(0,0,0,1,1,1);
        }
    }, Arrays.stream(Material.values()).filter(mat -> mat.name().contains("WALL_SIGN"))
            .toArray(Material[]::new)),

    _SIGN(new SimpleCollisionBox(0.25, 0.0, 0.25, 0.75, 1.0, 0.75),
            XMaterial.SIGN.parseMaterial(), XMaterial.LEGACY_SIGN_POST.parseMaterial()),
    _BUTTON((version, block) -> {
        BlockFace face;
        switch(block.getData() & 7) {
            case 0:
                face = BlockFace.UP;
                break;
            case 1:
                face = BlockFace.WEST;
                break;
            case 2:
                face = BlockFace.EAST;
                break;
            case 3:
                face = BlockFace.NORTH;
                break;
            case 4:
                face = BlockFace.SOUTH;
                break;
            case 5:
                face = BlockFace.DOWN;
                break;
            default:
                return NoCollisionBox.INSTANCE;
        }

        face = face.getOppositeFace();
        boolean flag = (block.getData() & 8) == 8; //is powered;
        double f2 = (float)(flag ? 1 : 2) / 16.0;
        switch(face) {
            case EAST:
                return new SimpleCollisionBox(0.0, 0.375, 0.3125, f2, 0.625, 0.6875);
            case WEST:
                return new SimpleCollisionBox(1.0 - f2, 0.375, 0.3125, 1.0, 0.625, 0.6875);
            case SOUTH:
                return new SimpleCollisionBox(0.3125, 0.375, 0.0, 0.6875, 0.625, f2);
            case NORTH:
                return new SimpleCollisionBox(0.3125, 0.375, 1.0 - f2, 0.6875, 0.625, 1.0);
            case UP:
                return new SimpleCollisionBox(0.3125, 0.0, 0.375, 0.6875, 0.0 + f2, 0.625);
            case DOWN:
                return new SimpleCollisionBox(0.3125, 1.0 - f2, 0.375, 0.6875, 1.0, 0.625);
        }
        return NoCollisionBox.INSTANCE;
    }, Arrays.stream(Material.values()).filter(mat -> mat.name().contains("BUTTON")).toArray(Material[]::new)),

    _LEVER((version, block) -> {
        byte data = (byte)(block.getData() & 7);
        BlockFace face;
        switch(data) {
            case 0:
            case 7:
                face = BlockFace.UP;
                break;
            case 1:
                face = BlockFace.WEST;
                break;
            case 2:
                face = BlockFace.EAST;
                break;
            case 3:
                face = BlockFace.NORTH;
                break;
            case 4:
                face = BlockFace.SOUTH;
                break;
            case 5:
            case 6:
                face = BlockFace.DOWN;
                break;
            default:
                return NoCollisionBox.INSTANCE;
        }

        double f = 0.1875;
        switch(face) {
            case EAST:
                return new SimpleCollisionBox(0.0, 0.2, 0.5 - f, f * 2.0, 0.8, 0.5 + f);
            case WEST:
                return new SimpleCollisionBox(1.0 - f * 2.0, 0.2, 0.5 - f, 1.0, 0.8, 0.5 + f);
            case SOUTH:
                return new SimpleCollisionBox(0.5 - f, 0.2, 0.0, 0.5 + f, 0.8, f * 2.0);
            case NORTH:
                return new SimpleCollisionBox(0.5 - f, 0.2, 1.0 - f * 2.0, 0.5 + f, 0.8, 1.0);
            case UP:
                return new SimpleCollisionBox(0.25, 0.0, 0.25, 0.75, 0.6, 0.75);
            case DOWN:
                return new SimpleCollisionBox(0.25, 0.4, 0.25, 0.75, 1.0, 0.75);
        }
        return NoCollisionBox.INSTANCE;
    }, XMaterial.LEVER.parseMaterial()),

    _NONE(NoCollisionBox.INSTANCE, Stream.of(XMaterial.TORCH, XMaterial.REDSTONE_TORCH,
            XMaterial.REDSTONE_WIRE, XMaterial.REDSTONE_WALL_TORCH, XMaterial.POWERED_RAIL, XMaterial.WALL_TORCH,
            XMaterial.RAIL, XMaterial.ACTIVATOR_RAIL, XMaterial.DETECTOR_RAIL, XMaterial.AIR, XMaterial.LONG_GRASS,
            XMaterial.TRIPWIRE, XMaterial.TRIPWIRE_HOOK)
            .map(BlockData::m)
            .toArray(Material[]::new)),

    _NONE2(NoCollisionBox.INSTANCE, Arrays.stream(XMaterial.values())
            .filter(mat -> {
                List<String> names = new ArrayList<>(Arrays.asList(mat.names));
                names.add(mat.name());
                return names.stream().anyMatch(name ->
                        name.contains("PLATE"));
            }).map(BlockData::m).toArray(Material[]::new)),
    _DEFAULT(new SimpleCollisionBox(0, 0, 0, 1, 1, 1),
            XMaterial.STONE.parseMaterial());

    private CollisionBox box;
    private CollisionFactory dynamic;
    private final Material[] materials;

    BlockData(CollisionBox box, Material... materials) {
        this.box = box;
        Set<Material> mList = new HashSet<>();
        mList.addAll(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Material[mList.size()]);
    }

    BlockData(CollisionFactory dynamic, Material... materials) {
        this.dynamic = dynamic;
        this.box = box;
        Set<Material> mList = new HashSet<>();
        mList.addAll(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Material[mList.size()]);
    }

    public CollisionBox getBox(Block block, ProtocolVersion version) {
        if (this.box != null)
            return this.box.copy().offset(block.getX(), block.getY(), block.getZ());
        return new DynamicCollisionBox(dynamic, block, version).offset(block.getX(), block.getY(), block.getZ());
    }

    private static final BlockData[] lookup = new BlockData[Material.values().length];

    static {
        for (BlockData data : values()) {
            for (Material mat : data.materials) lookup[mat.ordinal()] = data;
        }
    }

    public static BlockData getData(Material material) {
        Material matched = MiscUtils.match(material.toString());
        BlockData data = lookup[matched.ordinal()];
        return data != null ? data : _DEFAULT;
    }

    private static Material m(XMaterial xmat) {
        return xmat.parseMaterial();
    }
}
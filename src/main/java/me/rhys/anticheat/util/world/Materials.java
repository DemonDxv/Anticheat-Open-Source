package me.rhys.anticheat.util.world;

import org.bukkit.Material;

public class Materials {

    private Materials() {

    }

    private static final int[] MATERIAL_FLAGS = new int[Material.values().length];
    public static final int SOLID  = 0b00000000000000000000000000001;
    public static final int LADDER = 0b00000000000000000000000000010;
    public static final int WALL   = 0b00000000000000000000000000100;
    public static final int STAIRS = 0b00000000000000000000000001000;
    public static final int SLABS  = 0b00000000000000000000000010000;
    public static final int WATER  = 0b00000000000000000000000100000;
    public static final int LAVA   = 0b00000000000000000000001000000;
    public static final int LIQUID = 0b00000000000000000000001100000;
    public static final int ICE    = 0b00000000000000000000010000000;
    public static final int FENCE  = 0b00000000000000000000100000000;

    static {
        for (int i = 0; i < MATERIAL_FLAGS.length; i++) {
            Material material = Material.values()[i];

            //We use the one in BlockUtils also since we can't trust Material to include everything.
            if (material.isSolid()) {
                MATERIAL_FLAGS[i] |= SOLID;
            }
            if (material.name().endsWith("_STAIRS")) {
                MATERIAL_FLAGS[i] |= STAIRS;
            }

            if (material.name().contains("SLAB") || material.name().contains("STEP")) {
                MATERIAL_FLAGS[i] |= SLABS;
            }
        }

        // fix some types where isSolid() returns the wrong value
        MATERIAL_FLAGS[XMaterial.SLIME_BLOCK.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.COMPARATOR.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.REDSTONE_COMPARATOR_OFF.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.REDSTONE_COMPARATOR_ON.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.REPEATER.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.DIODE_BLOCK_OFF.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.DIODE_BLOCK_ON.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.SNOW.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.ANVIL.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.LILY_PAD.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.SKELETON_SKULL.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.SKELETON_WALL_SKULL.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.WITHER_SKELETON_SKULL.parseMaterial().ordinal()] = SOLID;
        MATERIAL_FLAGS[XMaterial.WITHER_SKELETON_WALL_SKULL.parseMaterial().ordinal()] = SOLID;

        // liquids
        MATERIAL_FLAGS[XMaterial.WATER.parseMaterial().ordinal()] |= LIQUID | WATER;
        MATERIAL_FLAGS[XMaterial.LAVA.parseMaterial().ordinal()] |= LIQUID | LAVA;
        MATERIAL_FLAGS[XMaterial.STATIONARY_LAVA.parseMaterial().ordinal()] |= LIQUID | LAVA;
        MATERIAL_FLAGS[XMaterial.STATIONARY_WATER.parseMaterial().ordinal()] |= LIQUID | WATER;

        // ladders
        MATERIAL_FLAGS[XMaterial.LADDER.parseMaterial().ordinal()] |= LADDER | SOLID;
        MATERIAL_FLAGS[XMaterial.VINE.parseMaterial().ordinal()] |= LADDER | SOLID;
        for (Material mat : Material.values()) {
            if (mat.name().contains("FENCE")) {
                if(!mat.name().contains("GATE")) MATERIAL_FLAGS[mat.ordinal()] |= FENCE | WALL;
                else MATERIAL_FLAGS[mat.ordinal()] |= WALL;
            }
            if(mat.name().contains("WALL")) MATERIAL_FLAGS[mat.ordinal()] |= WALL;
            if(mat.name().contains("PLATE")) MATERIAL_FLAGS[mat.ordinal()] = 0;
            if(mat.name().contains("BED") && !mat.name().contains("ROCK")) MATERIAL_FLAGS[mat.ordinal()]  |= SLABS;
            if(mat.name().contains("ICE")) MATERIAL_FLAGS[mat.ordinal()] |= ICE;
            if(mat.name().contains("CARPET")) MATERIAL_FLAGS[mat.ordinal()] = SOLID;
            if(mat.name().contains("SIGN")) MATERIAL_FLAGS[mat.ordinal()] = 0;
        }
    }

    public static int getBitmask(Material material) {
        return MATERIAL_FLAGS[material.ordinal()];
    }

    public static boolean checkFlag(Material material, int flag) {
        return (MATERIAL_FLAGS[material.ordinal()] & flag) == flag;
    }

    public static boolean isUsable(Material material) {
        String nameLower = material.name().toLowerCase();
        return material.isEdible()
                || nameLower.contains("bow")
                || nameLower.contains("sword")
                || nameLower.contains("trident");
    }

}
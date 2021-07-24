package me.rhys.anticheat.util.block;

import lombok.Getter;
import me.rhys.anticheat.base.user.User;
import me.rhys.anticheat.util.MaterialHelper;
import me.rhys.anticheat.util.box.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;

import java.util.List;

@Getter
public class BlockChecker {
    private final BoundingBox boundingBox;
    private final User user;

    public BlockChecker(User user) {
        this.boundingBox = user.getBoundingBox();
        this.user = user;
    }

    private boolean lillyPad, skull, carpet, cake, onGround, nearLava, nearWater, nearIce, climbable, slime, piston, snow, fence, bed,
            stair, slab, movingUp, underBlock, web, shulker, insideBlock, collideHorizontal;

    public void processBlocks() {

        List<CollideEntry> collidedBlocks = this.boundingBox.getCollidedBlocks(this.user.getPlayer());

        this.underBlock = new BoundingBox(
                (float) this.user.getCurrentLocation().getX(),
                (float) this.user.getPlayer().getEyeLocation().getY(),
                (float) this.user.getCurrentLocation().getZ(),
                (float) this.user.getCurrentLocation().getX(),
                (float) this.user.getPlayer().getEyeLocation().getY(),
                (float) this.user.getCurrentLocation().getZ()).expand(.3, .0, .3)
                .addXYZ(0, .4, 0).getCollidedBlocks(this.user.getPlayer())
                .stream().filter(CollideEntry::isChunkLoaded)
                .anyMatch(collideEntry -> (!collideEntry.getBlock().isLiquid()
                        && collideEntry.getBlock().getType().isSolid()));


        this.collideHorizontal = new BoundingBox(
                (float) this.user.getCurrentLocation().getX(),
                (float) this.user.getPlayer().getEyeLocation().getY(),
                (float) this.user.getCurrentLocation().getZ(),
                (float) this.user.getCurrentLocation().getX(),
                (float) this.user.getPlayer().getEyeLocation().getY(),
                (float) this.user.getCurrentLocation().getZ()).expand(.3, .0, .3)
                .addXYZ(0.6, 0, 0.6).getCollidedBlocks(this.user.getPlayer())
                .stream().filter(CollideEntry::isChunkLoaded)
                .anyMatch(collideEntry -> collideEntry.getBlock().getType().isSolid());

        this.nearWater = new BoundingBox(
                (float) this.user.getCurrentLocation().getX(),
                (float) this.user.getPlayer().getEyeLocation().getY(),
                (float) this.user.getCurrentLocation().getZ(),
                (float) this.user.getCurrentLocation().getX(),
                (float) this.user.getPlayer().getEyeLocation().getY(),
                (float) this.user.getCurrentLocation().getZ()).expand(.0, .0, .0)
                .addXYZ(0, -0.3, 0).getCollidedBlocks(this.user.getPlayer())
                .stream().filter(CollideEntry::isChunkLoaded)
                .anyMatch(collideEntry -> collideEntry.getBlock().isLiquid());

        this.nearLava = new BoundingBox(
                (float) this.user.getCurrentLocation().getX(),
                (float) this.user.getPlayer().getEyeLocation().getY(),
                (float) this.user.getCurrentLocation().getZ(),
                (float) this.user.getCurrentLocation().getX(),
                (float) this.user.getPlayer().getEyeLocation().getY(),
                (float) this.user.getCurrentLocation().getZ()).expand(.0, .0, .0)
                .addXYZ(0, -0.3, 0).getCollidedBlocks(this.user.getPlayer())
                .stream().filter(CollideEntry::isChunkLoaded)
                .anyMatch(collideEntry -> collideEntry.getBlock().isLiquid());


        collidedBlocks.stream().filter(CollideEntry::isChunkLoaded).forEach(collideEntry -> {
            boolean checkMovingUp = false;
            Class<? extends MaterialData> blockData = collideEntry.getBlock().getType().getData();

            if (collideEntry.getBlock().getType().isSolid()) {
                this.onGround = true;

                if (collideEntry.getBoundingBox().intersectsWithBox(user.getBoundingBox())) {
                    insideBlock = true;
                }
            }


            Block block = collideEntry.getBlock();

            switch (block.getType()) {
                case STATIONARY_LAVA:
                case LAVA: {
                 //   this.nearLava = true;
                    break;
                }

                case WATER:
                case STATIONARY_WATER: {
                    if (collideEntry.getBoundingBox().intersectsWithBox(user.getBoundingBox()) && block.isLiquid()) {
                     //   this.nearWater = true;
                    }
                    break;
                }

                case SKULL: {
                    skull = true;
                }

                case WATER_LILY: {
                    lillyPad = true;
                }

                case CARPET: {
                    this.carpet = true;
                    break;
                }

                case CAKE_BLOCK:
                case CAKE: {
                    this.cake = true;
                    break;
                }

                case ICE:
                case PACKED_ICE: {
                    this.nearIce = true;
                    break;
                }

                case LADDER:
                case VINE: {
                    this.climbable = true;
                    break;
                }

                case SLIME_BLOCK: {
                    this.slime = true;
                    break;
                }

                case PISTON_EXTENSION:
                case PISTON_MOVING_PIECE: {
                    this.piston = true;
                    break;
                }

                case PISTON_STICKY_BASE:
                case PISTON_BASE: {

                    break;
                }

                case SNOW: {
                    this.snow = true;
                    break;
                }

                case COBBLE_WALL:
                case FENCE: {
                    this.fence = true;
                    break;
                }

                case BED:
                case BED_BLOCK: {
                    this.bed = true;
                    break;
                }

                case SANDSTONE_STAIRS:
                case SMOOTH_STAIRS:
                case SPRUCE_WOOD_STAIRS:
                case ACACIA_STAIRS:
                case BIRCH_WOOD_STAIRS:
                case BRICK_STAIRS:
                case COBBLESTONE_STAIRS:
                case DARK_OAK_STAIRS:
                case JUNGLE_WOOD_STAIRS:
                case NETHER_BRICK_STAIRS:
                case QUARTZ_STAIRS:
                case RED_SANDSTONE_STAIRS:
                case WOOD_STAIRS: {
                    this.stair = true;
                    checkMovingUp = true;
                    break;
                }

                case WEB: {
                    this.web = true;
                    break;
                }
            }

            if (collideEntry.getBlock().getType() == Material.STEP
                    || collideEntry.getBlock().getType() == Material.STONE_SLAB2
                    || blockData == Step.class
                    || blockData == WoodenStep.class) {
                slab = true;
                checkMovingUp = true;
            }

            if (checkMovingUp) {
                double boxY = collideEntry.getBoundingBox().getMaximum().getY();
                double delta = Math.abs(boxY - user.getBlockData().lastBlockY);
                this.movingUp = delta > 0 && Math.abs(user.getMovementProcessor().getDeltaY()) > 0 && this.onGround;
                user.getBlockData().lastBlockY = boxY;
            }

            this.processOtherBlocks(block);
        });
    }

    //For 1.9+ blocks etc..
    void processOtherBlocks(Block block) {
        this.shulker = MaterialHelper.isShulker(block);
    }
}

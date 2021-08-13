package tfar.gravitymod.api;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import tfar.gravitymod.GravityMod;
import tfar.gravitymod.common.util.boundingboxes.GravityAxisAlignedBB;

//TODO: Replace anonymous inner BlockPos classes with non-inner classes (faster, no reference to 'EnumGravityDirection.this' passed around)

/**
 * Enum of the gravity direction objects.
 * Created by Mysteryem on 2016-08-14.
 */
public enum EnumGravityDirection implements IStringSerializable {
    UP(new Vec3i(0, 0, 180), "up", EnumFacing.UP) {
        @Override
        public double[] adjustXYZValues(double x, double y, double z) {
            return new double[]{-x, -y, z};
        }

        // Doing nothing is correct because the rotation of the player occurs about their position, which lines up with
        // how the UP gravity hitboxes are set up
        @Override
        public void applyOtherPlayerRenderTransformations(EntityPlayer player) {/*do nothing*/}

        @Override
        public GravityAxisAlignedBB getGravityAdjustedAABB(EntityPlayer player, float width, float height) {
            double widthOver2 = width / 2f;
            return new GravityAxisAlignedBB(
                    API.getGravityDirectionCapability(player),
                    player.posX - widthOver2, player.posY - height, player.posZ - widthOver2,
                    player.posX + widthOver2, player.posY, player.posZ + widthOver2
            );
        }

        @Override
        public void returnCentreOfGravityToPlayerPos(EntityPlayer player) {
            player.posY -= player.height / 2;
        }

        @Override
        public void offsetCentreOfGravityFromPlayerPos(EntityPlayer player) {
            player.posY += player.height / 2;
        }

        @Override
        public EnumGravityDirection getInverseAdjustmentFromDOWNDirection() {
            return this;
        }

        @Override
        public EnumGravityDirection getOpposite() {
            return DOWN;
        }

        @Override
        public void resetPositionToBB(EntityLivingBase entityLivingBase, AxisAlignedBB bb) {
            entityLivingBase.posX = (bb.minX + bb.maxX) / 2.0D;
            entityLivingBase.posY = bb.maxY;
            entityLivingBase.posZ = (bb.minZ + bb.maxZ) / 2.0D;
        }

        @Override
        public BlockPos makeRelativeBlockPos(BlockPos blockPos) {
            return new DirectionAwareBlockPos(blockPos) {
                @Override
                public BlockPos down(int n) {
                    return super.up(n);
                }

                @Override
                public BlockPos up(int n) {
                    return super.down(n);
                }

                @Override
                public BlockPos east(int n) {
                    return super.west(n);
                }

                @Override
                public BlockPos west(int n) {
                    return super.east(n);
                }
            };
        }

    },
    DOWN(new Vec3i(0, 0, 0), "down", EnumFacing.DOWN) {
        @Override
        public double[] adjustXYZValues(double x, double y, double z) {
            return new double[]{x, y, z};
        }

        @Override
        public void applyOtherPlayerRenderTransformations(EntityPlayer player) {/*do nothing*/}

        @Override
        public GravityAxisAlignedBB getGravityAdjustedAABB(EntityPlayer player, float width, float height) {
            double widthOver2 = width / 2f;
            return new GravityAxisAlignedBB(
                    API.getGravityDirectionCapability(player),
                    player.posX - widthOver2, player.posY, player.posZ - widthOver2,
                    player.posX + widthOver2, player.posY + height, player.posZ + widthOver2
            );
        }

        @Override
        public void returnCentreOfGravityToPlayerPos(EntityPlayer player) {
            player.posY += player.height / 2;
        }

        @Override
        public void offsetCentreOfGravityFromPlayerPos(EntityPlayer player) {
            player.posY -= player.height / 2;
        }

        @Override
        public EnumGravityDirection getInverseAdjustmentFromDOWNDirection() {
            return this;
        }

        @Override
        public EnumGravityDirection getOpposite() {
            return UP;
        }

        @Override
        public void resetPositionToBB(EntityLivingBase entityLivingBase, AxisAlignedBB bb) {
            entityLivingBase.posX = (bb.minX + bb.maxX) / 2.0D;
            entityLivingBase.posY = bb.minY;
            entityLivingBase.posZ = (bb.minZ + bb.maxZ) / 2.0D;
        }

        @Override
        public BlockPos makeRelativeBlockPos(BlockPos blockPos) {
            return blockPos;
        }

    };

    private final Vec3i cameraTransformVars;
    private final String name;

    EnumGravityDirection(Vec3i cameraTransformVars, String name, EnumFacing facingEquivalent) {
        this.cameraTransformVars = cameraTransformVars;
        this.name = name;
    }

    public static EnumGravityDirection fromEnumFacing(EnumFacing enumFacing) {
        switch (enumFacing) {
            case DOWN:
                return DOWN;
            case UP:
                return UP;
            default:throw new RuntimeException();
        }
    }

    /**
     * Will return DOWN if the int argument is out of bounds
     *
     * @param ordinal
     * @return
     */
    public static EnumGravityDirection getSafeDirectionFromOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < EnumGravityDirection.values().length) {
            return EnumGravityDirection.values()[ordinal];
        }
        else {
            return EnumGravityDirection.DOWN;
        }
    }

    public abstract void applyOtherPlayerRenderTransformations(EntityPlayer player);

    public abstract BlockPos makeRelativeBlockPos(BlockPos blockPos);

    public double[] adjustXYZValuesMaintainSigns(double x, double y, double z) {
        double[] values = this.adjustXYZValues(x, y, z);
        double[] signs = this.adjustXYZValues(1, 1, 1);
        return new double[]{values[0] * signs[0], values[1] * signs[1], values[2] * signs[2]};
    }

    public abstract double[] adjustXYZValues(double x, double y, double z);

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the opposite gravity direction to this. For all but UP and DOWN, it is the same as this.getInverseAdjustmentFromDOWNDirection()
     *
     * @return The opposite direction to this.
     */
    public EnumGravityDirection getOpposite() {
        return this.getInverseAdjustmentFromDOWNDirection();
    }

    public abstract EnumGravityDirection getInverseAdjustmentFromDOWNDirection();

    public void postModifyPlayerOnGravityChange(EntityPlayer player, EnumGravityDirection oldDirection, Vec3d inputEyePos) {

        // Moves the player's position to their centre of gravity
        oldDirection.returnCentreOfGravityToPlayerPos(player);
        // Moves the player's position to the new place for this gravity direction, such that the player's centre of
        // gravity is in the same place as when postModifyPlayerOnGravityChange was called
        this.offsetCentreOfGravityFromPlayerPos(player);

        // Move the player to try and get them out of a wall. Being inside of a block for even a single tick causes
        // suffocation damage
        this.setBoundingBoxAndPositionOnGravityChange(player, oldDirection, inputEyePos);
    }

    public abstract void returnCentreOfGravityToPlayerPos(EntityPlayer player);

    public abstract void offsetCentreOfGravityFromPlayerPos(EntityPlayer player);

    private void setBoundingBoxAndPositionOnGravityChange(EntityPlayer player, EnumGravityDirection oldDirection, Vec3d oldEyePos) {
        AxisAlignedBB axisAlignedBB = this.getGravityAdjustedAABB(player);
        player.resetPositionToBB();

        if (player.world.collidesWithAnyBlock(axisAlignedBB)) {
            // After rotating about the player's centre of gravity, the player is now partially inside of a block

            // TODO: Test being a 'spider' player and trying to change gravity direction in tight places
            // TODO: Re-add choosing the correct gravity direction and using that to inverseAdjust the movement values, so that 'spider' players don't get
            // stuck in walls
            // Instead of trying to move the player in all 6 directions to see which would, we can eliminate all but 2
            EnumGravityDirection directionToTry;
            double distanceToMove;

            // The player has a relatively normal hitbox
            if (player.height > player.width) {
                // Collision will be happening either above or below the player's centre of gravity from the
                // NEW gravity direction's perspective
                distanceToMove = (player.height - player.width) / 2;
                directionToTry = this;
            }
            // The player must be some sort of pancake, e.g. a spider
            else if (player.height < player.width) {
                // Collision will be happening either above or below the player's centre of gravity from the
                // OLD gravity direction's perspective
                distanceToMove = (player.width - player.height) / 2;
                directionToTry = oldDirection;
            }
            // The player is a cube, meaning that their collision/bounding box won't have actually changed shape after being rotated/moved
            else {
                // As rotation of the player occurs about their centre of gravity
                // This scenario means that the player was already inside a block when they rotated as this should be impossible otherwise

                // Not going to do anything in this case
                player.setEntityBoundingBox(axisAlignedBB);
                return;
            }

            // Get the movement that is considered 'up' by distanceToMove
            double[] adjustedMovement = directionToTry.adjustXYZValues(0, distanceToMove, 0);

            // Inverse to undo the adjustment caused by GravityAxisAlignedBB.offset(...)
            adjustedMovement = this.getInverseAdjustmentFromDOWNDirection().adjustXYZValues(adjustedMovement[0], adjustedMovement[1], adjustedMovement[2]);

            // Moving 'up' from the rotated player's perspective
            AxisAlignedBB secondTry = axisAlignedBB.offset(adjustedMovement[0], adjustedMovement[1], adjustedMovement[2]);


            // We try 'up' first because even if we move the player too far, their gravity will move them back 'down'
            if (player.world.collidesWithAnyBlock(secondTry)) {

                // Moving 'down' from the rotated player's perspective
                AxisAlignedBB thirdTry = axisAlignedBB.offset(-adjustedMovement[0], -adjustedMovement[1], -adjustedMovement[2]);

                if (player.world.collidesWithAnyBlock(thirdTry)) {
                    // Uh oh, looks like the player decided to rotate in a too small place
                    // Imagine a 2 block tall, 1 block wide player standing in a 2 block tall, one block wide space
                    // and then changing from UP/DOWN gravity to NORTH/EAST/SOUTH/WEST gravity
                    // they cannot possibly fit, we'll settle for putting the bottom of their bb at an integer value
                    // as well as trying one block up (relative)

                    // Move the player such that their old eye position is the same as the new one, this should limit suffocation
                    player.setEntityBoundingBox(axisAlignedBB);
                    player.resetPositionToBB();

                    Vec3d newEyePos = player.getPositionVector().addVector(0, player.getEyeHeight(), 0);
                    Vec3d eyesDifference = oldEyePos.subtract(newEyePos);
                    Vec3d adjustedDifference = this.getInverseAdjustmentFromDOWNDirection().adjustLookVec(eyesDifference);
                    AxisAlignedBB givenUp = axisAlignedBB.offset(adjustedDifference.x, adjustedDifference.y, adjustedDifference.z);
                    //TODO: Set position at feet to closest int so we don't fall through blocks
                    double relativeBottomOfBB = GravityAxisAlignedBB.getRelativeBottom(givenUp);
                    long rounded = Math.round(relativeBottomOfBB);
                    double difference = rounded - relativeBottomOfBB;
                    //Try one block up (relative) from the found position to start with, to try and avoid falling through the block that is now at our feet
                    givenUp = givenUp.offset(0, difference + 1, 0);
                    //If one block up collided, then we have no choice but to choose the block below
                    if (player.world.collidesWithAnyBlock(givenUp)) {
                        givenUp = givenUp.offset(0, -1, 0);
                    }
                    axisAlignedBB = givenUp;

                }
                else {
                    // Moving 'down' did not collide with the world
                    axisAlignedBB = thirdTry;

                }
            }
            else {
                // Moving 'up' did not collide with the world
                axisAlignedBB = secondTry;
            }
        }
        else if (GravityMod.GENERAL_DEBUG) {
            GravityMod.logInfo("Player's new hitbox fit in the world without moving the player");
        }
        player.setEntityBoundingBox(axisAlignedBB);
        player.resetPositionToBB();
    }

    public AxisAlignedBB getGravityAdjustedAABB(EntityPlayer player) {
        return this.getGravityAdjustedAABB(player, player.width, player.height);
    }

    public Vec3d adjustLookVec(Vec3d input) {
        double[] d = this.adjustXYZValues(input.x, input.y, input.z);
        return new Vec3d(d[0], d[1], d[2]);
    }

    public abstract GravityAxisAlignedBB getGravityAdjustedAABB(EntityPlayer player, float width, float height);

    // I'm lazy and don't want to put unnecessary code in the overridden methods of the enum constants
    public void resetPositionToBB(EntityLivingBase entityLivingBase) {
        this.resetPositionToBB(entityLivingBase, entityLivingBase.getEntityBoundingBox());
    }

    public abstract void resetPositionToBB(EntityLivingBase entityLivingBase, AxisAlignedBB bb);

    public void runCameraTransformation() {
        Vec3i vars = this.getCameraTransformVars();
        int x = vars.getX();
        int y = vars.getY();
        int z = vars.getZ();
        if (x != 0) {
            GlStateManager.rotate(x, 1, 0, 0);
        }
        if (y != 0) {
            GlStateManager.rotate(y, 0, 1, 0);
        }
        if (z != 0) {
            GlStateManager.rotate(z, 0, 0, 1);
        }

    }

    public Vec3i getCameraTransformVars() {
        return this.cameraTransformVars;
    }

    class DirectionAwareBlockPos extends BlockPos {

        public DirectionAwareBlockPos(int x, int y, int z) {
            super(x, y, z);
        }

        public DirectionAwareBlockPos(double x, double y, double z) {
            super(x, y, z);
        }

        public DirectionAwareBlockPos(Entity source) {
            super(source);
        }

        public DirectionAwareBlockPos(Vec3d vec) {
            super(vec);
        }

        public DirectionAwareBlockPos(Vec3i source) {
            super(source);
        }

        public DirectionAwareBlockPos(BlockPos other) {
            super(other.getX(), other.getY(), other.getZ());
        }

        @Override
        public BlockPos add(double x, double y, double z) {
            double[] d = EnumGravityDirection.this.adjustXYZValues(x, y, z);
            return super.add(d[0], d[1], d[2]);
        }

        @Override
        public BlockPos add(int x, int y, int z) {
            double[] d = EnumGravityDirection.this.adjustXYZValues(x, y, z);
            return super.add(d[0], d[1], d[2]);
        }

        @Override
        public BlockPos add(Vec3i vec) {
            double[] d = EnumGravityDirection.this.adjustXYZValues(vec.getX(), vec.getY(), vec.getZ());
            return super.add(d[0], d[1], d[2]);
        }

        @Override
        public BlockPos subtract(Vec3i vec) {
            double[] d = EnumGravityDirection.this.adjustXYZValues(vec.getX(), vec.getY(), vec.getZ());
            return super.add(d[0], d[1], d[2]);
        }
    }
}

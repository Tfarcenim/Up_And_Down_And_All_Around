package tfar.gravitymod.api;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import tfar.gravitymod.GravityMod;
import tfar.gravitymod.common.util.boundingboxes.GravityAxisAlignedBB;

public class util {

    public static void postModifyPlayerOnGravityChange(boolean up, EntityPlayer player, boolean oldDirection, Vec3d inputEyePos) {

        // Moves the player's position to their centre of gravity
        returnCentreOfGravityToPlayerPos(oldDirection,player);
        // Moves the player's position to the new place for this gravity direction, such that the player's centre of
        // gravity is in the same place as when postModifyPlayerOnGravityChange was called
        offsetCentreOfGravityFromPlayerPos(up,player);

        // Move the player to try and get them out of a wall. Being inside of a block for even a single tick causes
        // suffocation damage
        setBoundingBoxAndPositionOnGravityChange(up, player, oldDirection, inputEyePos);
    }

    private static void setBoundingBoxAndPositionOnGravityChange(boolean up, EntityPlayer player, boolean oldDirection, Vec3d oldEyePos) {
        AxisAlignedBB axisAlignedBB = getGravityAdjustedAABB(up,player);
        player.resetPositionToBB();

        if (player.world.collidesWithAnyBlock(axisAlignedBB)) {
            // After rotating about the player's centre of gravity, the player is now partially inside of a block

            // TODO: Test being a 'spider' player and trying to change gravity direction in tight places
            // TODO: Re-add choosing the correct gravity direction and using that to inverseAdjust the movement values, so that 'spider' players don't get
            // stuck in walls
            // Instead of trying to move the player in all 6 directions to see which would, we can eliminate all but 2
            boolean directionToTry;
            double distanceToMove;

            // The player has a relatively normal hitbox
            if (player.height > player.width) {
                // Collision will be happening either above or below the player's centre of gravity from the
                // NEW gravity direction's perspective
                distanceToMove = (player.height - player.width) / 2;
                directionToTry = up;
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
            double[] adjustedMovement = adjustXYZValues(directionToTry,0, distanceToMove, 0);

            // Inverse to undo the adjustment caused by GravityAxisAlignedBB.offset(...)
            adjustedMovement =adjustXYZValues(up,adjustedMovement[0], adjustedMovement[1], adjustedMovement[2]);

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
                    Vec3d adjustedDifference = adjustLookVec(up,eyesDifference);
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

    public static double[] adjustXYZValues(boolean up, double x, double y, double z) {
        if (up) {
            return new double[]{-x, -y, z};
        } else {
            return new double[]{x, y, z};
        }
    }

    public static void returnCentreOfGravityToPlayerPos(boolean up, EntityPlayer player) {
        if (up) {
            player.posY -= player.height / 2;
        } else {
            player.posY += player.height / 2;
        }
    }

    public static Vec3d adjustLookVec(boolean up, Vec3d input) {
        double[] d = adjustXYZValues(up,input.x, input.y, input.z);
        return new Vec3d(d[0], d[1], d[2]);
    }


    public static void offsetCentreOfGravityFromPlayerPos(boolean up, EntityPlayer player) {
        if (up) {
            player.posY += player.height / 2;
        } else {
            player.posY -= player.height / 2;
        }
    }


    public static AxisAlignedBB getGravityAdjustedAABB(boolean up,EntityPlayer player) {
        return getGravityAdjustedAABB(up,player, player.width, player.height);
    }

    public static GravityAxisAlignedBB getGravityAdjustedAABB(boolean up, EntityPlayer player, float width, float height) {

        double widthOver2 = width / 2f;
        if (up) {
            return new GravityAxisAlignedBB(
                    API.getGravityDirectionCapability(player),
                    player.posX - widthOver2, player.posY - height, player.posZ - widthOver2,
                    player.posX + widthOver2, player.posY, player.posZ + widthOver2
            );
        } else {
            return new GravityAxisAlignedBB(
                    API.getGravityDirectionCapability(player),
                    player.posX - widthOver2, player.posY, player.posZ - widthOver2,
                    player.posX + widthOver2, player.posY + height, player.posZ + widthOver2
            );
        }
    }

    public static double[] adjustXYZValuesMaintainSigns(boolean direction, double x, double y, double z) {
        double[] values = adjustXYZValues(direction,x, y, z);
        double[] signs = adjustXYZValues(direction,1, 1, 1);
        return new double[]{values[0] * signs[0], values[1] * signs[1], values[2] * signs[2]};
    }


    public static void runCameraTransformation(boolean up) {
        Vec3i vars = getCameraTransformVars(up);
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

    public static Vec3i getCameraTransformVars(boolean up) {
        if (up) {
            return new Vec3i(0, 0, 180);
        }
        else {
            return new Vec3i(0, 0, 0);
        }
    }
}

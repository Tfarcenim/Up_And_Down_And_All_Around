package tfar.gravitymod.client.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tfar.gravitymod.api.util;
import tfar.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import tfar.gravitymod.common.capabilities.gravitydirection.IGravityDirectionCapability;
import tfar.gravitymod.common.config.ConfigHandler;
import tfar.gravitymod.common.util.Vec3dHelper;

/**
 * Used to roll the player's camera according to their current gravity
 * <p>
 * Created by Mysteryem on 2016-08-04.
 */
@SideOnly(Side.CLIENT)
public class PlayerCameraListener {

    @SubscribeEvent
    public static void onCameraSetup(CameraSetup event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity renderViewEntity = minecraft.getRenderViewEntity();
        if (renderViewEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)renderViewEntity;
            IGravityDirectionCapability capability = GravityDirectionCapability.getGravityCapability(player);
            boolean gravityDirection = capability.getDirection();

            float transitionRollAmount = 0;
            int timeoutTicks = capability.getTimeoutTicks();
            double effectiveTimeoutTicks = timeoutTicks - (1 * event.getRenderPartialTicks());

            double interpolatedPitch = (player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * event.getRenderPartialTicks());
            double interpolatedYaw = (player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.getRenderPartialTicks());

            interpolatedPitch %= 360;
            interpolatedYaw %= 360;

            Vec3d interpolatedLookVec = Vec3dHelper.getPreciseVectorForRotation(interpolatedPitch, interpolatedYaw);
            Vec3d relativeInterpolatedLookVec = util.adjustLookVec(gravityDirection,interpolatedLookVec);
            double[] precisePitchAndYawFromVector = Vec3dHelper.getPrecisePitchAndYawFromVector(relativeInterpolatedLookVec);

            double relativeInterpolatedPitch = precisePitchAndYawFromVector[Vec3dHelper.PITCH];
            double relativeInterpolatedYaw = precisePitchAndYawFromVector[Vec3dHelper.YAW];

            double xTranslation = 0;
            double yTranslation = 0;
            double zTranslation = 0;

            if (timeoutTicks != 0 && effectiveTimeoutTicks > ConfigHandler.transitionAnimationRotationEnd) {

                double rotationAngle;

                // We don't want to run all this code every render tick, so we store the angle to rotate by over the transition
                if (!capability.hasTransitionAngle()) {
                    double yaw = player.rotationYaw;
                    double pitch = player.rotationPitch;

                    // Get the absolute look vector
                    Vec3d absoluteLookVec = Vec3dHelper.getPreciseVectorForRotation(pitch, yaw);

                    // Get the relative look vector for the current gravity direction
                    Vec3d relativeCurrentLookVector = util.adjustLookVec(gravityDirection,absoluteLookVec);
                    // Get the pitch and yaw from the relative look vector
                    double[] pitchAndYawRelativeCurrentLook = Vec3dHelper.getPrecisePitchAndYawFromVector(relativeCurrentLookVector);
                    // Pitch - 90, -90 changes it from the forwards direction to the upwards direction
                    double relativeCurrentPitch = pitchAndYawRelativeCurrentLook[Vec3dHelper.PITCH] - 90;
                    // Yaw
                    double relativeCurrentYaw = pitchAndYawRelativeCurrentLook[Vec3dHelper.YAW];
                    // Get the relative upwards vector
                    Vec3d relativeCurrentUpVector = Vec3dHelper.getPreciseVectorForRotation(
                            relativeCurrentPitch, relativeCurrentYaw);
                    // Get the absolute vector for the relative upwards vector
                    Vec3d absoluteCurrentUpVector = util.adjustLookVec(gravityDirection,relativeCurrentUpVector);

                    // Get the relative look vector for the previous gravity direction
                    Vec3d relativePrevLookVector = util.adjustLookVec(capability.getPrevDirection(),absoluteLookVec);
                    // Get the pitch and yaw from the relative look vector
                    double[] pitchAndYawRelativePrevLook = Vec3dHelper.getPrecisePitchAndYawFromVector(relativePrevLookVector);
                    // Pitch - 90, -90 changes it from the forwards direction to the upwards direction
                    double relativePrevPitch = pitchAndYawRelativePrevLook[Vec3dHelper.PITCH] - 90;
                    // Yaw
                    double relativePrevYaw = pitchAndYawRelativePrevLook[Vec3dHelper.YAW];
                    // Get the relative upwards vector
                    Vec3d relativePrevUpVector = Vec3dHelper.getPreciseVectorForRotation(
                            relativePrevPitch, relativePrevYaw);
                    // Get the absolute vector for the relative upwards vector
                    Vec3d absolutePrevUpVector = util.adjustLookVec(capability.getPrevDirection(),relativePrevUpVector);

                    //See http://stackoverflow.com/a/33920320 for the maths
                    rotationAngle = (180d / Math.PI) * Math.atan2(
                            absoluteCurrentUpVector.crossProduct(absolutePrevUpVector).dotProduct(absoluteLookVec),
                            absoluteCurrentUpVector.dotProduct(absolutePrevUpVector));

                    capability.setTransitionAngle(rotationAngle);
                }
                else {
                    rotationAngle = capability.getTransitionAngle();
                }

                double numerator = GravityDirectionCapability.DEFAULT_TIMEOUT - effectiveTimeoutTicks;
                double denominator = ConfigHandler.transitionAnimationRotationLength;

                double multiplierZeroToOne = numerator / denominator;
                double multiplierOneToZero = 1 - multiplierZeroToOne;

                transitionRollAmount = (float)(rotationAngle * multiplierOneToZero);
                Vec3d eyePosChangeVector = capability.getEyePosChangeVector();
                xTranslation = eyePosChangeVector.x * multiplierOneToZero;
                yTranslation = eyePosChangeVector.y * multiplierOneToZero;
                zTranslation = eyePosChangeVector.z * multiplierOneToZero;
                minecraft.renderGlobal.setDisplayListEntitiesDirty();
            }

            relativeInterpolatedPitch %= 360;
            relativeInterpolatedYaw %= 360;

            // Read these in reverse order
            // 5: Rotate by the relative player's pitch, this, combined with the camera transformation set the correct camera pitch
            GlStateManager.rotate((float)relativeInterpolatedPitch, 1, 0, 0);
            // 4: Rotate by the relative player's yaw, this, combined with the camera transformation sets the correct camera yaw
            GlStateManager.rotate((float)relativeInterpolatedYaw, 0, 1, 0);

            // 3: Now that our look direction is effectively 0 yaw and 0 pitch, perform the rotation specific for this gravity
            util.runCameraTransformation(gravityDirection);

            // 2: Undo the absolute yaw rotation of the player
            GlStateManager.rotate((float)-interpolatedYaw, 0, 1, 0);
            // 1: Undo the absolute pitch rotation of the player
            GlStateManager.rotate((float)-interpolatedPitch, 1, 0, 0);

            //If using the event's roll, the rotation calls that use event.getRoll() need to be un-commented
//            event.setRoll(event.getRoll() + transitionRollAmount);
            GlStateManager.rotate(transitionRollAmount, 0, 0, 1);

//            GlStateManager.rotate(event.getRoll(), 0, 0, 1);
            GlStateManager.rotate(event.getPitch(), 1, 0, 0);
            GlStateManager.rotate(event.getYaw(), 0, 1, 0);
            GlStateManager.translate(xTranslation, yTranslation, zTranslation);
            GlStateManager.rotate(-event.getYaw(), 0, 1, 0);
            GlStateManager.rotate(-event.getPitch(), 1, 0, 0);
//            GlStateManager.rotate(-event.getRoll(), 0, 0, 1);

        }
    }
}

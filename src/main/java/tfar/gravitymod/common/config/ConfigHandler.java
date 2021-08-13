package tfar.gravitymod.common.config;

import tfar.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;

/**
 * Created by Mysteryem on 07/03/2017.
 */
public class ConfigHandler {

    public static float oppositeDirectionFallDistanceMultiplier = 1;
    public static float otherDirectionsFallDistanceMultiplier = 1;

    public static double transitionAnimationRotationSpeed = 1.5;
    public static double transitionAnimationRotationLength;
    public static double transitionAnimationRotationEnd;

    public static void syncConfig() {
        transitionAnimationRotationLength = GravityDirectionCapability.DEFAULT_TIMEOUT / transitionAnimationRotationSpeed;
        transitionAnimationRotationEnd = GravityDirectionCapability.DEFAULT_TIMEOUT - transitionAnimationRotationLength;
    }
}

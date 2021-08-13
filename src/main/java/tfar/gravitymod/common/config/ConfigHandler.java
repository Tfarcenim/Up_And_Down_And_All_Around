package tfar.gravitymod.common.config;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.gravitymod.GravityMod;
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

    public static void initialConfigLoad(FMLPreInitializationEvent event) {
        syncConfig();
    }

    public static void syncConfig() {
        transitionAnimationRotationLength = GravityDirectionCapability.DEFAULT_TIMEOUT / transitionAnimationRotationSpeed;
        transitionAnimationRotationEnd = GravityDirectionCapability.DEFAULT_TIMEOUT - transitionAnimationRotationLength;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(GravityMod.MOD_ID)) {
            syncConfig();
        }
    }
}

package tfar.gravitymod.common.capabilities.gravitydirection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import tfar.gravitymod.GravityMod;
import tfar.gravitymod.api.util;
import tfar.gravitymod.common.config.ConfigHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by Mysteryem on 2016-08-14.
 */
public class GravityDirectionCapability {
    public static final boolean DEFAULT_GRAVITY = false;
    public static final int DEFAULT_TIMEOUT = 20;
    public static final int DEFAULT_REVERSE_TIMEOUT = 10;
    public static final int MIN_PRIORITY = Integer.MIN_VALUE;
    public static final String RESOURCE_NAME = "IGravityCapability";
    public static final ResourceLocation CAPABILITY_RESOURCE_LOCATION = new ResourceLocation(GravityMod.MOD_ID, RESOURCE_NAME);
    @CapabilityInject(IGravityDirectionCapability.class)
    public static Capability<IGravityDirectionCapability> GRAVITY_CAPABILITY_INSTANCE = null;

    public static boolean getGravityDirection(UUID playerName, World world) {
        return getGravityDirection(getGravityCapability(playerName, world));
    }

    public static boolean getGravityDirection(IGravityDirectionCapability capability) {
        return capability == null ? DEFAULT_GRAVITY : capability.getDirection();
    }

    @Nullable
    private static IGravityDirectionCapability getGravityCapability(@Nonnull UUID playerName, @Nonnull World world) {
        EntityPlayer playerByUsername = world.getPlayerEntityByUUID(playerName);
        return playerByUsername == null ? null : getGravityCapability(playerByUsername);
    }

    @Nullable
    public static IGravityDirectionCapability getGravityCapability(@Nonnull EntityPlayer player) {
        return player.getCapability(GRAVITY_CAPABILITY_INSTANCE, null);
    }

    public static boolean getGravityDirection(EntityPlayer player) {
        return getGravityDirection(getGravityCapability(player));
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IGravityDirectionCapability.class, new GravityDirectionCapabilityStorage(), GravityDirectionCapabilityImpl::new);
        MinecraftForge.EVENT_BUS.register(GravityDirectionCapabilityEventHandler.class);
    }

    public static void setGravityDirection(EntityPlayer player, boolean newDirection, boolean noTimeout) {
        final boolean clientSide = player.world.isRemote;

        // Get the player's capability
        IGravityDirectionCapability capability = getGravityCapability(player);
        // Get the current direction
        boolean oldDirection = capability.getDirection();
        // Get the current eye position (used when there's no safe position to put the player)
        Vec3d oldEyePos = player.getPositionVector().addVector(0, player.getEyeHeight(), 0);
        // Set the new direction
        setGravityDirection(capability, newDirection, noTimeout);
        // Apply any changes the new direction needs to make now that the direction has been changed
        util.postModifyPlayerOnGravityChange(newDirection,player, oldDirection, oldEyePos);

        if (oldDirection != newDirection) {
            player.fallDistance *= ConfigHandler.oppositeDirectionFallDistanceMultiplier;
        }
        // This CAN occur on the client (so I'm only logging it on the server side)
        // Client is connected to server,
        // Server tells client to create an EntityOtherPlayerMP,
        // New player's direction is DOWN by default
        // Client requests gravity direction of player from server
        // Client receives the other player's direction
        // If DOWN, we would get this message (or whatever the default direction happens to be)
        else if (!clientSide) {
            GravityMod.logInfo("Tried to set gravity direction of %s to %s, but it was already %s." +
                    " I'm pretty sure this shouldn't happen.", player.getName(), oldDirection, newDirection);
        }

        // This information is used in rendering, there's no reason to do it if we're a server
        if (clientSide) {
            Vec3d newEyePos = player.getPositionVector().addVector(0, player.getEyeHeight(), 0);

            Vec3d eyesDiff = newEyePos.subtract(oldEyePos);

            capability.setEyePosChangeVector(eyesDiff);
        }
    }

    private static void setGravityDirection(IGravityDirectionCapability capability,boolean direction, boolean noTimeout) {
        if (capability != null) {
            if (noTimeout) {
                capability.setDirectionNoTimeout(direction);
            }
            else {
                capability.setDirection(direction);
            }
        }
    }
}

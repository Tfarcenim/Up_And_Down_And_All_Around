package tfar.gravitymod.common.listeners;

import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import tfar.gravitymod.GravityMod;
import tfar.gravitymod.api.API;
import tfar.gravitymod.api.EnumGravityDirection;
import tfar.gravitymod.api.events.GravityTransitionEvent;
import tfar.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import tfar.gravitymod.common.capabilities.gravitydirection.IGravityDirectionCapability;
import tfar.gravitymod.common.packets.PacketHandler;
import tfar.gravitymod.common.packets.gravitychange.GravityChangeMessage;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by Mysteryem on 2016-08-04.
 */
public class GravityManagerCommon {

    public void handlePacket(GravityChangeMessage message, MessageContext context) {
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            PacketHandler.INSTANCE.sendTo(
                    new GravityChangeMessage(event.player.getGameProfile().getId(), GravityDirectionCapability.getGravityDirection(event.player), true),
                    (EntityPlayerMP)event.player
            );
        }
    }

    @SubscribeEvent
    public void onPlayerClone(Clone event) {
        EntityPlayer clone = event.getEntityPlayer();
        EntityPlayer original = event.getOriginal();
        if (clone instanceof EntityPlayerMP && original instanceof EntityPlayerMP) {
            if (event.isWasDeath()) {
                PacketHandler.INSTANCE.sendTo(
                        new GravityChangeMessage(clone.getGameProfile().getId(), GravityDirectionCapability.getGravityDirection(clone), true),
                        (EntityPlayerMP)clone
                );
            }
            else {
                EntityPlayerMP cloneMP = (EntityPlayerMP)clone;
                EntityPlayerMP originalMP = (EntityPlayerMP)original;

                EnumGravityDirection originalDirection = GravityDirectionCapability.getGravityDirection(original);

                // When a player entity enters the world, they are given a GravityCapability which defaults to DOWN
                MinecraftForge.EVENT_BUS.post(new GravityTransitionEvent.Server.Clone.Pre(originalDirection, EnumGravityDirection.DOWN, cloneMP, originalMP, event));
                GravityDirectionCapability.setGravityDirection(clone, originalDirection, false);
                MinecraftForge.EVENT_BUS.post(new GravityTransitionEvent.Server.Clone.Post(originalDirection, EnumGravityDirection.DOWN, cloneMP, originalMP, event));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            EnumGravityDirection gravityDirection = API.getGravityDirection(event.player);
            // Default gravity is down when players are created server/client side
            if (gravityDirection != EnumGravityDirection.DOWN) {
                PacketHandler.INSTANCE.sendTo(
                        new GravityChangeMessage(event.player.getGameProfile().getId(), gravityDirection, true),
                        (EntityPlayerMP)event.player
                );
                // When the client receives the gravity change packet, their position changes client side, we teleport them back via a packet
                ((EntityPlayerMP)event.player).connection.setPlayerLocation(event.player.posX, event.player.posY, event.player.posZ, event.player.rotationYaw, event.player.rotationPitch);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerUpdateTickStart(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            IGravityDirectionCapability gravityCapability = GravityDirectionCapability.getGravityCapability(event.player);
            if (event.side == Side.SERVER) {
                EntityPlayer player = event.player;

                EnumGravityDirection currentDirection = gravityCapability.getDirection();
                EnumGravityDirection pendingDirection = gravityCapability.getPendingDirection();

                if (currentDirection != pendingDirection) {
                    int reverseTimeOut = gravityCapability.getReverseTimeoutTicks();
                    // FIXME: Returns false if we set our gravity to downwards (the same as DEFAULT) and then unset it. (the last change was high priority,
                    // so it returns false)
                    int previousTickPriority = gravityCapability.getPreviousTickPriority();
                    if (previousTickPriority ==Integer.MIN_VALUE) {
                        reverseTimeOut = 0;
                    }
                    // Attempting to change to a different direction, we should decrement the reverseTimeout
                    if (reverseTimeOut > 0) {
                        // TODO: Replace with, 'if' higher tier than previous, change quickly
                        if (gravityCapability.getPendingPriority() > previousTickPriority) {
                            gravityCapability.setReverseTimeoutTicks(reverseTimeOut - 2);
                        }
                        else {
                            gravityCapability.setReverseTimeoutTicks(reverseTimeOut - 1);
                        }
                        // Let the previous tick's priority linger
                        gravityCapability.forceSetPendingDirection(currentDirection, previousTickPriority);
                    }
                    // reverseTimeout is already 0
                    else {
                        int timeOut = gravityCapability.getTimeoutTicks();
                        if (timeOut <= 0) {
                            // Will reset timeOut and reverseTimeOut
                            this.doGravityTransition(pendingDirection, (EntityPlayerMP)player, false);
                            // Immediately after transition to SOUTH, it is claiming we're attempting to switch from world gravity, not sure if this is an issue
                            // since the timeout blocks any new transition
                        }
                        else {
                            // We recently changed direction, so can't change just yet
//                            gravityCapability.forceSetPendingDirection(currentDirection, previousTickPriority);
                        }
                    }
                }
                else {
                    // Same direction as before, so reset reverseTimeout
                    // gravityCapability::tick will still be called, so timeOut will get decremented
                    gravityCapability.setReverseTimeoutTicks(GravityDirectionCapability.DEFAULT_REVERSE_TIMEOUT);
                }
                gravityCapability.tickServer();
            }
            //decrements timeOut on both client and server
            gravityCapability.tickCommon();
            //Hooks.makeMotionRelative(event.player);
        }
    }

    public void doGravityTransition(@Nonnull EnumGravityDirection newDirection, @Nonnull EntityPlayerMP player, boolean noTimeout) {
        EnumGravityDirection oldDirection = GravityDirectionCapability.getGravityDirection(player);
        if (oldDirection != newDirection) {
            GravityTransitionEvent.Server event = new GravityTransitionEvent.Server.Pre(newDirection, oldDirection, player);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                GravityDirectionCapability.setGravityDirection(event.player, event.newGravityDirection, noTimeout);
                player.connection.update();
                this.sendUpdatePacketToTrackingPlayers(event.player, event.newGravityDirection, noTimeout);
                MinecraftForge.EVENT_BUS.post(new GravityTransitionEvent.Server.Post(newDirection, oldDirection, player));
            }
        }
    }

    //Dedicated/Integrated Server only
    @SuppressWarnings("unchecked")
    private void sendUpdatePacketToTrackingPlayers(@Nonnull EntityPlayerMP player, @Nonnull EnumGravityDirection newGravityDirection, boolean noTimeout) {
        //DEBUG
        if (GravityMod.GENERAL_DEBUG) {
            GravityMod.logInfo("Sending gravity data for %s to players", player.getName());
        }

        // Don't know why it wouldn't be a WorldServer, but may as well check
        if (player.world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer)player.world;
            EntityTracker entityTracker = worldServer.getEntityTracker();
            // For some reason, the Forge guys made the method return a Set<? extends EntityPlayer> instead of Set<? extends EntityPlayerMP>
            Set<? extends EntityPlayerMP> trackingPlayers = (Set<? extends EntityPlayerMP>)entityTracker.getTrackingPlayers(player);
            for (EntityPlayerMP trackingPlayer : trackingPlayers) {
                PacketHandler.INSTANCE.sendTo(new GravityChangeMessage(player.getGameProfile().getId(), newGravityDirection, noTimeout), trackingPlayer);
            }
            // Players don't track themselves, so they need to be sent the packet too
            PacketHandler.INSTANCE.sendTo(new GravityChangeMessage(player.getGameProfile().getId(), newGravityDirection, noTimeout), player);
        }
    }

    public void prepareGravityTransition(@Nonnull EnumGravityDirection newDirection, @Nonnull EntityPlayerMP player, int priority) {
        IGravityDirectionCapability gravityCapability = GravityDirectionCapability.getGravityCapability(player);
        gravityCapability.setPendingDirection(newDirection, priority);
    }
}

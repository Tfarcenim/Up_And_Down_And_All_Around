package tfar.gravitymod.client.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tfar.gravitymod.GravityMod;
import tfar.gravitymod.api.events.GravityTransitionEvent;
import tfar.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import tfar.gravitymod.common.listeners.GravityManagerCommon;
import tfar.gravitymod.common.packets.PacketHandler;
import tfar.gravitymod.common.packets.gravitychange.EnumChangePacketType;
import tfar.gravitymod.common.packets.gravitychange.GravityChangeMessage;

import java.util.UUID;

/**
 * Used to control/record the gravity of all players
 * <p>
 * Created by Mysteryem on 2016-08-04.
 */
@SideOnly(Side.CLIENT)
public class GravityManagerClient extends GravityManagerCommon {

    @Override
    public void handlePacket(GravityChangeMessage message, MessageContext context) {
        if (message.getPacketType() == EnumChangePacketType.SINGLE) {
            if (GravityMod.GENERAL_DEBUG) {
                GravityMod.logInfo("Received gravity data for %s", message.getStringData());
            }
            this.setClientSideGravityDirection(message.getStringData(), message.getNewGravityDirection(), message.getNoTimeout());
        } else {
            super.handlePacket(message, context);
        }

    }

    private void setClientSideGravityDirection(UUID playerName, boolean direction, boolean noTimeout) {
        //TODO: Switch to UUIDs instead of names?
        EntityPlayer playerEntityByName = Minecraft.getMinecraft().world.getPlayerEntityByUUID(playerName);

        if (playerEntityByName instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer)playerEntityByName;

            // Get the current direction
            boolean oldDirection = GravityDirectionCapability.getGravityDirection(player);
            // Post Pre client event
            MinecraftForge.EVENT_BUS.post(new GravityTransitionEvent.Client.Pre(direction, oldDirection, player));
            // Set the new direction
            GravityDirectionCapability.setGravityDirection(player, direction, noTimeout);
            // Post post client event
            MinecraftForge.EVENT_BUS.post(new GravityTransitionEvent.Client.Post(direction, oldDirection, player));
        }
        else if (playerEntityByName != null) {
            GravityMod.logWarning("Retrieved a non-client player from the client world, what is going on?");
        }
        else if (GravityMod.GENERAL_DEBUG) {
            GravityMod.logInfo("Server has told us to set the gravity direction of a player we're currently not tracking. Ignoring the request.");
        }
    }

    //TODO: Try PlayerEvent.startTracking instead of EntityJoinWorldEvent
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) {
            Entity entity = event.getEntity();
            if (entity instanceof EntityOtherPlayerMP) {
                EntityPlayer player = (EntityPlayer)entity;
                if (GravityMod.GENERAL_DEBUG) {
                    GravityMod.logInfo("Requesting gravity data for %s", player.getName());
                }
                this.requestGravityDirectionFromServer(player.getGameProfile().getId());
            }
        }
    }

    private void requestGravityDirectionFromServer(UUID nameOfPlayerRequested) {
        PacketHandler.INSTANCE.sendToServer(new GravityChangeMessage(nameOfPlayerRequested));
    }
}

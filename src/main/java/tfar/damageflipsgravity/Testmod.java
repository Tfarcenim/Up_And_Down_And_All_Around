package tfar.damageflipsgravity;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tfar.gravitymod.api.API;
import tfar.gravitymod.api.EnumGravityDirection;

@Mod.EventBusSubscriber
public class Testmod {

    private static boolean flipped;

    @SubscribeEvent
    public static void hurt(LivingDamageEvent e) {
        if (e.getEntityLiving() instanceof EntityPlayerMP) {
            flipped = !flipped;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void tick(TickEvent.PlayerTickEvent e) {
        if (!e.player.world.isRemote && e.phase == TickEvent.Phase.START) {
            if (flipped) {
                API.setPlayerGravity(EnumGravityDirection.fromEnumFacing(EnumFacing.UP), (EntityPlayerMP) e.player, 0);
            } else {
            }
        }
    }
}

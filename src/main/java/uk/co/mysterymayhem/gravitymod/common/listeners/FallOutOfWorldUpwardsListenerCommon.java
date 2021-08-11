package uk.co.mysterymayhem.gravitymod.common.listeners;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import uk.co.mysterymayhem.gravitymod.common.config.ConfigHandler;

import java.util.Random;
import java.util.WeakHashMap;

//TODO: The treating of remote/non-remote players needs to be cleaned up, see FallOutOfWorldUpwardsListenerClient
/**
 * Created by Mysteryem on 11/03/2017.
 */
public class FallOutOfWorldUpwardsListenerCommon {

    // Slow damage (same as drown damage (reduced by protection/resistance))
    private static final DamageSource[] SOURCES_ASPHYXIATION =
            createDamageSources("mysttmtgravitymod_asphyxiation", 2, true, false, false);
    // Fast damage
    private static final DamageSource[] SOURCES_BLOOD_BOIL =
            createDamageSources("mysttmtgravitymod_bloodboil", 2, true, true, true);
    // Instant death
    // Has all the cool death messages
    private static final DamageSource[] SOURCES_OUTERSPACE =
            createDamageSources("mysttmtgravitymod_outerspace", 10, true, true, true);
    private static final Random SHARED_RANDOM = new Random();
    private final WeakHashMap<EntityPlayer, Integer> serverMap = new WeakHashMap<>();

    public static DamageSource getBloodBoilDamageSource() {
        return randomFromArray(SOURCES_BLOOD_BOIL);
    }

    private static <T> T randomFromArray(T[] array) {
        return array[SHARED_RANDOM.nextInt(array.length)];
    }

    private static DamageSource[] createDamageSources(String baseName, int count, boolean bypassArmour, boolean absolute, boolean hurtCreative) {
        DamageSource[] sources = new DamageSource[count];
        for (int i = 0; i < sources.length; i++) {
            DamageSource damageSource = new DamageSource(baseName + i);
            if (bypassArmour) {
                damageSource.setDamageBypassesArmor();
            }
            if (absolute) {
                damageSource.setDamageIsAbsolute();
            }
            if (hurtCreative) {
                damageSource.setDamageAllowedInCreativeMode();
            }
            sources[i] = damageSource;
        }
        return sources;
    }

    /**
     *
     * @param player whose worldObj is NOT remote
     */
    protected void processServerPlayer(EntityPlayer player) {
    }

    /**
     * Common is by default a server environment, the client listener overrides this method in order to differentiate between client and server players
     * @param player
     */
    protected void processSidedPlayer(EntityPlayer player) {
        this.processServerPlayer(player);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;

            this.processSidedPlayer(player);

            // Set and get special air
            this.setSpecialAir(player, 300);
        }
    }

    protected void incrementFreezeCounter(EntityPlayer player) {}
    protected void decrementFreezeCounter(EntityPlayer player) {}

    public void setSpecialAir(EntityPlayer player, int air) {
        this.serverMap.put(player, air);
    }

    public int getSpecialAir(EntityPlayer player) {
        return this.serverMap.get(player);
    }

//    private static int decreaseAir(int inAir) {
//        return i > 0 && this.rand.nextInt(i + 1) > 0 ? air : air - 1;
//    }
}

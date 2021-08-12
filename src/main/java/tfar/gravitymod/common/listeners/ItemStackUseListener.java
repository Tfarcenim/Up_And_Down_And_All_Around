package tfar.gravitymod.common.listeners;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.oredict.OreDictionary;
import tfar.gravitymod.common.modsupport.prepostmodifier.IPrePostModifier;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

//TODO: Build two separate hashcodes and sets of maps, one that's used in SSP and one that's used in SMP.
//TODO: If a server's hashcode does not match the client's, it replaces the SMP set.

/**
 * Listens to use of item stacks and modifies motion/rotation as defined in the config so items from other mods can be
 * made to work with different gravity directions
 * Created by Mysteryem on 2016-10-23.
 */
public class ItemStackUseListener {

    public static final TreeMap<Item, TIntObjectHashMap<IPrePostModifier<EntityPlayer>>> onItemRightClick_itemToPrePostModifier = new TreeMap<>(new ItemComparator());
    public static final TreeMap<Item, TIntObjectHashMap<IPrePostModifier<EntityPlayer>>> onItemUse_itemToPrePostModifier = new TreeMap<>(new ItemComparator());
    public static final TreeMap<Item, TIntObjectHashMap<IPrePostModifier<EntityPlayer>>> onPlayerStoppedUsing_itemToPrePostModifier = new TreeMap<>(new ItemComparator());

    private static int hashCode;

    public static void clearPrePostModifiers() {
        onItemRightClick_itemToPrePostModifier.clear();
        onItemUse_itemToPrePostModifier.clear();
        onPlayerStoppedUsing_itemToPrePostModifier.clear();
    }

    public static int getHashCode() {
        return hashCode;
    }

    public static void makeHash() {
        int hash = 1;
        hash = 31 * hash + getHashForMap(onItemUse_itemToPrePostModifier);
        hash = 31 * hash + getHashForMap(onItemRightClick_itemToPrePostModifier);
        hash = 31 * hash + getHashForMap(onPlayerStoppedUsing_itemToPrePostModifier);

        hashCode = hash;
    }

    private static int getHashForMap(TreeMap<Item, TIntObjectHashMap<IPrePostModifier<EntityPlayer>>> map) {
        int hash = 1;
        for (Item itemKey : map.keySet()) {
            hash = 31 * hash + (itemKey == null ? 0 : itemKey.getRegistryName().hashCode());
            TIntObjectHashMap<IPrePostModifier<EntityPlayer>> prePostMap = map.get(itemKey);
            if (prePostMap == null) {
                hash = 31 * hash;
            }
            else {
                int[] keysArray = prePostMap.keySet().toArray();
                Arrays.sort(keysArray);
                for (int nextKey : keysArray) {
                    hash = 31 * hash + nextKey;
                    IPrePostModifier<EntityPlayer> prePostModifier = prePostMap.get(nextKey);
                    if (prePostModifier == null) {
                        hash = 31 * hash;
                    } else {
                        hash = 31 * hash + prePostModifier.getUniqueID();
                    }
                }
            }
        }
        return hash;
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onRightClickBlockHighest(PlayerInteractEvent.RightClickBlock event) {
        //Hooks.makeMotionAbsolute(event.getEntityPlayer());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onRightClickBlockLowest(PlayerInteractEvent.RightClickBlock event) {
        //Hooks.popMotionStack(event.getEntityPlayer());
    }


    // Events that try to allow other mods using these events to modify the player's motion as if they have currently
    // have downwards gravity

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onRightClickItemHighest(PlayerInteractEvent.RightClickItem event) {
        //Hooks.makeMotionAbsolute(event.getEntityPlayer());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onRightClickItemLowest(PlayerInteractEvent.RightClickItem event) {
       // Hooks.popMotionStack(event.getEntityPlayer());
    }

    public enum EnumItemStackUseCompat {
        BLOCK("onUseOnBlock"), GENERAL("onUseGeneral"), STOPPED_USING("onStoppedUsing");

        public final String configName;

        EnumItemStackUseCompat(String configName) {
            this.configName = configName;
        }
    }

    private static class ItemComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getRegistryName().toString().compareTo(o2.getRegistryName().toString());
        }
    }

}

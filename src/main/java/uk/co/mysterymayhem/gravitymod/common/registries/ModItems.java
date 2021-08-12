package uk.co.mysterymayhem.gravitymod.common.registries;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.mysterymayhem.gravitymod.GravityMod;
import uk.co.mysterymayhem.gravitymod.common.items.armour.ItemGravityBoots;
import uk.co.mysterymayhem.gravitymod.common.items.armour.ItemGravityChestplate;
import uk.co.mysterymayhem.gravitymod.common.items.armour.ItemGravityHelmet;
import uk.co.mysterymayhem.gravitymod.common.items.armour.ItemGravityLeggings;
import uk.co.mysterymayhem.gravitymod.common.items.materials.*;
import uk.co.mysterymayhem.gravitymod.common.items.misc.ItemCreativeTabIcon;
import uk.co.mysterymayhem.gravitymod.common.items.tools.ItemGravityAnchor;
import uk.co.mysterymayhem.mystlib.setup.registries.AbstractItemRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Created by Mysteryem on 2016-08-08.
 */
public class ModItems extends AbstractItemRegistry<IGravityModItem<?>, ArrayList<IGravityModItem<?>>> {

    public static final CreativeTabs UP_AND_DOWN_CREATIVE_TAB;
    //'fake' tab for JEI compat
    public static final CreativeTabs FAKE_TAB_FOR_CONTROLLERS;
    @SuppressWarnings("WeakerAccess")
    static ItemCreativeTabIcon creativeTabIcon;
    static ItemGravityAnchor gravityAnchor;
    static ItemGravityIngot gravityIngot;
    static ItemGravityPearl gravityPearl;
    static ItemGravityDust gravityDust;
    static ItemSpacetimeAnomaly spacetimeAnomaly;
    static ItemGravityBoots gravityBoots;
    static ItemGravityChestplate gravityChestplate;
    static ItemGravityHelmet gravityHelmet;
    static ItemGravityLeggings gravityLeggings;
    static ItemRestabilisedGravityDust restabilisedGravityDust;
    static ItemStack liquidAntiMassBucket;
    static ItemGravityDustInducer spacetimeDistorter;
    static boolean STATIC_SETUP_ALLOWED = false;

    static {
        UP_AND_DOWN_CREATIVE_TAB = new CreativeTabs(GravityMod.MOD_ID) {
            @Override
            public ItemStack getTabIconItem() {
                // TODO: Replace with getting static ItemStack reference
                return new ItemStack(creativeTabIcon);
            }

            @SideOnly(Side.CLIENT)
            @Override
            public void displayAllRelevantItems(@Nonnull NonNullList<ItemStack> itemList) {
                itemList.add(StaticItems.LIQUID_ANTI_MASS_BUCKET);
                super.displayAllRelevantItems(itemList);
            }
        };

        // Get the current array of creative tabs
        CreativeTabs[] creativeTabArray = CreativeTabs.CREATIVE_TAB_ARRAY;

        // Add our 'fake' tab (this creates a new array with an increased size)
        FAKE_TAB_FOR_CONTROLLERS = new CreativeTabs(GravityMod.MOD_ID + "allcontrollers") {
            @Override
            public ItemStack getTabIconItem() {
                // TODO: Replace with getting static ItemStack reference
                return new ItemStack(creativeTabIcon);
            }
        };

        // We don't want our 'fake' tab in the array, so we restore the array of creative tabs to what it was before we
        // added the 'fake' tab
        CreativeTabs.CREATIVE_TAB_ARRAY = creativeTabArray;
    }

    public ModItems() {
        super(new ArrayList<>());
    }

    @Override
    protected void addToCollection(ArrayList<IGravityModItem<?>> modObjects) {
        modObjects.add(creativeTabIcon = new ItemCreativeTabIcon());
        modObjects.add(gravityAnchor = new ItemGravityAnchor());
        modObjects.add(gravityIngot = new ItemGravityIngot());
        modObjects.add(gravityPearl = new ItemGravityPearl());
        modObjects.add(gravityDust = new ItemGravityDust());
        modObjects.add(spacetimeAnomaly = new ItemSpacetimeAnomaly());
        modObjects.add(gravityBoots = new ItemGravityBoots());
        modObjects.add(gravityChestplate = new ItemGravityChestplate());
        modObjects.add(gravityHelmet = new ItemGravityHelmet());
        modObjects.add(gravityLeggings = new ItemGravityLeggings());
        modObjects.add(restabilisedGravityDust = new ItemRestabilisedGravityDust());
        modObjects.add(spacetimeDistorter = new ItemGravityDustInducer());
        STATIC_SETUP_ALLOWED = true;
    }

}

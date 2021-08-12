package uk.co.mysterymayhem.gravitymod.common.registries;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.mysterymayhem.gravitymod.GravityMod;
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
    static ItemGravityAnchor gravityAnchor;

    static ItemStack liquidAntiMassBucket;
    static boolean STATIC_SETUP_ALLOWED = false;

    static {
        UP_AND_DOWN_CREATIVE_TAB = new CreativeTabs(GravityMod.MOD_ID) {
            @Override
            public ItemStack getTabIconItem() {
                // TODO: Replace with getting static ItemStack reference
                return new ItemStack(Items.REDSTONE);
            }
        };

        // Get the current array of creative tabs
        CreativeTabs[] creativeTabArray = CreativeTabs.CREATIVE_TAB_ARRAY;

        // Add our 'fake' tab (this creates a new array with an increased size)
        FAKE_TAB_FOR_CONTROLLERS = new CreativeTabs(GravityMod.MOD_ID + "allcontrollers") {
            @Override
            public ItemStack getTabIconItem() {
                // TODO: Replace with getting static ItemStack reference
                return new ItemStack(Items.REDSTONE);
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
        modObjects.add(gravityAnchor = new ItemGravityAnchor());
        STATIC_SETUP_ALLOWED = true;
    }

}

package tfar.mystlib.setup.singletons;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import tfar.gravitymod.GravityMod;

/**
 * It may be frowned upon, but this is so I can have some item classes that extend Item and some that extend ItemArmor
 * whilst still having common default code for both of them.
 * <p>
 * Created by Mysteryem on 2016-11-05.
 */
public interface IModItem<T extends Item & IModItem<T>> extends IModObject, IModRegistryEntry<Item> {

    @Override
    default void register(IForgeRegistry<Item> registry) {
        T cast = this.getItem();
        cast.setUnlocalizedName(GravityMod.MOD_ID + "." + this.getModObjectName());
        cast.setRegistryName(new ResourceLocation(GravityMod.MOD_ID, this.getModObjectName()));
        CreativeTabs creativeTab = CreativeTabs.TOOLS;
        if (creativeTab != null) {
            cast.setCreativeTab(creativeTab);
        }
        registry.register(cast);
    }

    @SuppressWarnings("unchecked")
    default T getItem() {
        return (T)this;
    }
}

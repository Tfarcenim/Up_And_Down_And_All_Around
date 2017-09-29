package uk.co.mysterymayhem.mystlib.setup.singletons;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Mysteryem on 2017-01-28.
 */
public interface IModBlock<T extends Block & IModBlock<T>> extends IModObject {
    @Override
    default void preInit() {
        CreativeTabs creativeTab = this.getModCreativeTab();
        T block = this.getBlock();
        block.setUnlocalizedName(this.getModID() + '.' + block.getModObjectName());
        block.setRegistryName(this.getModID(), block.getModObjectName());
        if (creativeTab != null) {
            block.setCreativeTab(creativeTab);
        }
    }

    default void register(IForgeRegistry<Block> registry) {
        registry.register(getBlock());
    }

    @SuppressWarnings("unchecked")
    default T getBlock() {
        return (T)this;
    }
}

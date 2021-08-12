package uk.co.mysterymayhem.mystlib.setup.registries;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.mysterymayhem.mystlib.setup.singletons.IModObject;
import uk.co.mysterymayhem.mystlib.setup.singletons.IModRegistryEntry;

import java.util.Collection;

/**
 * Created by Mysteryem on 15/10/2017.
 */
public abstract class AbstractRegistrableModObjectRegistry<SINGLETON extends IModObject & IModRegistryEntry<?>, COLLECTION extends Collection<SINGLETON>>
        extends AbstractModObjectRegistry<SINGLETON, COLLECTION> {

    public AbstractRegistrableModObjectRegistry(COLLECTION modObjects) {
        super(modObjects);
    }

    @Override
    public void preInit() {
        // Don't want to create the IModObjects in preInit as registration events are yet to occur
    }

    @SideOnly(Side.CLIENT)
    public void onRegisterItemsClient(RegistryEvent.Register<Item> event) {
//        this.getCollection().forEach(obj -> obj.registerItemClient(event.getRegistry()));
    }

    public void onRegisterItems(RegistryEvent.Register<Item> event) {
//        this.getCollection().forEach(obj -> obj.registerItem(event.getRegistry()));
    }

}

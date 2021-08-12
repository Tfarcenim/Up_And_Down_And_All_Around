package uk.co.mysterymayhem.gravitymod;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import uk.co.mysterymayhem.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import uk.co.mysterymayhem.gravitymod.common.config.ConfigHandler;
import uk.co.mysterymayhem.gravitymod.common.events.BlockBreakListener;
import uk.co.mysterymayhem.gravitymod.common.listeners.GravityManagerCommon;
import uk.co.mysterymayhem.gravitymod.common.listeners.ItemStackUseListener;
import uk.co.mysterymayhem.gravitymod.common.packets.PacketHandler;
import uk.co.mysterymayhem.gravitymod.common.registries.ModItems;
import uk.co.mysterymayhem.mystlib.setup.registries.AbstractIFMLStagedRegistry;
import uk.co.mysterymayhem.mystlib.setup.registries.AbstractModObjectRegistry;
import uk.co.mysterymayhem.mystlib.setup.registries.AbstractRegistrableModObjectRegistry;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Created by Mysteryem on 2016-08-04.
 */
public class CommonProxy extends AbstractIFMLStagedRegistry<AbstractModObjectRegistry<?,?>, ArrayList<AbstractModObjectRegistry<?,?>>> {

    public GravityManagerCommon gravityManagerCommon;

    public CommonProxy() {
        super(new ArrayList<>());
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
        GravityDirectionCapability.registerCapability();
        this.registerGravityManager();
        PacketHandler.registerMessages();
        super.preInit();
    }

    public void registerGravityManager() {
        this.gravityManagerCommon = new GravityManagerCommon();
    }

    @Override
    public void init() {
        super.init();
        this.registerListeners();
    }

    public void registerListeners() {
        MinecraftForge.EVENT_BUS.register(this.getGravityManager());
        MinecraftForge.EVENT_BUS.register(ItemStackUseListener.class);
        MinecraftForge.EVENT_BUS.register(BlockBreakListener.class);
        MinecraftForge.EVENT_BUS.register(ConfigHandler.class);
//        MinecraftForge.EVENT_BUS.register(new DebugHelperListener());
    }

    public GravityManagerCommon getGravityManager() {
        return this.gravityManagerCommon;
    }

    @Override
    protected void addToCollection(ArrayList<AbstractModObjectRegistry<?,?>> modObjects) {
        modObjects.add(new ModItems());
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        this.doRegister(registry -> registry.onRegisterItems(event));
    }

    protected void doRegister(Consumer<AbstractRegistrableModObjectRegistry<?,?>> consumer) {
        this.getCollection().forEach(registry -> {
            if (registry instanceof AbstractRegistrableModObjectRegistry) {
                consumer.accept((AbstractRegistrableModObjectRegistry<?,?>)registry);
            }
        });
    }
}

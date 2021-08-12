package tfar.gravitymod;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import tfar.gravitymod.common.config.ConfigHandler;
import tfar.gravitymod.common.listeners.GravityManagerCommon;
import tfar.gravitymod.common.listeners.ItemStackUseListener;
import tfar.gravitymod.common.packets.PacketHandler;
import tfar.gravitymod.common.registries.ModItems;
import tfar.mystlib.setup.registries.AbstractIFMLStagedRegistry;
import tfar.mystlib.setup.registries.AbstractModObjectRegistry;
import tfar.mystlib.setup.registries.AbstractRegistrableModObjectRegistry;

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

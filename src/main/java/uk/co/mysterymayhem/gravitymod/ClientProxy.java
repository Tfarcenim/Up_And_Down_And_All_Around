package uk.co.mysterymayhem.gravitymod;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.mysterymayhem.gravitymod.client.listeners.EntityRenderListener;
import uk.co.mysterymayhem.gravitymod.client.listeners.GravityManagerClient;
import uk.co.mysterymayhem.gravitymod.client.listeners.PlayerCameraListener;

/**
 * Created by Mysteryem on 2016-08-04.
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void postInit() {
        super.postInit();
        this.postInitClient();
    }

    @Override
    public void preInit() {
        super.preInit();
        this.preInitClient();
    }

    @Override
    public void init() {
        super.init();
        this.initClient();
    }

    @Override
    public void registerGravityManager() {
        this.gravityManagerCommon = new GravityManagerClient();
    }

    @Override
    public void registerListeners() {
        super.registerListeners();
        MinecraftForge.EVENT_BUS.register(PlayerCameraListener.class);
        MinecraftForge.EVENT_BUS.register(EntityRenderListener.class);
    }

    @Override
    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        super.onRegisterItems(event);
        this.doRegister(col -> col.onRegisterItemsClient(event));
    }
}

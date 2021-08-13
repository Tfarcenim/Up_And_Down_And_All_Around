package tfar.gravitymod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import tfar.gravitymod.client.listeners.EntityRenderListener;
import tfar.gravitymod.client.listeners.PlayerCameraListener;
import tfar.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import tfar.gravitymod.common.config.ConfigHandler;
import tfar.gravitymod.common.packets.PacketHandler;

/**
 * Created by Mysteryem on 2016-08-04.
 */
@SuppressWarnings("WeakerAccess")
@Mod(
        modid = GravityMod.MOD_ID,
        version = GravityMod.VERSION,
        acceptedMinecraftVersions = GravityMod.MINECRAFT_VERSION,
        name = GravityMod.USER_FRIENDLY_NAME,
        acceptableRemoteVersions = GravityMod.ACCEPTABLE_VERSIONS
)
public class GravityMod {
    // Major changes, 1 was used for TMT, 2 is main development, 3 will likely be for initial full release and beyond
    private static final int MAJOR_VERSION = 2;
    // Indicates a breaking change in the mod, requiring both client and server updates
    private static final int MINOR_VERSION = 9;
    // Indicates a non-breaking change in the mod
    // Different patch numbers but the same major and minor version should be compatible with one another
    private static final int PATCH_NUMBER = 1;

    // M.m.p
    public static final String VERSION = "" + MAJOR_VERSION + '.' + MINOR_VERSION + '.' + PATCH_NUMBER;
    // [M.m,M.m+1), e.g. [2.5,2.6)
    public static final String ACCEPTABLE_VERSIONS = "[" + MAJOR_VERSION + '.' + MINOR_VERSION + ',' + MAJOR_VERSION + '.' + (MINOR_VERSION + 1) + ")";
    public static final String MOD_ID = "mysttmtgravitymod";
    public static final String MINECRAFT_VERSION = "1.12.2";
    public static final String USER_FRIENDLY_NAME = "Up And Down And All Around";

    private static final Logger logger = LogManager.getLogger("UpAndDownAndAllAround", StringFormatterMessageFactory.INSTANCE);

    public static final boolean GENERAL_DEBUG = false;

    @Mod.Instance(GravityMod.MOD_ID)
    public static GravityMod INSTANCE;

    @SidedProxy(clientSide = "tfar.gravitymod.ClientProxy", serverSide = "tfar.gravitymod.CommonProxy")
    public static CommonProxy proxy;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    public static void logWarning(String formattableString, Object... objects) {
        logger.warn(formattableString, objects);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Register listeners, blocks etc.

        MinecraftForge.EVENT_BUS.register(this);
        GravityDirectionCapability.registerCapability();
        proxy.registerGravityManager();
        PacketHandler.registerMessages();

        MinecraftForge.EVENT_BUS.register(proxy.getGravityManager());
        MinecraftForge.EVENT_BUS.register(ConfigHandler.class);
        MinecraftForge.EVENT_BUS.register(PlayerCameraListener.class);
        MinecraftForge.EVENT_BUS.register(EntityRenderListener.class);
    }

    public static void logInfo(String formattableString, Object... objects) {
        logger.info(formattableString, objects);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //TODO: config stuff instead of hardcoding for just the Botania rod (or just for the air sigil
        ConfigHandler.initialConfigLoad(event);
    }

}

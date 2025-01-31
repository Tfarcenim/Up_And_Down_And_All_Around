package tfar.gravitymod.common.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.gravitymod.GravityMod;
import tfar.gravitymod.common.capabilities.gravitydirection.GravityDirectionCapability;
import tfar.gravitymod.common.listeners.ItemStackUseListener;
import tfar.gravitymod.common.listeners.ItemStackUseListener.EnumItemStackUseCompat;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Mysteryem on 07/03/2017.
 */
public class ConfigHandler {

    public static final String CATEGORY_MOD_COMPAT = newCategory("modcompat");
    public static String[] modCompatUseOnBlock;
    public static String[] modCompatUseGeneral;
    public static String[] modCompatOnStoppedUsing;
    //<mod id>:<item name>[:damage][:damage][...],<compatibility modifier>:[compatibility modifier]
    public static final Pattern modCompatPattern = Pattern.compile("[^:]+:[^:]+(:[\\d]+)*,[a-zA-Z]+(:[a-zA-Z]+)?");

    public static final String CATEGORY_GRAVITY = newCategory("gravity");
    public static float oppositeDirectionFallDistanceMultiplier;
    public static float otherDirectionsFallDistanceMultiplier;
    public static int numWeakGravityEnablersRequiredForWeakGravity;
    public static int numNormalGravityEnablersRequiredForNormalGravity;
    public static int numNormalEnablersWeakEnablersCountsAs;

    public static final String CATEGORY_CLIENT = newCategory("client");
    public static double transitionAnimationRotationSpeed;
    public static double transitionAnimationRotationLength;
    public static double transitionAnimationRotationEnd;

    public static Configuration config;
    private static List<String> propertyOrder;
    private static String category;
    private static Property prop;
    private static Map<String, Set<String>> configNameToPropertyKeySet;

    public static void initialConfigLoad(FMLPreInitializationEvent event) {
        File file = event.getModConfigurationDirectory().toPath().resolve("UpAndDownAndAllAround.cfg").toFile();
        config = new Configuration(file);
        syncConfig(true);
    }

    public static void syncConfig(boolean load) {
        propertyOrder = new ArrayList<>();
        configNameToPropertyKeySet = new HashMap<>();

        if (load) {
            GravityMod.logInfo("Loading config");
            config.load();
        }
        else {
            GravityMod.logInfo("Reloading config");
        }

        nextCategory(CATEGORY_MOD_COMPAT);

        prop = config.get(category, EnumItemStackUseCompat.BLOCK.configName, new String[0], "Adding an item to this list will apply the " +
                "compatibility " +
                "when the item is right clicked " +
                "on a block. Specifically when the item's \"onItemUse\" method is called from within ItemStack::onItemUse.", modCompatPattern);
        modCompatUseOnBlock = process().getStringList();

        prop = config.get(category, EnumItemStackUseCompat.GENERAL.configName, new String[]{"tconstruct:rapier,relativeMotionAll:relativeRotation"}, "Adding an " +
                "item to this list will " +
                "apply the compatibility when the item is right clicked on air, or on a block that doesn't open a chest/machine GUI or otherwise change " +
                "state, such as right clicking on a vanilla lever. Specifically when the item's \"onItemRightClick\" method is called from within " +
                "ItemStack::useItemRightClick.\n" +

                "The Tinkers' Construct Rapier makes the player jump upwards and backwards slightly when right clicked. We want the player to jump upwards " +
                "relative to their view of the world, so we will need at least use relative Y motion for this compatibility. We also want the player to jump " +
                "backwards relative to their view of the world. The rapier uses the player's pitch and yaw rotations to calculate the backwards direction and" +
                " modifies X and Z motion accordingly, so we need them to be relative too.", modCompatPattern);
        modCompatUseGeneral = process().getStringList();

        prop = config.get(category, EnumItemStackUseCompat.STOPPED_USING.configName, new String[]{"tconstruct:longsword,relativeMotionAll:relativeRotation"}, "Adding an " +
                        "item to this list " +
                        "will apply the compatibility when a player stops 'using' an item. For bows, this is when you release right click to fire an arrow.\n",
                modCompatPattern);
        modCompatOnStoppedUsing = process().getStringList();

        config.setCategoryComment(category,
                "Up And Down changes how player motion is used to move the player. Some items may move the player in unexpected ways when used. If that's the" +
                        " case, try adding them to one of the below lists. The format is (text in square brackets is optional) \"<mod id>:<item " +
                        "name>[:damage][:damage][...],<compatibility modifier>:[compatibility modifier]\"\n" +

                        "Where a compatibility modifier is case insensitive and one of: relativeRotation, relativeMotionAll, relativeMotionX, " +
                        "relativeMotionY, relativeMotionZ, absoluteMotionX, absoluteMotionY or absoluteMotionZ\n" +

                        "These modify the player's rotation and/or motion before the item is used (and then puts the rotation/motion back to normal, taking " +
                        "into account any changes to motion, but NOT taking into account any changes to rotation).\n" +

                        "You may use a max of one type of modifier per entry (up to 1 motion modifier and up to 1 rotation modifier).\n" +

                        "The default state when using items is that both rotation and motion are absolute (as if the player currently has downwards gravity)" +
                        ".\n" +

                        "Using relativeMotionX implies that Z and Y motion will be absolute.\n" +

                        "Likewise, using absoluteMotionY implies that X and Z motion will be relative.\n" +

                        "So if you wanted both X and Y motion to be relative, you would use absoluteMotionZ.\n" +

                        "Examples:\n" +
                        "'minecraft:stick:0:4,relativeMotionX' - Adds a relative X motion modifier to vanilla sticks with damage values 0 and 4.\n" +
                        "'tconstruct:longsword,relativeMotionAll:relativeRotation' - Adds a relative X, Y and Z motion modifier combined with a relative " +
                        "rotation modifier to all damage values of Tinkers' Construct Longswords.");

        nextCategory(CATEGORY_GRAVITY);

        prop = config.get(category, "oppositeDirectionFallDistanceMultiplier", 0d, "When a player's gravity direction changes to the opposite direction, " +
                "their accrued fall distance will be multiplied by this value.", 0d, 1d);
        oppositeDirectionFallDistanceMultiplier = (float)process().getDouble();

        prop = config.get(category, "otherDirectionFallDistanceMultiplier", 0.5d, "When a player's gravity direction changes to a direction other than the " +
                "opposite direction, their accrued fall distance will be multiplied by this value.", 0d, 1d);
        otherDirectionsFallDistanceMultiplier = (float)process().getDouble();

        prop = config.get(category, "numWeakGravityEnablersRequiredForWeakGravity", 1, "Number of weak gravity enablers (armor + baubles if installed) that " +
                "must be worn for a player to be affected by weak gravity", 0, Integer.MAX_VALUE);
        numWeakGravityEnablersRequiredForWeakGravity = process().getInt();

        prop = config.get(category, "numNormalGravityEnablersRequiredForNormalGravity", 4, "Number of normal gravity enablers (armor + baubles if installed) " +
                "that must be worn for a player to be affected by normal strength gravity", 0, Integer.MAX_VALUE);
        numNormalGravityEnablersRequiredForNormalGravity = process().getInt();

        prop = config.get(category, "numNormalEnablersWeakEnablersCountAs", 4, "Weak gravity enablers count as this many normal gravity enablers.\n" +
                "This makes more sense thematically to be greater than 1, but '1' or '0' will still work.");
        numNormalEnablersWeakEnablersCountsAs = process().getInt();

        nextCategory(CATEGORY_CLIENT, false);
        prop = config.get(category, "rotationAnimationSpeed", 1.5d, "Animation speed for gravity transition." +
                "Takes 1 second divided by this config value.", 1d, 1000d);
        transitionAnimationRotationSpeed = process().getDouble();
        transitionAnimationRotationLength = GravityDirectionCapability.DEFAULT_TIMEOUT / transitionAnimationRotationSpeed;
        transitionAnimationRotationEnd = GravityDirectionCapability.DEFAULT_TIMEOUT - transitionAnimationRotationLength;

        // Needed to set the ordering of the current category
        nextCategory(null);

        // Sets static fields back to null and deletes categories and properties that we weren't expecting (this helps clean up old configs)
        cleanup();

        if (config.hasChanged()) {
            config.save();
            GravityMod.logInfo("Saved config");
        }

        GravityMod.logInfo("Loaded config");
    }

    public static void processLateConfig() {
        ItemStackUseListener.clearPrePostModifiers();
        ItemStackUseListener.makeHash();
        if (GravityMod.GENERAL_DEBUG) {
            GravityMod.logInfo("HashCode: " + ItemStackUseListener.getHashCode());
        }
    }

    private static void cleanup() {
        category = null;
        prop = null;
        Set<String> categoryNames = config.getCategoryNames();
        for (String categoryName : categoryNames) {
            Set<String> knownPropertyKeys = configNameToPropertyKeySet.get(categoryName);
            if (knownPropertyKeys == null) {
                config.removeCategory(config.getCategory(categoryName));
            }
            else {
                ConfigCategory category = config.getCategory(categoryName);
                Set<String> categoryPropertyKeys = category.keySet();
                categoryPropertyKeys.removeIf(s -> !knownPropertyKeys.contains(s));
            }
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(GravityMod.MOD_ID)) {
            syncConfig(false);
            processLateConfig();
        }
    }

    private static void nextCategory(String categoryName) {
        nextCategory(categoryName, true);
    }

    private static void nextCategory(String categoryName, boolean requiresWorldRestart) {
        if (category != null) {
            setCategoryOrder();
        }
        category = categoryName;
        if (category != null) {

            config.setCategoryRequiresWorldRestart(category, requiresWorldRestart);
        }
    }

    private static Property process(Property prop) {
        setLangKey(prop);
        order(prop);
        Set<String> propKeys = configNameToPropertyKeySet.computeIfAbsent(category, k -> new HashSet<>());
        propKeys.add(prop.getName());
        return prop;
    }

    private static void setCategoryOrder() {
        config.setCategoryPropertyOrder(category, propertyOrder);
        propertyOrder = new ArrayList<>();
    }

    private static void setLangKey(Property prop) {
        setLangKey(prop, category);
    }

    private static void order(Property prop) {
        propertyOrder.add(prop.getName());
    }

    private static void setLangKey(Property prop, String category) {
        StringBuilder builder = new StringBuilder();
        builder.append("config.").append(GravityMod.MOD_ID).append('.');
        // Removes "category_" from the front and then replaces '_' with '.'
        category = category.substring(category.indexOf('_') + 1).replace('_', '.');
        builder.append(category);

        String propName = prop.getName();
//        propName = propName.substring(propName.lastIndexOf('_') + 1);
        builder.append('.').append(propName);

        prop.setLanguageKey(builder.toString());
    }

    private static String newCategory(String baseName) {
        return "category_" + baseName.replace(' ', '.').toLowerCase(Locale.ENGLISH);
    }

    private static Property process() {
        return process(prop);
    }
}

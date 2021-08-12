package tfar.mystlib.setup.singletons;

import net.minecraft.creativetab.CreativeTabs;
import tfar.mystlib.setup.IFMLStaged;

/**
 * Created by Mysteryem on 2016-12-07.
 */
public interface IModObject extends IFMLStaged {

    /**
     * Get a name of this IModObject, often you would ensure this is unique, but it depends on the circumstances
     *
     * @return
     */
    String getModObjectName();
//    default String getName() {
//        return this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
//    }

    /**
     * Get the String ID of your mod
     *
     * @return
     */
    String getModID();

    /**
     * Get your mod's instance. Use @Mod.Instance(&lt;modID&gt;) on a field and return that field in this method.
     *
     * @return
     */
    Object getModInstance();
}

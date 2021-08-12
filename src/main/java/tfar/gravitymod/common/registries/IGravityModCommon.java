package tfar.gravitymod.common.registries;

import net.minecraft.creativetab.CreativeTabs;
import tfar.gravitymod.GravityMod;
import tfar.mystlib.setup.singletons.IModObject;

/**
 * Created by Mysteryem on 2017-01-03.
 */
public interface IGravityModCommon extends IModObject {
    @Override
    default String getModID() {
        return GravityMod.MOD_ID;
    }

    @Override
    default Object getModInstance() {
        return GravityMod.INSTANCE;
    }
}

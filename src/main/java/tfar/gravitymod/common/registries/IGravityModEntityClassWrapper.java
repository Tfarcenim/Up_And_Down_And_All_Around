package tfar.gravitymod.common.registries;

import net.minecraft.entity.Entity;
import tfar.gravitymod.GravityMod;
import tfar.mystlib.setup.singletons.IModEntityClassWrapper;

/**
 * Created by Mysteryem on 2016-12-24.
 */
public interface IGravityModEntityClassWrapper<T extends Entity> extends IModEntityClassWrapper<T>, IGravityModCommon {
    @Override
    default int getUniqueID() {
        return GravityMod.getNextEntityID();
    }
}

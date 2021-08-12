package tfar.gravitymod.common.registries;

import net.minecraft.item.Item;
import tfar.mystlib.setup.singletons.IModItem;
import tfar.mystlib.setup.singletons.IModObject;

/**
 * Created by Mysteryem on 2016-12-24.
 */
public interface IGravityModItem<T extends Item & IGravityModItem<T>> extends IModItem<T>, IModObject {
}

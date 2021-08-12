package tfar.gravitymod.common.registries;

import tfar.gravitymod.common.items.tools.ItemGravityAnchor;
import tfar.mystlib.setup.registries.AbstractItemRegistry;

import java.util.ArrayList;

/**
 * Created by Mysteryem on 2016-08-08.
 */
public class ModItems extends AbstractItemRegistry<IGravityModItem<?>, ArrayList<IGravityModItem<?>>> {

    @SuppressWarnings("WeakerAccess")
    static ItemGravityAnchor gravityAnchor;

    static boolean STATIC_SETUP_ALLOWED = false;

    public ModItems() {
        super(new ArrayList<>());
    }

    @Override
    protected void addToCollection(ArrayList<IGravityModItem<?>> modObjects) {
        modObjects.add(gravityAnchor = new ItemGravityAnchor());
        STATIC_SETUP_ALLOWED = true;
    }

}

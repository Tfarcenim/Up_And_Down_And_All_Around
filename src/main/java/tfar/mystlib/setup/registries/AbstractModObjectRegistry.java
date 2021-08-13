package tfar.mystlib.setup.registries;

import tfar.mystlib.setup.IFMLStaged;

import java.util.Collection;

/**
 * Created by Mysteryem on 2016-12-14.
 */
public abstract class AbstractModObjectRegistry<SINGLETON extends IFMLStaged, COLLECTION extends Collection<SINGLETON>> extends
        AbstractIFMLStagedRegistry<SINGLETON, COLLECTION> {
    public AbstractModObjectRegistry(COLLECTION modObjects) {
        super(modObjects);
    }
}

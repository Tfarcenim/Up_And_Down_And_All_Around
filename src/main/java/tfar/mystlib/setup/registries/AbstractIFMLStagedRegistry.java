package tfar.mystlib.setup.registries;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tfar.mystlib.setup.IFMLStaged;

import java.util.Collection;

/**
 * Created by Mysteryem on 2016-12-25.
 */
public abstract class AbstractIFMLStagedRegistry<SINGLETON extends IFMLStaged, COLLECTION extends Collection<SINGLETON>> implements IFMLStaged {
    private final COLLECTION modObjects;

    public AbstractIFMLStagedRegistry(COLLECTION modObjects) {
        this.modObjects = modObjects;
    }

    @Override
    public void preInit() {
        this.addToCollection(this.getCollection());
        this.getCollection().forEach(IFMLStaged::preInit);
    }

    protected abstract void addToCollection(COLLECTION modObjects);

    public COLLECTION getCollection() {
        return this.modObjects;
    }

    @Override
    public void init() {
        this.getCollection().forEach(IFMLStaged::init);
    }

    @Override
    public void postInit() {
        this.getCollection().forEach(IFMLStaged::postInit);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initClient() {
        this.getCollection().forEach(IFMLStaged::initClient);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void preInitClient() {
        this.getCollection().forEach(IFMLStaged::preInitClient);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void postInitClient() {
        this.getCollection().forEach(IFMLStaged::postInitClient);
    }
}

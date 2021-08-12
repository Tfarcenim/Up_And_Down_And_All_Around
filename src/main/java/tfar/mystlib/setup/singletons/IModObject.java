package tfar.mystlib.setup.singletons;

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
}

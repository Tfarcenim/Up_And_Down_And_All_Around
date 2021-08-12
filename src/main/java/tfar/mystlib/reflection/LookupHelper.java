package tfar.mystlib.reflection;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Used during development or to access methods that could be overridden, without altering their access with an AT.
 * Created by Mysteryem on 2016-09-05.
 */
public final class LookupHelper {
    private static final MethodHandles.Lookup TRUSTED_LOOKUP;

    static {
        try {
            Field IMPL_LOOKUP = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            IMPL_LOOKUP.setAccessible(true);
            TRUSTED_LOOKUP = (MethodHandles.Lookup)IMPL_LOOKUP.get(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    private LookupHelper() {
        try {
            Constructor<MethodHandles.Lookup> declaredConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
            declaredConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a the Lookup object that is trusted and can access
     *
     * @return
     */
    public static MethodHandles.Lookup getTrustedLookup() {
        return TRUSTED_LOOKUP;
    }
}

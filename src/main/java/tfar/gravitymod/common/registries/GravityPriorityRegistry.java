package tfar.gravitymod.common.registries;

/**
 * Created by Mysteryem on 2016-11-08.
 */
public class GravityPriorityRegistry {
    private static final int NUM_PRIORITIES = 3;
    private static final int NUM_EXTRA_VALUES = 5;

    private static final long MAX_UNSIGNED_INT = (long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE;
    private static final long MAX_SPACE_FOR_PRIORITIES = MAX_UNSIGNED_INT - NUM_EXTRA_VALUES - NUM_PRIORITIES; // Each priority uses an extra 'value'
    // I'm still not sure how the maths is wrong on this, but it'll do for now
    private static final int PRIORITY_SIZE = (int)(MAX_SPACE_FOR_PRIORITIES / NUM_PRIORITIES);
//    private static final int REMAINDER = (int)(MAX_SPACE_FOR_PRIORITIES % NUM_PRIORITIES);

//    private static final int PRIORITY_SIZE =
//            (int)(    //(max unsigned int - extra values) / number of priorities
//                    (((long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE) - NUM_EXTRA_VALUES)
//                            / NUM_PRIORITIES
//            );

    // Extra value
    public static final int WORLD_DEFAULT = Integer.MIN_VALUE;

    public static final int WEAK_MIN = WORLD_DEFAULT + 1;
    public static final int WEAK_MAX = WEAK_MIN + PRIORITY_SIZE;
    // Extra value
    public static final int WEAK_GRAVITY_CONTROLLER = WEAK_MAX + 1;

    public static final int NORMAL_MIN = WEAK_GRAVITY_CONTROLLER + 1;
    public static final int NORMAL_MAX = NORMAL_MIN + PRIORITY_SIZE;
    // Extra value
    public static final int PERSONAL_GRAVITY_CONTROLLER = NORMAL_MAX + 1;
    // Extra value
    public static final int GRAVITY_ANCHOR = PERSONAL_GRAVITY_CONTROLLER + 1;

}

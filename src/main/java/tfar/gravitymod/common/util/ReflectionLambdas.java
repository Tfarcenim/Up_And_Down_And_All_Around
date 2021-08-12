package tfar.gravitymod.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.function.DoubleSupplier;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static tfar.mystlib.reflection.lambda.LambdaBuilder.*;

/**
 * Created by Mysteryem on 2016-12-26.
 */
@SuppressWarnings("unchecked")
public class ReflectionLambdas {
    // Replacement for these Access Transformer entries
    //    public net.minecraft.entity.Entity field_70150_b #nextStepDistance
    //    public net.minecraft.util.math.MathHelper field_181163_d #FRAC_BIAS
    //    public net.minecraft.util.math.MathHelper field_181164_e #ASINE_TAB
    //    public net.minecraft.entity.item.EntityItem field_70291_e #health
    //    public net.minecraft.entity.item.EntityItem field_70292_b #age

    public static final ToIntFunction<Entity> get_Entity$nextStepDistance
            = buildInstanceFieldGetter(ToIntFunction.class, Entity.class, int.class, "field_70150_b", "nextStepDistance");
    public static final ObjIntConsumer<Entity> set_Entity$nextStepDistance
            = buildInstanceFieldSetter(ObjIntConsumer.class, Entity.class, int.class, "field_70150_b", "nextStepDistance");

    public static final DoubleSupplier get_MathHelper$FRAC_BIAS
            = buildStaticFieldGetter(DoubleSupplier.class, MathHelper.class, double.class, "field_181163_d", "FRAC_BIAS");

    public static final Supplier<double[]> get_MathHelper$ASINE_TAB
            = buildStaticFieldGetter(Supplier.class, MathHelper.class, double[].class, "field_181164_e", "ASINE_TAB");

}

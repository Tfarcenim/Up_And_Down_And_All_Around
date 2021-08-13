package tfar.gravitymod.common.capabilities.gravitydirection;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

/**
 * Created by Mysteryem on 2016-10-10.
 */
public interface IGravityDirectionCapability {
    boolean getDirection();

    void setDirection(boolean up);

    @Nonnull Vec3d getEyePosChangeVector();

    void setEyePosChangeVector(@Nonnull Vec3d vec3d);

    boolean getPendingDirection();

    boolean getPrevDirection();

    int getTimeoutTicks();

    int getReverseTimeoutTicks();

    void setReverseTimeoutTicks(int newReverseTimeout);

    double getTransitionAngle();

    void setTransitionAngle(double angle);

    boolean hasTransitionAngle();

    void setDirectionNoTimeout(boolean up);

    void setPendingDirection(boolean up, int priority);
    void forceSetPendingDirection(boolean up, int priority);

    int getPendingPriority();

    int getPreviousTickPriority();

    default void tickCommon() {}

    default void tickServer() {}

}

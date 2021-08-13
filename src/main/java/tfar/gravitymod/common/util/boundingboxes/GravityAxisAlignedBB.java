package tfar.gravitymod.common.util.boundingboxes;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import tfar.gravitymod.api.util;
import tfar.gravitymod.common.capabilities.gravitydirection.IGravityDirectionCapability;

/**
 * Created by Mysteryem on 2016-09-04.
 */
public class GravityAxisAlignedBB extends AxisAlignedBB {
    private final IGravityDirectionCapability gravityCapability;

    public GravityAxisAlignedBB(IGravityDirectionCapability gravityCapability, AxisAlignedBB axisAlignedBB) {
        this(gravityCapability, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    }

    public GravityAxisAlignedBB(IGravityDirectionCapability gravityCapability, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
        this.gravityCapability = gravityCapability;
    }

    public GravityAxisAlignedBB(GravityAxisAlignedBB other, AxisAlignedBB axisAlignedBB) {
        this(other.gravityCapability, axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    }

    public static double getRelativeBottom(AxisAlignedBB bb) {
        if (bb instanceof GravityAxisAlignedBB) {
            return ((GravityAxisAlignedBB) bb).getRelativeBottom();
        }
        return bb.minY;
    }

    public double getRelativeBottom() {
        boolean up = this.getDirection();
        if (up) {
            return -this.maxY;
        } else {
            return this.minY;
        }
    }

    public boolean getDirection() {
        return this.gravityCapability.getDirection();
    }

    @Override
    public GravityAxisAlignedBB expand(double x, double y, double z) {
        double[] d = util.adjustXYZValues(this.gravityCapability.getDirection(), x, y, z);
        return new GravityAxisAlignedBB(this, super.expand(d[0], d[1], d[2]));
    }

    @Override
    public GravityAxisAlignedBB grow(double x, double y, double z) {
        // Sign of the arguments is important, we only want to get the new directions the values correspond to
        double[] d = util.adjustXYZValuesMaintainSigns(this.getDirection(),x, y, z);
        return new GravityAxisAlignedBB(this, super.grow(d[0], d[1], d[2]));
    }

    @Override
    public GravityAxisAlignedBB offset(BlockPos pos) {
        return new GravityAxisAlignedBB(this, this.offset(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public GravityAxisAlignedBB offset(double x, double y, double z) {
        double[] d = util.adjustXYZValues(this.gravityCapability.getDirection(),x, y, z);
        return new GravityAxisAlignedBB(this, super.offset(d[0], d[1], d[2]));
    }

    // Do I need to gravity adjust these?
    @Override
    public double calculateXOffset(AxisAlignedBB other, double offsetX) {
        return this.offsetFromArray(util.adjustXYZValues(this.getDirection(),1, 0, 0), other, offsetX);
    }

    private double offsetFromArray(double[] adjusted, AxisAlignedBB other, double offset) {
        switch ((int) adjusted[0]) {
            case -1:
                return -super.calculateXOffset(other, -offset);
            case 0:
                switch ((int) adjusted[1]) {
                    case -1:
                        return -super.calculateYOffset(other, -offset);
                    case 0:
                        //case 0:
                        if ((int) adjusted[2] == -1) {
                            return -super.calculateZOffset(other, -offset);
                        }
                        return super.calculateZOffset(other, offset);
                    default:
                        return super.calculateYOffset(other, offset);
                }
            default:
                return super.calculateXOffset(other, offset);
        }
    }

    // Do I need to gravity adjust these?
    @Override
    public double calculateYOffset(AxisAlignedBB other, double offsetY) {
        return this.offsetFromArray(util.adjustXYZValues(this.getDirection(),0, 1, 0), other, offsetY);
    }

    // Do I need to gravity adjust these?
    @Override
    public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
        return this.offsetFromArray(util.adjustXYZValues(this.getDirection(),0, 0, 1), other, offsetZ);
    }

    public IGravityDirectionCapability getCapability() {
        return this.gravityCapability;
    }

    public Vec3d getOrigin() {

        boolean up = this.gravityCapability.getDirection();

        if (up) {
            return new Vec3d(this.getCentreX(), this.maxY, this.getCentreZ());
        } else {
              return new Vec3d(this.getCentreX(), this.minY, this.getCentreZ());
        }
    }

    private double getCentreX() {
        return (this.minX + this.maxX) / 2d;
    }

    private double getCentreZ() {
        return (this.minZ + this.maxZ) / 2d;
    }

}

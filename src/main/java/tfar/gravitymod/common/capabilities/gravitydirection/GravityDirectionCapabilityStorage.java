package tfar.gravitymod.common.capabilities.gravitydirection;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Created by Mysteryem on 2016-10-10.
 */
class GravityDirectionCapabilityStorage implements Capability.IStorage<IGravityDirectionCapability> {

    @Override
    public NBTBase writeNBT(Capability<IGravityDirectionCapability> capability, IGravityDirectionCapability instance, EnumFacing side) {
        return new NBTTagInt(instance.getDirection() ? 1 : 0);
    }

    @Override
    public void readNBT(Capability<IGravityDirectionCapability> capability, IGravityDirectionCapability instance, EnumFacing side, NBTBase nbt) {
        boolean direction = ((NBTPrimitive)nbt).getInt() == 1;
        instance.setDirection(direction);
        instance.setDirectionNoTimeout(direction);
        instance.setPendingDirection(direction, Integer.MIN_VALUE + 1);
    }
}

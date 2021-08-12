package tfar.gravitymod.asm.util;

import net.minecraft.item.ItemStack;

/**
 * Used in ASM, if this class is moved, ASM will need to be updated
 * Created by Mysteryem on 2016-10-23.
 */
public class ItemStackAndBoolean {
    public final boolean isRemote;
    public final ItemStack stack;

    public ItemStackAndBoolean(ItemStack stack, boolean isRemote) {
        this.stack = stack;
        this.isRemote = isRemote;
    }
}

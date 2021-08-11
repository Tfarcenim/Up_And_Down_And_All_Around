package uk.co.mysterymayhem.gravitymod.common.items.materials;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.mysterymayhem.gravitymod.common.registries.IGravityModItem;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Mysteryem on 2016-11-10.
 */
public class ItemGravityDust extends Item implements IGravityModItem<ItemGravityDust> {

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("mouseovertext.mysttmtgravitymod.gravitydust.line1"));
    }

    @Override
    public String getModObjectName() {
        return "gravitydust";
    }

}

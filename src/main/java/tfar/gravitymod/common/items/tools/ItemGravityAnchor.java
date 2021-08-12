package tfar.gravitymod.common.items.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import tfar.gravitymod.GravityMod;
import tfar.gravitymod.api.API;
import tfar.gravitymod.api.EnumGravityDirection;
import tfar.gravitymod.api.ITickOnMouseCursor;
import tfar.gravitymod.common.registries.GravityPriorityRegistry;
import tfar.gravitymod.common.registries.IGravityModItem;
import tfar.gravitymod.common.registries.ModItems;
import tfar.mystlib.util.KeyBindingUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mysteryem on 2016-11-03.
 */
public class ItemGravityAnchor extends Item implements ITickOnMouseCursor, IGravityModItem<ItemGravityAnchor> {

    // From EntityItem::onUpdate
    private static final double GRAVITY_DOWNWARDS_MOTION = 0.03999999910593033D;

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        KeyBinding keyBindSneak = Minecraft.getMinecraft().gameSettings.keyBindSneak;
        if (KeyBindingUtil.isKeyPressed(keyBindSneak)) {
//            tooltip.add("Affects gravity in inventory or on mouse cursor");
            tooltip.add(I18n.format("mouseovertext.mysttmtgravitymod.gravityanchor.sneak.line1"));
//            tooltip.add("Take care when crafting");
            tooltip.add(I18n.format("mouseovertext.mysttmtgravitymod.gravityanchor.sneak.line2"));
        }
        else {
            tooltip.add(I18n.format("mouseovertext.mysttmtgravitymod.gravityanchor.line1"));
            tooltip.add(keyBindSneak.getDisplayName() + I18n.format("mouseovertext.mysttmtgravitymod.presskeyfordetails"));
        }
    }

    @Override
    public String getModObjectName() {
        return "gravityanchor";
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return stack.isItemEnchanted() ? EnumRarity.RARE : GravityMod.RARITY_NORMAL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (getCreativeTab() == tab) {
            for (int damage = 0; damage < EnumGravityDirection.values().length; damage++) {
                items.add(new ItemStack(this, 1, damage));
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getItemDamage();
        return super.getUnlocalizedName() + "." + EnumGravityDirection.getSafeDirectionFromOrdinal(i).getName();
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof EntityPlayerMP) {
            int meta = stack.getItemDamage();
            API.setPlayerGravity(EnumGravityDirection.getSafeDirectionFromOrdinal(meta), (EntityPlayerMP)entityIn, GravityPriorityRegistry.GRAVITY_ANCHOR);
        }
    }

    @Override
    public void register(IForgeRegistry<Item> registry) {
        this.setHasSubtypes(true);
        IGravityModItem.super.register(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(IForgeRegistry<Item> registry) {
        // ItemFacing used to produce the ModelResourceLocations
        for (ItemFacing direction : ItemFacing.values()) {
            ModelBakery.registerItemVariants(this, new ModelResourceLocation(this.getRegistryName() + "_" + direction.name().toLowerCase(Locale.ENGLISH), "inventory"));
        }

        // Ordinal of EnumGravityDirection provides the damage value/metadata of the itemstack
        for (EnumGravityDirection gravityDirection : EnumGravityDirection.values()) {
            ModelLoader.setCustomModelResourceLocation(this, gravityDirection.ordinal(), new ModelResourceLocation(this.getRegistryName(), "inventory"));
        }
    }

    // If these are changed, the item jsons will need to be changed too!
    public enum ItemFacing {
        FORWARDS,
        BACKWARDS,
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

}

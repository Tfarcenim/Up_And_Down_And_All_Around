package uk.co.mysterymayhem.gravitymod.common.events;

import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import uk.co.mysterymayhem.gravitymod.common.items.materials.ItemGravityDustInducer;

import java.util.*;

public class BlockBreakListener {

    private static final HashSet<Block> ACCEPTABLE_BLOCKS = new HashSet<>();
    private static final HashMap<Block, TIntHashSet> ACCEPTABLE_BLOCKS_WITH_META = new HashMap<>();
    private static final HashSet<String> ACCEPTABLE_BLOCK_ORE_NAMES = new HashSet<>();
    private static final HashSet<Item> ACCEPTABLE_DROPS = new HashSet<>();
    private static final HashSet<String> ACCEPTABLE_DROPS_ORE_NAMES = new HashSet<>();
    private static final HashMap<Item, TIntHashSet> ACCEPTABLE_DROPS_WITH_META = new HashMap<>();

    public static void clearAcceptableBlocksAndDrops() {
        ACCEPTABLE_BLOCKS.clear();
        ACCEPTABLE_BLOCKS_WITH_META.clear();
        ACCEPTABLE_BLOCK_ORE_NAMES.clear();
        ACCEPTABLE_DROPS.clear();
        ACCEPTABLE_DROPS_ORE_NAMES.clear();
        ACCEPTABLE_DROPS_WITH_META.clear();
    }

    //TODO: Spacetime distorter
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockDropItems(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer harvester = event.getHarvester();
        // FakePlayers are allowed, custom item entities are not spawned in that case, instead, the anti-mass item is directly added to the drops
        if (harvester != null) {

            IBlockState blockState = event.getState();
            Block block = blockState.getBlock();

            ItemStack heldItemMainhand = harvester.getHeldItemMainhand();

            // TODO: Add config option that disables needing a distorter
            if (!heldItemMainhand.isEmpty() && ItemGravityDustInducer.hasDistorterTag(heldItemMainhand)) {
                World world = event.getWorld();

                if (block == Blocks.LIT_REDSTONE_ORE) {
                    block = Blocks.REDSTONE_ORE;
                }

                if (isBlockAcceptable(block) || isBlockWithMetaAcceptable(block, blockState)
                        || isBlockWithOreDictAcceptable(block, blockState, world, event.getPos(), event.getHarvester())) {
                    // Block broken is ok, check each of the drops to see if they're acceptable, we may return after the first acceptable drop, dependent on
                    // config

                    List<ItemStack> drops = event.getDrops();

                    // Values frequently used inside the loop, get them once, here, outside of the loop
                    BlockPos pos = event.getPos();
                    // TODO: Do some proper benchmarking on multiple accesses of final instance field within a method, compared to accessing once and
                    // storing to a local variable. It may be that extracting such fields from loops is unneeded.
                    Random worldRand = world.rand;
                    float dustDropChance = 0;

                    for (ListIterator<ItemStack> it = drops.listIterator(); it.hasNext(); ) {
                        ItemStack stack = it.next();
                        Item item = stack.getItem();

                        if (isItemAcceptable(item) || isItemWithMetaAcceptable(item, stack.getItemDamage()) || isItemWithOreDictAcceptable(stack)) {
                            // The order of checks and results of each check have been carefully organised such that the chance of the item dropping from the
                            // block remains the same as normal
                            if (worldRand.nextFloat() < dustDropChance) {
                                // dustDropChance should be fairly low, so we won't get to this code very often

                                // Vanilla uses <=, which technically means there is a 1/(pretty big(2^24?)) chance for an item to still drop even with a
                                // drop chance of 0, in the case that the pseudo-random number generator generates exactly zero
                                // no drop at all
                                if (!(harvester instanceof FakePlayer)) {
                                    // drop special item
                                    it.remove();
                                }
                            }
                            // Item should drop as per normal (we'll let vanilla handle if the item drops or not)

                            // Only one attempt allowed per block broken
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * @param block to check
     * @return true if the Block is acceptable
     */
    private static boolean isBlockAcceptable(Block block) {
        return ACCEPTABLE_BLOCKS.contains(block);
    }

    /**
     * This uses the same values you would use when using the "/setblock" command.
     *
     * @param block      to check, it is assumed that blockState.getBlock() == block
     * @param blockState IBlockState that will be converted to meta
     * @return true if the block and its metadata value is acceptable
     */
    // Replace with checking property names and values?
    private static boolean isBlockWithMetaAcceptable(Block block, IBlockState blockState) {
        TIntHashSet metadataSet = ACCEPTABLE_BLOCKS_WITH_META.get(block);
        return metadataSet != null && metadataSet.contains(block.getMetaFromState(blockState));
    }

    /**
     * This attempts to get the Item representation of the Block via block::getPickBlock
     *
     * @param block      to check, it is assumed that blockState.getBlock() == block
     * @param blockState IBlockState that may be converted to meta if getPickBlock is not overridden
     * @param world      of the broken block
     * @param pos        of the broken block
     * @param player     who broke the block
     * @return true if the 'picked' item from the block has an acceptable ore dictionary name
     */
    private static boolean isBlockWithOreDictAcceptable(Block block, IBlockState blockState, World world, BlockPos pos, EntityPlayer player) {
        // For a block to be in the ore dictionary, it must have an Item registered to it, so we get it through the getPickBlock method
        ItemStack itemFromBlock = block.getPickBlock(blockState, null, world, pos, player);

        // OreDictionary will throw an exception if the Item from a Block is null
        int[] oreIDs = OreDictionary.getOreIDs(itemFromBlock);

        for (int oreID : oreIDs) {
            String oreName = OreDictionary.getOreName(oreID);
            if (ACCEPTABLE_BLOCK_ORE_NAMES.contains(oreName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param item to check
     * @return true if item is an acceptable drop
     */
    private static boolean isItemAcceptable(Item item) {
        return ACCEPTABLE_DROPS.contains(item);
    }

    /**
     * @param item to check
     * @param meta to check
     * @return true if item with specified meta is an acceptable drop
     */
    private static boolean isItemWithMetaAcceptable(Item item, int meta) {
        TIntHashSet metadataSet = ACCEPTABLE_DROPS_WITH_META.get(item);
        if (metadataSet != null) {
            if (metadataSet.contains(meta)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param stack to check
     * @return true if stack has an acceptable ore dictionary name for a drop
     */
    private static boolean isItemWithOreDictAcceptable(ItemStack stack) {
        int[] oreIDs = OreDictionary.getOreIDs(stack);
        for (int oreID : oreIDs) {
            String oreName = OreDictionary.getOreName(oreID);
            if (ACCEPTABLE_DROPS_ORE_NAMES.contains(oreName)) {
                return true;
            }
        }
        return false;
    }
}

package io.enderdev.endermodpacktweaks.features.instantbonemeal;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoneMealHandler {
    @SubscribeEvent
    public void onBoneMealUse(BonemealEvent event) {
        if (event.getWorld().isRemote) {
            return;
        }
        boolean processed = false;
        IBlockState blockState = event.getBlock();
        Block block = blockState.getBlock();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (block instanceof BlockCrops && !((BlockCrops) block).isMaxAge(blockState)) {
            world.setBlockState(pos, ((BlockCrops) block).withAge(((BlockCrops) block).getMaxAge()), 3);
            processed = true;
        } else if (block instanceof BlockStem && blockState.getValue(BlockStem.AGE) != 7) {
            world.setBlockState(pos, blockState.withProperty(BlockStem.AGE, 7), 3);
            processed = true;
        } else if (block instanceof BlockSapling) {
            ((BlockSapling) block).generateTree(world, pos, blockState, world.rand);
            processed = true;
        } else {
            try {
                block.getClass().getMethod("generateTree", World.class, BlockPos.class, IBlockState.class, java.util.Random.class)
                        .invoke(block, world, pos, blockState, world.rand);
                world.setBlockState(pos,world.getBlockState(pos.up(1)), 3);
            } catch (Exception ignored) {

            } finally {
                if (blockState instanceof IGrowable && ((IGrowable) block).canGrow(world, pos, blockState, world.isRemote)) {
                    ((IGrowable) block).grow(world, world.rand, pos, blockState);
                    processed = true;
                }
            }
        }
        if (processed) {
            event.setResult(Event.Result.ALLOW);
        }
    }
}

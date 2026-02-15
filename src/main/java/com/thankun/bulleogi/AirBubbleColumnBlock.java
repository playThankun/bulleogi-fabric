package com.thankun.bulleogi;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class AirBubbleColumnBlock extends BubbleColumnBlock {
    
    public AirBubbleColumnBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true; 
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    // 핵심: 이 블록은 항상 '비어있는(공기)' 상태라고 구라(?)를 칩니닿. 혹시 이 놀라운 문구를 볼 사람이 있을가욯 홓홓홓
    @Override
    public FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.getDefaultState();
    }

    public BlockState getStateForNeighborUpdate(
            BlockState state, 
            Direction direction, 
            BlockState neighborState, 
            WorldAccess world, 
            ScheduledTickView tickView, 
            BlockPos pos, 
            BlockPos neighborPos
    ) {
        return state; 
    }
}
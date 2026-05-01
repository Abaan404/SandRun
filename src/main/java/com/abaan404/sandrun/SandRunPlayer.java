package com.abaan404.sandrun;

import net.minecraft.block.BlockState;

public class SandRunPlayer {
    private final BlockState blockState;

    public SandRunPlayer(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }
}

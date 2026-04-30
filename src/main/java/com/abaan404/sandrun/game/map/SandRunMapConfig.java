package com.abaan404.sandrun.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class SandRunMapConfig {
    public static final Codec<SandRunMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("spawn_block").forGetter(map -> map.spawnBlock))
            .apply(instance, SandRunMapConfig::new));

    public final BlockState spawnBlock;

    public SandRunMapConfig(BlockState spawnBlock) {
        this.spawnBlock = spawnBlock;
    }
}

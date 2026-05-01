package com.abaan404.sandrun;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

public record SandRunConfig(WaitingLobbyConfig players, Identifier map, boolean pvp, long maxDuration, int frequency,
        List<BlockState> blocks) {
    public static final MapCodec<SandRunConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(SandRunConfig::players),
            Identifier.CODEC.fieldOf("map").forGetter(SandRunConfig::map),
            Codec.BOOL.optionalFieldOf("pvp", false).forGetter(SandRunConfig::pvp),
            Codec.LONG.optionalFieldOf("max_duration", (long) (3 * 60 * 1000)).forGetter(SandRunConfig::maxDuration),
            Codec.INT.optionalFieldOf("frequency", 4).forGetter(SandRunConfig::frequency),
            Codec.list(BlockState.CODEC)
                    .optionalFieldOf("blocks", List.of(
                            Blocks.WHITE_CONCRETE_POWDER.getDefaultState(),
                            Blocks.ORANGE_CONCRETE_POWDER.getDefaultState(),
                            Blocks.MAGENTA_CONCRETE_POWDER.getDefaultState(),
                            Blocks.LIGHT_BLUE_CONCRETE_POWDER.getDefaultState(),
                            Blocks.YELLOW_CONCRETE_POWDER.getDefaultState(),
                            Blocks.LIME_CONCRETE_POWDER.getDefaultState(),
                            Blocks.PINK_CONCRETE_POWDER.getDefaultState(),
                            Blocks.GRAY_CONCRETE_POWDER.getDefaultState(),
                            Blocks.LIGHT_GRAY_CONCRETE_POWDER.getDefaultState(),
                            Blocks.CYAN_CONCRETE_POWDER.getDefaultState(),
                            Blocks.PURPLE_CONCRETE_POWDER.getDefaultState(),
                            Blocks.BLUE_CONCRETE_POWDER.getDefaultState(),
                            Blocks.BROWN_CONCRETE_POWDER.getDefaultState(),
                            Blocks.GREEN_CONCRETE_POWDER.getDefaultState(),
                            Blocks.RED_CONCRETE_POWDER.getDefaultState(),
                            Blocks.BLACK_CONCRETE_POWDER.getDefaultState()))
                    .forGetter(SandRunConfig::blocks))
            .apply(instance, SandRunConfig::new));
}

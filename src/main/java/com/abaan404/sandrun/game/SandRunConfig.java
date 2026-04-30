package com.abaan404.sandrun.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.abaan404.sandrun.game.map.SandRunMapConfig;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

public record SandRunConfig(WaitingLobbyConfig players, SandRunMapConfig mapConfig, int timeLimitSecs) {
    public static final MapCodec<SandRunConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(SandRunConfig::players),
            SandRunMapConfig.CODEC.fieldOf("map").forGetter(SandRunConfig::mapConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(SandRunConfig::timeLimitSecs))
            .apply(instance, SandRunConfig::new));
}

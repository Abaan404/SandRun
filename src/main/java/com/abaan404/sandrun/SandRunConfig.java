package com.abaan404.sandrun;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

public record SandRunConfig(WaitingLobbyConfig players, Identifier map, boolean pvp, long duration, int frequency, List<Identifier> blocks) {
    public static final MapCodec<SandRunConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(SandRunConfig::players),
            Identifier.CODEC.fieldOf("map").forGetter(SandRunConfig::map),
            Codec.BOOL.fieldOf("pvp").forGetter(SandRunConfig::pvp),
            Codec.LONG.fieldOf("duration").forGetter(SandRunConfig::duration),
            Codec.INT.fieldOf("frequency").forGetter(SandRunConfig::frequency),
            Codec.list(Identifier.CODEC).fieldOf("blocks").forGetter(SandRunConfig::blocks))
            .apply(instance, SandRunConfig::new));

    
}

package com.abaan404.sandrun;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Uuids;
import xyz.nucleoid.plasmid.api.util.PlayerRef;

public record SandRunPlayer(PlayerRef ref, String offlineName) {
    public static final SandRunPlayer DEFAULT = new SandRunPlayer(PlayerRef.ofUnchecked(UUID.randomUUID()), "Mumbo");

    public static final Codec<SandRunPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("uuid").xmap(PlayerRef::new, PlayerRef::id).forGetter(SandRunPlayer::ref),
            Codec.STRING.fieldOf("offline_name").forGetter(SandRunPlayer::offlineName))
            .apply(instance, SandRunPlayer::new));

    public static SandRunPlayer of(PlayerEntity player) {
        return SandRunPlayer.of(player.getGameProfile());
    }

    public static SandRunPlayer of(GameProfile profile) {
        return new SandRunPlayer(PlayerRef.of(profile), profile.getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SandRunPlayer other = (SandRunPlayer) obj;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        return true;
    }
}

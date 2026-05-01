package com.abaan404.sandrun.gameplay;

import java.util.Set;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.map_templates.BlockBounds;

public class SpawnLogic {
    private final ServerWorld world;

    public SpawnLogic(ServerWorld world) {
        this.world = world;
    }

    /**
     * Resets the player.
     *
     * @param player The player.
     */
    public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
        player.changeGameMode(gameMode);
        player.setVelocity(Vec3d.ZERO);
        player.fallDistance = 0.0f;
    }

    /**
     * Spawn a player in a randomly selected block in the region bounds.
     *
     * @param player The player.
     * @param spawn The bounds to spawn in.
     */
    public void spawnPlayer(ServerPlayerEntity player, BlockBounds spawn) {
        float x = MathHelper.nextFloat(player.getRandom(), spawn.min().getX(), spawn.max().getX());
        float y = Math.min(spawn.min().getY(), spawn.max().getY());
        float z = MathHelper.nextFloat(player.getRandom(), spawn.min().getZ(), spawn.max().getZ());

        player.teleport(this.world, x, y, z, Set.of(), 0.0F, 0.0F, true);
    }
}

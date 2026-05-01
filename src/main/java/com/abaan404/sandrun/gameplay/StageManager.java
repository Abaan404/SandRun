package com.abaan404.sandrun.gameplay;

import java.util.Set;

import com.abaan404.sandrun.SandRunConfig;
import com.abaan404.sandrun.SandRunMap;
import com.abaan404.sandrun.SandRunPlayer;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.api.game.GameSpace;

public class StageManager {
    private final GameSpace gameSpace;
    private final ServerWorld world;
    private final SandRunMap map;

    private final SpawnLogic spawnLogic;

    private final Set<SandRunPlayer> participants = new ObjectOpenHashSet<>();

    public StageManager(GameSpace gameSpace, SandRunConfig config, ServerWorld world, SandRunMap map) {
        this.gameSpace = gameSpace;
        this.world = world;
        this.map = map;
        this.spawnLogic = new SpawnLogic(world);
    }

    /**
     * Spawn the player (or spectator) in the spawn bounding box
     *
     * @param player The player.
     */
    public void spawnPlayer(ServerPlayerEntity player) {
        SandRunPlayer sPlayer = SandRunPlayer.of(player);
        SandRunMap.Regions regions = this.map.getRegions();

        // spawn spectators at spawn without boats
        if (!this.participants.contains(sPlayer)) {
            this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
            this.spawnLogic.spawnPlayer(player, regions.spawn());
            return;
        }

        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player, regions.spawn());
    }

    public void tick() {
    }

    /**
     * Transition the player to a spectator.
     *
     * @param player The player.
     */
    public void toSpectator(SandRunPlayer player) {
        if (!this.participants.contains(player)) {
            return;
        }

        this.participants.remove(player);
    }

    /**
     * Transition a player to a participant.
     *
     * @param player The player.
     */
    public void toParticipant(SandRunPlayer player) {
        if (this.participants.contains(player)) {
            return;
        }

        this.participants.add(player);
    }

}

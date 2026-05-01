package com.abaan404.sandrun.gameplay;

import java.util.Map;
import com.abaan404.sandrun.SandRunConfig;
import com.abaan404.sandrun.SandRunMap;
import com.abaan404.sandrun.SandRunPlayer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.util.PlayerRef;

public class StageManager {
    private final GameSpace gameSpace;
    private final ServerWorld world;
    private final SandRunMap map;
    private final SandRunConfig config;

    private final SpawnLogic spawnLogic;

    private final Map<PlayerRef, SandRunPlayer> participants = new Object2ObjectOpenHashMap<>();

    private int nextBlockStateIdx = 0;
    private long duration = 0;

    public StageManager(GameSpace gameSpace, SandRunConfig config, ServerWorld world, SandRunMap map) {
        this.gameSpace = gameSpace;
        this.world = world;
        this.map = map;
        this.config = config;

        this.spawnLogic = new SpawnLogic(world);
    }

    /**
     * Spawn the player (or spectator) in the spawn bounding box
     *
     * @param player The player.
     */
    public void spawnPlayer(ServerPlayerEntity player) {
        PlayerRef playerRef = PlayerRef.of(player);
        SandRunMap.Regions regions = this.map.getRegions();

        // spawn spectators at spawn without boats
        if (!this.participants.containsKey(playerRef)) {
            this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
            this.spawnLogic.spawnPlayer(player, regions.spawn());
            return;
        }

        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player, regions.spawn());
    }

    /**
     * Tick forward the game.
     */
    public void tick() {
        boolean allDisconnected = true;
        for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
            allDisconnected &= !PlayerRef.of(player).isOnline(this.gameSpace);
        }

        if (this.duration > this.config.duration() || this.participants.isEmpty() || allDisconnected) {
            this.endGame();
            return;
        }

        // summon blocks
        this.tickFallingBlocks();

        this.duration += this.world.getTickManager().getMillisPerTick();
    }

    /**
     * Transition the player to a spectator.
     *
     * @param player The player.
     */
    public void toSpectator(PlayerRef player) {
        if (!this.participants.containsKey(player)) {
            return;
        }

        this.participants.remove(player);
    }

    /**
     * Transition a player to a participant.
     *
     * @param player The player.
     */
    public void toParticipant(PlayerRef player) {
        if (this.participants.containsKey(player)) {
            return;
        }

        BlockState blockState = Blocks.STONE.getDefaultState();

        if (this.config.blocks().size() != 0) {
            blockState = this.config.blocks().get(nextBlockStateIdx);
            nextBlockStateIdx = (nextBlockStateIdx + 1) % this.config.blocks().size();
        }

        this.participants.put(player, new SandRunPlayer(blockState));
    }

    private void tickFallingBlocks() {
        // only tick every N ticks
        if (this.gameSpace.getServer().getTicks() % this.config.frequency() != 0) {
            return;
        }

        for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
            PlayerRef playerRef = PlayerRef.of(player);

            if (!this.participants.containsKey(playerRef)) {
                continue;
            }

            SandRunPlayer sPlayer = this.participants.get(playerRef);

            BlockPos playerPos = player.getBlockPos();
            BlockBounds sandSpawn = this.map.getRegions().sandSpawn();

            // spawn at player's x, z and the sand region's minimum height
            Vec3d spawnLocation = new Vec3d(playerPos.getX(), sandSpawn.min().getY(), playerPos.getZ());

            // dont spawn anything if player is not within the range
            if (!sandSpawn.asBox().contains(spawnLocation)) {
                continue;
            }

            FallingBlockEntity.spawnFromBlock(this.world, BlockPos.ofFloored(spawnLocation), sPlayer.getBlockState());
        }
    }

    private void endGame() {
        this.gameSpace.close(GameCloseReason.FINISHED);
    }

}

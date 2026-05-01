package com.abaan404.sandrun.game;

import com.abaan404.sandrun.SandRunConfig;
import com.abaan404.sandrun.SandRunMap;
import com.abaan404.sandrun.gameplay.SpawnLogic;
import com.abaan404.sandrun.utils.TextUtils;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class Waiting {
    private final GameSpace gameSpace;
    private final SandRunMap map;
    private final SandRunConfig config;
    private final SpawnLogic spawnLogic;
    private final ServerWorld world;

    private Waiting(GameSpace gameSpace, ServerWorld world, SandRunMap map, SandRunConfig config) {
        this.gameSpace = gameSpace;
        this.map = map;
        this.config = config;
        this.world = world;
        this.spawnLogic = new SpawnLogic(world);
    }

    public static GameOpenProcedure open(GameOpenContext<SandRunConfig> context) {
        SandRunConfig config = context.config();

        SandRunMap map = SandRunMap.load(context.server(), config.map());
        RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
                .setGenerator(map.asGenerator(context.server()));

        return context.openWithWorld(worldConfig, (game, world) -> {
            Waiting waiting = new Waiting(game.getGameSpace(), world, map, context.config());

            GameWaitingLobby lobby = GameWaitingLobby.addTo(game, config.players());

            lobby.setSidebarTitle(TextUtils.scoreboardTitleText());

            game.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);
            game.listen(GamePlayerEvents.ADD, waiting::addPlayer);
            game.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
            game.listen(GamePlayerEvents.ACCEPT, joinAcceptor -> joinAcceptor.teleport(world, map.getRegions().spawn().center()));
            game.listen(PlayerDeathEvent.EVENT, waiting::onPlayerDeath);
        });
    }

    private GameResult requestStart() {
        Active.open(this.gameSpace, this.world, this.map, this.config);
        return GameResult.ok();
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnPlayer(player);

        StatusEffectInstance nightVision = new StatusEffectInstance(
                StatusEffects.NIGHT_VISION,
                StatusEffectInstance.INFINITE,
                0,
                false,
                false,
                false);

        player.addStatusEffect(nightVision);
    }

    private EventResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        player.setHealth(20.0f);
        this.spawnPlayer(player);
        return EventResult.DENY;
    }

    private void spawnPlayer(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player, this.map.getRegions().spawn());
    }
}

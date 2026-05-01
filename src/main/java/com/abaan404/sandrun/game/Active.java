package com.abaan404.sandrun.game;

import com.abaan404.sandrun.SandRunConfig;
import com.abaan404.sandrun.SandRunMap;
import com.abaan404.sandrun.gameplay.StageManager;
import com.abaan404.sandrun.gameplay.Widgets;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.player.JoinOfferResult;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.plasmid.api.util.PlayerRef;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class Active {
    private final StageManager stageManager;
    private final Widgets widgets;

    private Active(GameSpace gameSpace, ServerWorld world, SandRunMap map, GlobalWidgets widgets,
            SandRunConfig config) {
        this.stageManager = new StageManager(gameSpace, config, world, map);
        this.widgets = new Widgets(gameSpace, widgets, map);

        // move players to participants from the lobby
        for (ServerPlayerEntity player : gameSpace.getPlayers().participants()) {
            this.stageManager.toParticipant(PlayerRef.of(player));
        }
    }

    public static void open(GameSpace gameSpace, ServerWorld world, SandRunMap map, SandRunConfig config) {
        gameSpace.setActivity(game -> {
            GlobalWidgets widgets = GlobalWidgets.addTo(game);
            Active active = new Active(gameSpace, world, map, widgets, config);

            game.setRule(GameRuleType.CRAFTING, EventResult.DENY);
            game.setRule(GameRuleType.PORTALS, EventResult.DENY);
            game.setRule(GameRuleType.SATURATED_REGENERATION, EventResult.DENY);
            game.setRule(GameRuleType.PVP, config.pvp() ? EventResult.ALLOW : EventResult.DENY);
            game.setRule(GameRuleType.HUNGER, EventResult.DENY);
            game.setRule(GameRuleType.FALL_DAMAGE, EventResult.DENY);
            game.setRule(GameRuleType.INTERACTION, EventResult.DENY);
            game.setRule(GameRuleType.BLOCK_DROPS, EventResult.DENY);
            game.setRule(GameRuleType.UNSTABLE_TNT, EventResult.DENY);
            game.setRule(GameRuleType.FIRE_TICK, EventResult.DENY);
            game.setRule(GameRuleType.ICE_MELT, EventResult.DENY);
            game.setRule(GameRuleType.FLUID_FLOW, EventResult.DENY);
            game.setRule(GameRuleType.CORAL_DEATH, EventResult.DENY);

            game.listen(GameActivityEvents.STATE_UPDATE, state -> state.canPlay(false));

            game.listen(GamePlayerEvents.OFFER, active::offerPlayer);
            game.listen(GamePlayerEvents.ACCEPT, joinAcceptor -> joinAcceptor.teleport(world, Vec3d.ZERO));
            game.listen(GamePlayerEvents.ADD, active::addPlayer);
            game.listen(GamePlayerEvents.REMOVE, active::removePlayer);

            game.listen(GameActivityEvents.TICK, active::tick);

            game.listen(PlayerDeathEvent.EVENT, active::onPlayerDeath);
        });
    }

    private JoinOfferResult.Accept offerPlayer(JoinOffer offer) {
        // join as spectator irrespective of intent
        for (GameProfile profile : offer.players()) {
            PlayerRef player = PlayerRef.of(profile);
            this.stageManager.toSpectator(player);
        }

        return offer.accept();
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.stageManager.spawnPlayer(player);
    }

    private void removePlayer(ServerPlayerEntity player) {
        this.stageManager.toSpectator(PlayerRef.of(player));
    }

    private EventResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        player.setHealth(20.0f);
        this.stageManager.toSpectator(PlayerRef.of(player));
        this.stageManager.spawnPlayer(player);
        return EventResult.DENY;
    }

    private void tick() {
        this.stageManager.tick();
        this.widgets.tick(this.stageManager);
    }
}

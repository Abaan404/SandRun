package com.abaan404.sandrun.gameplay;

import java.util.Map;

import com.abaan404.sandrun.SandRunMap;
import com.abaan404.sandrun.utils.TextUtils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.api.game.common.widget.SidebarWidget;
import xyz.nucleoid.plasmid.api.util.PlayerRef;

public class Widgets {
    private final GameSpace gameSpace;
    private final GlobalWidgets widgets;
    private final SandRunMap map;

    private final Map<PlayerRef, SidebarWidget> sidebars = new Object2ObjectOpenHashMap<>();

    public Widgets(GameSpace gameSpace, GlobalWidgets widgets, SandRunMap map) {
        this.gameSpace = gameSpace;
        this.map = map;
        this.widgets = widgets;
    }

    /**
     * Tick the UI for the player.
     *
     * @param stageManager The game's state.
     */
    public void tick(StageManager stageManager) {
        for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
            PlayerRef playerRef = PlayerRef.of(player);

            if (!this.sidebars.containsKey(playerRef)) {
                SidebarWidget newSidebar = this.widgets.addSidebar(
                        TextUtils.scoreboardTitleText(),
                        p -> playerRef.equals(playerRef));
                newSidebar.addPlayer(player);
                this.sidebars.put(playerRef, newSidebar);
            }

            SidebarWidget sidebar = this.sidebars.get(playerRef);

            sidebar.set(content -> {
                content.add(ScreenTexts.EMPTY);
                TextUtils.scoreboardMeta(this.map.getMeta()).forEach(content::add);
                content.add(ScreenTexts.EMPTY);

                content.add(TextUtils.scoreboardDuration(
                        stageManager.getDurationTimer(),
                        stageManager.getConfig().maxDuration()));

                content.add(ScreenTexts.EMPTY);

                content.add(TextUtils.scoreboardPlayersRemaining(
                        stageManager.getParticipantCount(),
                        this.gameSpace.getPlayers().size()));

                content.add(Text.of("                                "));
            });
        }
    }
}

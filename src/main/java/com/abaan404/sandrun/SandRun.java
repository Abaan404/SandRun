package com.abaan404.sandrun;

import net.fabricmc.api.ModInitializer;
import xyz.nucleoid.plasmid.api.game.GameType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.abaan404.sandrun.game.Waiting;

public class SandRun implements ModInitializer {

    public static final String ID = "sandrun";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<SandRunConfig> TYPE = GameType.register(
            Identifier.of(ID, "game"),
            SandRunConfig.CODEC,
            Waiting::open);

    @Override
    public void onInitialize() {
    }
}

package com.abaan404.sandrun;

import java.io.IOException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.MapTemplateSerializer;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.api.game.GameOpenException;
import xyz.nucleoid.plasmid.api.game.world.generator.TemplateChunkGenerator;

public class SandRunMap {
    private final MapTemplate template;

    private final Regions regions;

    public SandRunMap(MapTemplate template) {
        BlockBounds spawn = template.getMetadata()
                .getRegions("spawn")
                .findFirst()
                .map(TemplateRegion::getBounds)
                .orElse(BlockBounds.of(BlockPos.ORIGIN, BlockPos.ORIGIN));

        BlockBounds sandSpawn = template.getMetadata()
                .getRegions("sand_spawn")
                .findFirst()
                .map(TemplateRegion::getBounds)
                .orElseGet(() -> {
                    // the region's highest y value is where the blocks will summon by default
                    int highestY = template.getBounds().max().getY();

                    BlockPos min = template.getBounds().min();
                    BlockPos max = template.getBounds().max();
                    return new BlockBounds(
                            new BlockPos(min.getX(), highestY, min.getZ()),
                            new BlockPos(max.getX(), highestY, max.getZ()));
                });

        this.template = template;
        this.regions = new Regions(spawn, sandSpawn);
    }

    /**
     * Represents a track loaded from a resource.
     *
     * @param server     The server to load from.
     * @param identifier The resource id of the track
     * @return A loaded track.
     */
    public static SandRunMap load(MinecraftServer server, Identifier identifier) throws GameOpenException {
        MapTemplate template;

        try {
            template = MapTemplateSerializer.loadFromResource(server, identifier);
        } catch (IOException e) {
            throw new GameOpenException(Text.of(String.format("Couldn't load track {}", identifier.toString())));
        }

        return new SandRunMap(template);
    }

    /**
     * Get a check generator for this map.
     *
     * @param server The server to use.
     * @return The chunk generator.
     */
    public ChunkGenerator asGenerator(MinecraftServer server) {
        return new TemplateChunkGenerator(server, this.template);
    }

    /**
     * The regions for this track loaded from its template.
     *
     * @return The regions.
     */
    public Regions getRegions() {
        return this.regions;
    }

    public record Regions(BlockBounds spawn, BlockBounds sandSpawn) {
    }
}

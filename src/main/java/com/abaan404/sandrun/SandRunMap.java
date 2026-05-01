package com.abaan404.sandrun;

import java.io.IOException;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
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
    private final Meta meta;

    public SandRunMap(MapTemplate template) {
        NbtCompound root = template.getMetadata().getData();

        Meta meta = Meta.DEFAULT;
        if (root.contains("meta", NbtElement.COMPOUND_TYPE)) {
            meta = Meta.CODEC
                    .decode(NbtOps.INSTANCE, NbtOps.INSTANCE.getMap(root.getCompound("meta")).getOrThrow())
                    .resultOrPartial((error) -> SandRun.LOGGER.error("Failed to read track meta: {}", error))
                    .orElse(Meta.DEFAULT);
        }

        this.meta = meta;

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
            throw new GameOpenException(Text.of(String.format("Couldn't load map {}", identifier.toString())));
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

    /**
     * The meta for this track loaded from its template.
     *
     * @return The meta.
     */
    public Meta getMeta() {
        return this.meta;
    }

    public record Regions(BlockBounds spawn, BlockBounds sandSpawn) {
    }

    public record Meta(String name, List<String> authors) {
        public static final Meta DEFAULT = new Meta("Unknown Map", List.of("Unknown Authors"));

        public static final MapCodec<Meta> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("name", DEFAULT.name()).forGetter(Meta::name),
                Codec.STRING.listOf().optionalFieldOf("authors", DEFAULT.authors()).forGetter(Meta::authors))
                .apply(instance, Meta::new));
    }
}

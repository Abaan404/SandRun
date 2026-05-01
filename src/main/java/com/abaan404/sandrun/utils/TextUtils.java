package com.abaan404.sandrun.utils;

import java.util.EnumSet;
import java.util.List;

import com.abaan404.sandrun.SandRunMap;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Common texts for widgets
 */
public final class TextUtils {
    private TextUtils() {
    }

    public static Text scoreboardTitleText() {
        return Text.empty()
                .append(Text.literal("Sand").formatted(Formatting.YELLOW))
                .append(Text.literal("Run").formatted(Formatting.WHITE, Formatting.ITALIC))
                .formatted(Formatting.BOLD);
    }

    /**
     * Create lines of scoreboard texts for track metadata (i.e. authors, name, etc)
     *
     * @param meta The track meta.
     * @return A list of text for each line.
     */
    public static List<Text> scoreboardMeta(SandRunMap.Meta meta) {
        List<Text> list = new ObjectArrayList<>();

        list.add(Text.literal(" ").append(meta.name()).formatted(Formatting.BOLD));

        List<String> authorLines = new ObjectArrayList<>();

        final int maxLength = 36;
        StringBuilder currentLine = new StringBuilder();

        // wrap author names
        for (int i = 0; i < meta.authors().size(); i++) {
            String next = new String();

            if (authorLines.isEmpty() && currentLine.isEmpty()) {
                next += "  - By ";
            }

            next += meta.authors().get(i) + (i != meta.authors().size() - 1 ? ", " : " ");

            if (currentLine.length() + next.length() > maxLength) {
                authorLines.add(currentLine.toString());
                currentLine = new StringBuilder("     " + next);
            } else {
                currentLine.append(next);
            }
        }

        if (currentLine.length() > 0) {
            authorLines.add(currentLine.toString());
        }

        for (String line : authorLines) {
            list.add(Text.literal(line).formatted(Formatting.GRAY, Formatting.ITALIC));
        }

        return list;
    }

    /**
     * Format duration as text.
     *
     * @param duration    The elapsed time.
     * @param maxDuration The remaining time.
     * @return The duration text.
     */
    public static Text scoreboardDuration(long duration, long maxDuration) {
        return Text.empty()
                .append(Text.literal("Duration: ").formatted(Formatting.YELLOW))
                .append(Text.literal(TimeUtils.formatTime(
                        Math.min(duration, maxDuration),
                        EnumSet.complementOf(EnumSet.of(TimeUtils.Selector.HOURS)),
                        EnumSet.complementOf(EnumSet.of(TimeUtils.Selector.MILLISECONDS)))))
                .append(Text.literal(" / ").formatted(Formatting.ITALIC))
                .append(Text.literal(TimeUtils.formatTime(
                        maxDuration,
                        EnumSet.complementOf(EnumSet.of(TimeUtils.Selector.HOURS)),
                        EnumSet.complementOf(EnumSet.of(TimeUtils.Selector.MILLISECONDS)))));
    }

    /**
     * Format players remaining as text.
     *
     * @param players    The remaining players.
     * @param maxPlayers    The total players.
     * @return The players text.
     */
    public static Text scoreboardPlayersRemaining(int players, int maxPlayers) {
        return Text.empty()
                .append(Text.literal("Players: ").formatted(Formatting.YELLOW))
                .append(Text.literal(String.format("%d", players)))
                .append(Text.literal(" / ").formatted(Formatting.ITALIC))
                .append(Text.literal(String.format("%d", maxPlayers)));
    }
}

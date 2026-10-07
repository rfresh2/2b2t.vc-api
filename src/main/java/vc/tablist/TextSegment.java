package vc.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.*;

/**
 * A run of text with a fully resolved style (inherited from all parent components).
 */
public record TextSegment(String text, Style style) {
    private static final String LEGACY_COLOR_CODES = "0123456789abcdef";
    private static final NamedTextColor[] LEGACY_COLORS = {
        NamedTextColor.BLACK, NamedTextColor.DARK_BLUE, NamedTextColor.DARK_GREEN, NamedTextColor.DARK_AQUA,
        NamedTextColor.DARK_RED, NamedTextColor.DARK_PURPLE, NamedTextColor.GOLD, NamedTextColor.GRAY,
        NamedTextColor.DARK_GRAY, NamedTextColor.BLUE, NamedTextColor.GREEN, NamedTextColor.AQUA,
        NamedTextColor.RED, NamedTextColor.LIGHT_PURPLE, NamedTextColor.YELLOW, NamedTextColor.WHITE
    };

    /**
     * Flattens a component into lines of styled segments, splitting on newlines.
     * Legacy '§' formatting codes embedded in text are applied the same way the vanilla client does.
     */
    public static List<List<TextSegment>> lines(final Component component) {
        var segments = new ArrayList<TextSegment>();
        ComponentFlattener.basic().flatten(component, new FlattenerListener() {
            private final Deque<Style> styles = new ArrayDeque<>(List.of(Style.empty()));

            @Override
            public void pushStyle(final Style style) {
                styles.push(style.merge(styles.peek(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET));
            }

            @Override
            public void component(final String text) {
                applyLegacyCodes(text, styles.peek(), segments);
            }

            @Override
            public void popStyle(final Style style) {
                styles.pop();
            }
        });

        var lines = new ArrayList<List<TextSegment>>();
        var line = new ArrayList<TextSegment>();
        for (var segment : segments) {
            var parts = segment.text().split("\n", -1);
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    lines.add(line);
                    line = new ArrayList<>();
                }
                if (!parts[i].isEmpty()) line.add(new TextSegment(parts[i], segment.style()));
            }
        }
        lines.add(line);
        return lines;
    }

    private static void applyLegacyCodes(final String text, final Style baseStyle, final List<TextSegment> out) {
        if (text.indexOf('§') < 0) {
            out.add(new TextSegment(text, baseStyle));
            return;
        }
        var style = baseStyle;
        var current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c != '§' || i + 1 >= text.length()) {
                current.append(c);
                continue;
            }
            char code = Character.toLowerCase(text.charAt(++i));
            if (!current.isEmpty()) {
                out.add(new TextSegment(current.toString(), style));
                current.setLength(0);
            }
            int colorIndex = LEGACY_COLOR_CODES.indexOf(code);
            if (colorIndex >= 0) {
                // a color code clears all decorations, same as vanilla
                style = style.color(LEGACY_COLORS[colorIndex])
                    .decorations(EnumSet.allOf(TextDecoration.class), false);
            } else {
                style = switch (code) {
                    case 'k' -> style.decoration(TextDecoration.OBFUSCATED, true);
                    case 'l' -> style.decoration(TextDecoration.BOLD, true);
                    case 'm' -> style.decoration(TextDecoration.STRIKETHROUGH, true);
                    case 'n' -> style.decoration(TextDecoration.UNDERLINED, true);
                    case 'o' -> style.decoration(TextDecoration.ITALIC, true);
                    case 'r' -> baseStyle;
                    default -> style;
                };
            }
        }
        if (!current.isEmpty()) out.add(new TextSegment(current.toString(), style));
    }
}

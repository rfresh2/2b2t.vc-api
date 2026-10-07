package vc.tablist;

import net.kyori.adventure.text.format.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Renders text the way the vanilla Minecraft font renderer does, at an integer GUI scale.
 *
 * <p>Glyphs are rasterized once from the Minecraft TTF/OTF without antialiasing so they come out as crisp pixel art,
 * then cached as masks. Spacing, bold, italic, shadow, underline and strikethrough follow vanilla's rules, all measured
 * in GUI pixels (1 GUI pixel = {@code scale} image pixels).
 *
 * <p>Characters missing from the Minecraft font are rasterized from a system fallback font at image resolution,
 * similar to how vanilla falls back to unifont.
 */
public final class MinecraftFont {
    public static final int LINE_HEIGHT = 9;
    /** Distance from the top of a line to the glyph baseline, in GUI pixels */
    private static final int BASELINE = 7;
    private static final TextColor DEFAULT_COLOR = NamedTextColor.WHITE;

    private final Font font;
    private final Font fallbackFont;
    private final int scale;
    private final Map<Integer, Glyph> glyphs = new ConcurrentHashMap<>();
    private volatile Map<Integer, List<Integer>> obfuscationPool;

    /**
     * @param mask image-pixel mask, row-major
     * @param top offset of the mask's first row from the baseline (negative = above), in image pixels
     * @param advance horizontal advance, in image pixels
     */
    private record Glyph(boolean[] mask, int width, int height, int top, int advance) {
        boolean isEmpty() {
            return width == 0;
        }
    }

    public MinecraftFont(final Font minecraftFont, final int scale) {
        this.scale = scale;
        // the minecraft font draws 1 font pixel per (size / 9) points
        this.font = minecraftFont.deriveFont((float) (9 * scale));
        this.fallbackFont = new Font(Font.SANS_SERIF, Font.PLAIN, 9 * scale);
    }

    public int scale() {
        return scale;
    }

    public boolean canDisplay(final int codepoint) {
        return font.canDisplay(codepoint) || fallbackFont.canDisplay(codepoint);
    }

    /** Width of the line in image pixels, including vanilla's trailing 1 GUI pixel of glyph spacing */
    public int width(final List<TextSegment> line) {
        int width = 0;
        for (var segment : line) {
            width += width(segment.text(), segment.style());
        }
        return width;
    }

    public int width(final String text, final Style style) {
        boolean bold = style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE;
        int width = 0;
        for (int i = 0; i < text.length(); ) {
            int cp = text.codePointAt(i);
            i += Character.charCount(cp);
            width += glyph(cp).advance() + (bold ? scale : 0);
        }
        return width;
    }

    /**
     * Draws a line of text with vanilla's drop shadow
     *
     * @param x left edge, in image pixels
     * @param y top of the line, in image pixels
     */
    public void draw(final Graphics2D g, final List<TextSegment> line, final int x, final int y) {
        int penX = x;
        for (var segment : line) {
            var style = segment.style();
            var shadow = shadowColor(style);
            if (shadow != null) {
                drawSegment(g, segment.text(), style, shadow, penX + scale, y + scale);
            }
            penX = drawSegment(g, segment.text(), style, color(style), penX, y);
        }
    }

    public void draw(final Graphics2D g, final String text, final Style style, final int x, final int y) {
        draw(g, List.of(new TextSegment(text, style)), x, y);
    }

    /** @return x position after the segment */
    private int drawSegment(final Graphics2D g, final String text, final Style style, final Color color, final int x, final int y) {
        boolean bold = style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE;
        boolean italic = style.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE;
        boolean obfuscated = style.decoration(TextDecoration.OBFUSCATED) == TextDecoration.State.TRUE;
        boolean underlined = style.decoration(TextDecoration.UNDERLINED) == TextDecoration.State.TRUE;
        boolean strikethrough = style.decoration(TextDecoration.STRIKETHROUGH) == TextDecoration.State.TRUE;
        int baseline = y + BASELINE * scale;
        g.setColor(color);
        int penX = x;
        for (int i = 0; i < text.length(); ) {
            int cp = text.codePointAt(i);
            i += Character.charCount(cp);
            var glyph = glyph(cp);
            int advance = glyph.advance() + (bold ? scale : 0);
            if (obfuscated) glyph = glyph(obfuscate(cp, glyph));
            drawGlyph(g, glyph, penX, baseline, y, italic);
            if (bold) drawGlyph(g, glyph, penX + scale, baseline, y, italic);
            // vanilla draws decorations 1 pixel past each glyph on both sides so they connect
            if (strikethrough) {
                g.fillRect(penX - scale, y + (int) (3.5 * scale), advance + scale, scale);
            }
            if (underlined) {
                g.fillRect(penX - scale, y + (LINE_HEIGHT - 1) * scale, advance + scale, scale);
            }
            penX += advance;
        }
        return penX;
    }

    private void drawGlyph(final Graphics2D g, final Glyph glyph, final int x, final int baseline, final int lineTop, final boolean italic) {
        for (int row = 0; row < glyph.height(); row++) {
            int py = baseline + glyph.top() + row;
            // vanilla shears italic glyphs by 1/4 pixel per pixel of height, centered on the middle of the line
            int shift = italic ? Math.round((1.0f - 0.25f * ((py - lineTop + 0.5f) / scale)) * scale) : 0;
            int base = row * glyph.width();
            int col = 0;
            while (col < glyph.width()) {
                if (!glyph.mask()[base + col]) {
                    col++;
                    continue;
                }
                int start = col;
                while (col < glyph.width() && glyph.mask()[base + col]) col++;
                g.fillRect(x + start + shift, py, col - start, 1);
            }
        }
    }

    private Color color(final Style style) {
        var color = style.color() == null ? DEFAULT_COLOR : style.color();
        return new Color(color.value());
    }

    private Color shadowColor(final Style style) {
        ShadowColor shadow = style.shadowColor();
        if (shadow != null) {
            return shadow.alpha() == 0 ? null : new Color(shadow.red(), shadow.green(), shadow.blue(), shadow.alpha());
        }
        // vanilla: (rgb & 0xFCFCFC) >> 2, i.e. each channel at 1/4 brightness
        var color = style.color() == null ? DEFAULT_COLOR : style.color();
        return new Color((color.value() & 0xFCFCFC) >> 2);
    }

    private Glyph glyph(final int codepoint) {
        return glyphs.computeIfAbsent(codepoint, this::rasterize);
    }

    private Glyph rasterize(final int codepoint) {
        if (codepoint == ' ') return new Glyph(new boolean[0], 0, 0, 0, 4 * scale);
        boolean fallback = !font.canDisplay(codepoint) && fallbackFont.canDisplay(codepoint);
        var f = fallback ? fallbackFont : font;
        int size = f.getSize();
        int canvas = size * 3;
        int originX = size;
        int originY = size * 2;
        var image = new BufferedImage(canvas, canvas, BufferedImage.TYPE_INT_ARGB);
        var g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setFont(f);
        g.setColor(Color.WHITE);
        var str = new String(Character.toChars(codepoint));
        g.drawString(str, originX, originY);
        int fontAdvance = g.getFontMetrics().stringWidth(str);
        g.dispose();

        int minX = canvas, minY = canvas, maxX = -1, maxY = -1;
        for (int py = 0; py < canvas; py++) {
            for (int px = 0; px < canvas; px++) {
                if ((image.getRGB(px, py) >>> 24) != 0) {
                    minX = Math.min(minX, px);
                    maxX = Math.max(maxX, px);
                    minY = Math.min(minY, py);
                    maxY = Math.max(maxY, py);
                }
            }
        }
        if (maxX < 0) {
            // whitespace or unsupported: keep the font's advance, snapped to whole GUI pixels
            int advance = Math.max(scale, Math.round((float) fontAdvance / scale) * scale);
            return new Glyph(new boolean[0], 0, 0, 0, advance);
        }
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        var mask = new boolean[width * height];
        for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
                mask[py * width + px] = (image.getRGB(minX + px, minY + py) >>> 24) != 0;
            }
        }
        // vanilla advance = glyph width + 1 GUI pixel of spacing
        int advance = roundUp(width, fallback ? 1 : scale) + scale;
        return new Glyph(mask, width, height, minY - originY, advance);
    }

    private static int roundUp(final int value, final int multiple) {
        return (value + multiple - 1) / multiple * multiple;
    }

    /** Picks a random printable ascii glyph with the same advance, like vanilla's obfuscated style */
    private int obfuscate(final int codepoint, final Glyph glyph) {
        if (glyph.isEmpty()) return codepoint;
        var pool = obfuscationPool;
        if (pool == null) {
            var newPool = new ConcurrentHashMap<Integer, List<Integer>>();
            for (int c = 33; c < 127; c++) {
                newPool.computeIfAbsent(glyph(c).advance(), k -> new ArrayList<>()).add(c);
            }
            obfuscationPool = pool = newPool;
        }
        var candidates = pool.get(glyph.advance());
        if (candidates == null || candidates.isEmpty()) return codepoint;
        return candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
    }
}

package vc.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Renders the tablist over an in-game screenshot, drawing the backdrop, cells, and text
 * the same way vanilla's PlayerTabOverlay does.
 *
 * <p>Each render uses a random PNG screenshot from the {@code tablist-backgrounds} folder in the working directory,
 * never the same one twice in a row. If that folder is missing or has no PNGs, the bundled {@code tab-bare.png} is used.
 * Screenshots must be taken at GUI scale {@link #SCALE}.
 *
 * <p>All layout math is in GUI pixels (1 GUI pixel = {@link #SCALE} image pixels), matching the screenshot's GUI scale.
 * Unlike vanilla, which caps the tablist at 80 players and 20 rows, rows grow as needed so every player fits on screen.
 */
@org.springframework.stereotype.Component
public class TablistRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TablistRenderer.class);
    private static final int SCALE = 2;
    private static final int TOP = 10;
    private static final int BOTTOM_MARGIN = 10;
    /** Vanilla limits the tablist width to the screen width minus this */
    private static final int HORIZONTAL_MARGIN = 50;
    private static final int COLUMN_SPACING = 5;
    private static final int ROW_HEIGHT = 9;
    private static final int CELL_HEIGHT = 8;
    private static final int HEAD_SIZE = 8;
    /** Extra space above the header and footer text, not in vanilla */
    private static final int HEADER_PADDING_TOP = 4;
    private static final int FOOTER_PADDING_TOP = 6;
    /** Vanilla's preferred max rows before adding more columns */
    private static final int PREFERRED_MAX_ROWS = 20;
    /** Below this column width, show fewer players instead of truncating names further */
    private static final int MIN_COLUMN_WIDTH = HEAD_SIZE + 1 + 40;
    private static final Color BACKDROP_COLOR = new Color(0x80000000, true);
    private static final Color CELL_COLOR = new Color(0x20FFFFFF, true);
    private static final Duration HEAD_FETCH_TIMEOUT = Duration.ofSeconds(30);
    private static final Path BACKGROUNDS_DIR = Path.of("tablist-backgrounds");
    private static final String DEFAULT_BACKGROUND = "default-tablist-background.png";

    private final MinecraftFont font;
    private final PlayerHeadCache headCache;
    private Path lastBackground;

    public record TablistPlayer(String name, UUID uuid) { }

    private record Layout(int columns, int rows, int columnWidth, int shownPlayers) {
        int width() {
            return columns * columnWidth + (columns - 1) * COLUMN_SPACING;
        }
    }

    public TablistRenderer(final PlayerHeadCache headCache) throws IOException, FontFormatException {
        this.headCache = headCache;
        try (var fontFile = new ClassPathResource("Minecraft.otf").getInputStream()) {
            this.font = new MinecraftFont(Font.createFont(Font.TRUETYPE_FONT, fontFile), SCALE);
        }
    }

    /**
     * Picks a random background from {@link #BACKGROUNDS_DIR}, different from the previous one when there's more than one.
     * The folder is re-listed on every render so images can be added or removed without a restart.
     * Images are decoded per render rather than held in memory, a decoded 2560x1440 screenshot is ~15MB.
     */
    private BufferedImage nextBackground() throws IOException {
        var candidates = listBackgrounds();
        if (candidates.isEmpty()) {
            try (var in = new ClassPathResource(DEFAULT_BACKGROUND).getInputStream()) {
                return ImageIO.read(in);
            }
        }
        Path selected;
        synchronized (this) {
            var choices = candidates.size() > 1
                ? candidates.stream().filter(p -> !p.equals(lastBackground)).toList()
                : candidates;
            selected = choices.get(ThreadLocalRandom.current().nextInt(choices.size()));
            lastBackground = selected;
        }
        var image = ImageIO.read(selected.toFile());
        if (image == null) throw new IOException("Unreadable tablist background: " + selected);
        return image;
    }

    private static List<Path> listBackgrounds() throws IOException {
        if (!Files.isDirectory(BACKGROUNDS_DIR)) return List.of();
        try (var files = Files.list(BACKGROUNDS_DIR)) {
            return files
                .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
                .filter(Files::isRegularFile)
                .toList();
        }
    }

    /**
     * @param players in display order, filled top-to-bottom then left-to-right like vanilla
     * @return PNG image bytes
     */
    public byte[] render(final List<TablistPlayer> players, final Component header, final Component footer) {
        BufferedImage baseImage;
        try {
            baseImage = nextBackground();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        int screenWidth = baseImage.getWidth() / SCALE;
        int screenHeight = baseImage.getHeight() / SCALE;
        var headerLines = TextSegment.lines(header);
        var footerLines = TextSegment.lines(footer);
        if (isBlank(headerLines)) headerLines = List.of();
        if (isBlank(footerLines)) footerLines = List.of();

        int textHeight = (headerLines.isEmpty() ? 0 : HEADER_PADDING_TOP + headerLines.size() * ROW_HEIGHT + 1)
            + (footerLines.isEmpty() ? 0 : FOOTER_PADDING_TOP + footerLines.size() * ROW_HEIGHT + 1);
        int maxRows = Math.max(1, (screenHeight - TOP - BOTTOM_MARGIN - textHeight) / ROW_HEIGHT);
        var layout = layout(players, screenWidth - HORIZONTAL_MARGIN, maxRows);

        int width = layout.width();
        for (var line : headerLines) width = Math.max(width, guiWidth(line));
        for (var line : footerLines) width = Math.max(width, guiWidth(line));
        // derive the right edge from the left so odd widths still get 1px of backdrop on both sides
        // (vanilla computes both edges with width / 2, which loses the right padding when the width is odd)
        int backdropLeft = screenWidth / 2 - width / 2 - 1;
        int backdropRight = backdropLeft + width + 2;

        // drop any alpha channel, the screenshot is opaque and this keeps the output png smaller
        var image = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        try {
            g.drawImage(baseImage, 0, 0, null);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int y = TOP;
            if (!headerLines.isEmpty()) {
                fill(g, backdropLeft, y - 1, backdropRight, y + HEADER_PADDING_TOP + headerLines.size() * ROW_HEIGHT, BACKDROP_COLOR);
                y = drawCenteredLines(g, headerLines, y + HEADER_PADDING_TOP, screenWidth) + 1;
            }
            fill(g, backdropLeft, y - 1, backdropRight, y + layout.rows() * ROW_HEIGHT, BACKDROP_COLOR);
            drawPlayers(g, players, layout, screenWidth / 2 - layout.width() / 2, y);
            y += layout.rows() * ROW_HEIGHT + 1;
            if (!footerLines.isEmpty()) {
                fill(g, backdropLeft, y - 1, backdropRight, y + FOOTER_PADDING_TOP + footerLines.size() * ROW_HEIGHT, BACKDROP_COLOR);
                drawCenteredLines(g, footerLines, y + FOOTER_PADDING_TOP, screenWidth);
            }
        } finally {
            g.dispose();
        }
        try (var out = new ByteArrayOutputStream(4 * 1024 * 1024)) {
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Picks the column/row counts like vanilla (max 20 rows, adding columns as needed), but when the columns
     * would no longer fit the screen width, adds rows instead. Only when rows run out of vertical space are columns
     * narrowed, truncating long names.
     */
    private Layout layout(final List<TablistPlayer> players, final int maxWidth, final int maxRows) {
        int count = Math.max(1, players.size());
        int maxNameWidth = 0;
        for (var player : players) {
            maxNameWidth = Math.max(maxNameWidth, guiWidth(player.name(), Style.empty()));
        }
        int columnWidth = HEAD_SIZE + 1 + maxNameWidth;
        for (int rows = Math.min(PREFERRED_MAX_ROWS, maxRows); rows <= maxRows; rows++) {
            int columns = Math.ceilDiv(count, rows);
            // balance rows across the columns, same as vanilla
            int balancedRows = Math.ceilDiv(count, columns);
            var layout = new Layout(columns, balancedRows, columnWidth, players.size());
            if (layout.width() <= maxWidth) return layout;
        }
        // not enough room for full names: use every row and narrow the columns
        int columns = Math.ceilDiv(count, maxRows);
        int maxColumns = (maxWidth + COLUMN_SPACING) / (MIN_COLUMN_WIDTH + COLUMN_SPACING);
        if (columns > maxColumns) {
            // still doesn't fit, the last cell shows how many players are hidden
            return new Layout(maxColumns, maxRows, MIN_COLUMN_WIDTH, maxColumns * maxRows - 1);
        }
        int narrowWidth = (maxWidth - (columns - 1) * COLUMN_SPACING) / columns;
        return new Layout(columns, Math.ceilDiv(count, columns), narrowWidth, players.size());
    }

    private void drawPlayers(final Graphics2D g, final List<TablistPlayer> players, final Layout layout, final int left, final int top) {
        var visible = players.subList(0, layout.shownPlayers());
        var heads = headCache.getHeads(visible.stream().map(TablistPlayer::uuid).toList(), HEAD_FETCH_TIMEOUT);
        for (int i = 0; i < visible.size(); i++) {
            var player = visible.get(i);
            int x = left + (i / layout.rows()) * (layout.columnWidth() + COLUMN_SPACING);
            int y = top + (i % layout.rows()) * ROW_HEIGHT;
            fill(g, x, y, x + layout.columnWidth(), y + CELL_HEIGHT, CELL_COLOR);
            var head = heads.get(player.uuid());
            if (head != null) {
                g.drawImage(head, x * SCALE, y * SCALE, HEAD_SIZE * SCALE, HEAD_SIZE * SCALE, null);
            }
            drawText(g, player.name(), Style.empty(), x + HEAD_SIZE + 1, y, layout.columnWidth() - HEAD_SIZE - 1);
        }
        if (visible.size() < players.size()) {
            int i = visible.size();
            int x = left + (i / layout.rows()) * (layout.columnWidth() + COLUMN_SPACING);
            int y = top + (i % layout.rows()) * ROW_HEIGHT;
            fill(g, x, y, x + layout.columnWidth(), y + CELL_HEIGHT, CELL_COLOR);
            var more = "+" + (players.size() - i) + " more";
            drawText(g, more, Style.style(NamedTextColor.GRAY), x, y, layout.columnWidth());
        }
    }

    /** Draws text starting at {@code x}, truncating with an ellipsis if wider than {@code maxWidth} GUI pixels */
    private void drawText(final Graphics2D g, final String text, final Style style, final int x, final int y, final int maxWidth) {
        font.draw(g, fit(text, style, maxWidth * SCALE), style, x * SCALE, y * SCALE);
    }

    private String fit(final String text, final Style style, final int maxWidth) {
        if (font.width(text, style) <= maxWidth) return text;
        var ellipsis = font.canDisplay('…') ? "…" : "..";
        int end = text.length();
        while (end > 0 && font.width(text.substring(0, end) + ellipsis, style) > maxWidth) end--;
        return text.substring(0, end) + ellipsis;
    }

    /** @return y position after the lines */
    private int drawCenteredLines(final Graphics2D g, final List<List<TextSegment>> lines, final int top, final int screenWidth) {
        int y = top;
        for (var line : lines) {
            font.draw(g, line, (screenWidth * SCALE - font.width(line)) / 2, y * SCALE);
            y += ROW_HEIGHT;
        }
        return y;
    }

    /** Fills a rectangle given in GUI pixel coordinates, like vanilla's GuiGraphics.fill */
    private static void fill(final Graphics2D g, final int x1, final int y1, final int x2, final int y2, final Color color) {
        g.setColor(color);
        g.fillRect(x1 * SCALE, y1 * SCALE, (x2 - x1) * SCALE, (y2 - y1) * SCALE);
    }

    private static boolean isBlank(final List<List<TextSegment>> lines) {
        return lines.stream().allMatch(List::isEmpty);
    }

    private int guiWidth(final List<TextSegment> line) {
        return Math.ceilDiv(font.width(line), SCALE);
    }

    private int guiWidth(final String text, final Style style) {
        return Math.ceilDiv(font.width(text, style), SCALE);
    }
}

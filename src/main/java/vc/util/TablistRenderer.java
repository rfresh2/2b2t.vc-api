package vc.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class TablistRenderer {
    private final Font font;
    private final BufferedImage tabBaseImage;

    public TablistRenderer() throws IOException, FontFormatException {

        try (var fontFile = new ClassPathResource("Minecraft.otf").getInputStream()) {
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        }

        try (var imageFile = new ClassPathResource("tab.png").getInputStream()) {
            tabBaseImage = ImageIO.read(imageFile);
        }
    }

    public void render() {
        BufferedImage copy = new BufferedImage(tabBaseImage.getColorModel(), tabBaseImage.copyData(null), tabBaseImage.isAlphaPremultiplied(), null);
        Graphics2D graphics = copy.createGraphics();
        graphics.setFont(font.deriveFont(200f));
        graphics.setColor(Color.WHITE);
        graphics.drawString("Hello, world! AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 50, 300);
        graphics.dispose();

        // create new file at /images/tab.png and write the modified image to it
        try {
            var path = Path.of("images/tab.png");
            var file = path.toFile();
            file.getParentFile().mkdirs();
            if (file.exists()) {
                file.delete();
            }
            try (var os = Files.newOutputStream(path)) {
                ImageIO.write(copy, "png", os);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        copy.flush();
    }
}

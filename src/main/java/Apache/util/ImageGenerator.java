package Apache.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

/**
 * Utilidad para generar una imagen PNG de prueba.
 */
public class ImageGenerator {

    public static void generateTestImage(String outputPath) throws IOException {
        Path parent = Path.of(outputPath).getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        int width = 200;
        int height = 200;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();

        // Fondo degradado
        GradientPaint gradient = new GradientPaint(0, 0, new Color(52, 152, 219),
                width, height, new Color(46, 204, 113));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Texto
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("MicroSpringBoot", 25, 95);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Servidor Web Java", 40, 115);

        g2d.dispose();
        ImageIO.write(img, "png", new File(outputPath));
    }
}

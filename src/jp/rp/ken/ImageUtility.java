package jp.rp.ken;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageUtility {

    public Image absDiff(Image img1, Image img2) {
        int width = (int) img1.getWidth();
        int height = (int) img1.getHeight();
        PixelReader reader1 = img1.getPixelReader();
        PixelReader reader2 = img2.getPixelReader();
        WritableImage ret = new WritableImage(width, height);
        PixelWriter writer = ret.getPixelWriter();
        BufferedImage resultImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color1 = reader1.getColor(i, j);
                Color color2 = reader2.getColor(i, j);
                double scale = Math.abs(((color1.getRed() + color1.getGreen() + color1.getBlue())
                        - (color2.getRed() + color2.getGreen() + color2.getBlue())) / 3.0);
                writer.setColor(i, j, new Color(scale, scale, scale, 1.0));
            }
        }

        SwingFXUtils.fromFXImage(ret, resultImg);
        return SwingFXUtils.toFXImage(resultImg, null);
    }

    public Image bitwise_and(Image img1, Image img2) {
        int width = (int) img1.getWidth();
        int height = (int) img1.getHeight();
        PixelReader reader1 = img1.getPixelReader();
        PixelReader reader2 = img2.getPixelReader();
        WritableImage ret = new WritableImage(width, height);
        PixelWriter writer = ret.getPixelWriter();
        BufferedImage resultImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color1 = reader1.getColor(i, j);
                Color color2 = reader2.getColor(i, j);
                int monoColor1 = (int) (color1.getRed() * 255.0);
                int monoColor2 = (int) (color2.getRed() * 255.0);
                double and = monoColor1 & monoColor2;
                writer.setColor(i, j, new Color(and / 256.0, and / 256.0, and / 256.0, 1.0));
            }
        }

        SwingFXUtils.fromFXImage(ret, resultImg);
        return SwingFXUtils.toFXImage(resultImg, null);

    }

    public Image binarize(Image img, int th) {
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();
        PixelReader reader = img.getPixelReader();
        WritableImage ret = new WritableImage(width, height);
        PixelWriter writer = ret.getPixelWriter();
        BufferedImage resultImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (reader.getColor(i, j).getRed() * 256 > th) {
                    writer.setColor(i, j, new Color(1, 1, 1, 1));
                } else {
                    writer.setColor(i, j, new Color(0, 0, 0, 1));
                }
            }
        }

        SwingFXUtils.fromFXImage(ret, resultImg);
        return SwingFXUtils.toFXImage(resultImg, null);
    }

    public int binarizeCount(Image img, int th) {
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();
        PixelReader reader = img.getPixelReader();
        int ret = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (reader.getColor(i, j).getRed() * 256 > th) {
                    ret++;
                }
            }
        }

        return ret;
    }
}

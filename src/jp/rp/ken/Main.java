package jp.rp.ken;


import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    Group root = new Group();
    GraphicsContext gc;

    int threshold = 50;

    MediaPlayer beep;
    MediaPlayer alert;

    boolean beeping = false;
    long beepStart = 0;

    ImageUtility utility = new ImageUtility();

    Webcam webcam = Webcam.getWebcamByName(Webcam.getWebcams().get(0).getName());
    Image[] images = new Image[3];

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        Media beepMedia = new Media(new File("beep.mp3").toURI().toString());
        Media alertMedia = new Media(new File("alert.mp3").toURI().toString());
        beep = new MediaPlayer(beepMedia);
        alert = new MediaPlayer(alertMedia);
        beep.setCycleCount(MediaPlayer.INDEFINITE);
        alert.setCycleCount(MediaPlayer.INDEFINITE);

        Dimension size = WebcamResolution.VGA.getSize();
        System.out.println(size.width+" "+size.height);
        webcam.setViewSize(new Dimension(size.width/2, size.height/2));

        stage.setTitle("Camera View");
        stage.initStyle(StageStyle.DECORATED);
        // get image
        webcam.open();
        BufferedImage bimg = webcam.getImage();

        Canvas cvs = new Canvas(bimg.getWidth()*2, bimg.getHeight()*2);
        root.getChildren().add(cvs);

        gc = cvs.getGraphicsContext2D();
        gc.setStroke(new Color(1, 1, 1, 1));
        gc.setTextAlign(TextAlignment.LEFT);
        Scene scene = new Scene(root, bimg.getWidth()*2, bimg.getHeight()*2);

        stage.setScene(scene);

        new AnimationTimer() {
            public void handle(long current) {
                BufferedImage bimg = webcam.getImage();
                Image image = SwingFXUtils.toFXImage(bimg, null);
                if (images[2]!=null) {
                    images[0] = images[1];
                    images[1] = images[2];
                    images[2] = image;
                } else {
                    images[0] = image;
                    images[1] = image;
                    images[2] = image;
                }

                Image diff1 = utility.absDiff(images[0], images[1]);
                Image diff2 = utility.absDiff(images[2], images[1]);

                Image diff = utility.bitwise_and(diff1, diff2);
                int count = utility.binarizeCount(diff, 10);
                if (count>threshold&& !beeping) {
                    beep.play();
                    beeping = true;
                    beepStart = System.nanoTime();
                } else if (count<=threshold) {
                    beep.stop();
                    alert.stop();
                    beeping = false;
                } else if (count>threshold&&System.nanoTime()-beepStart>1_500_000_000) {
                    beep.stop();
                    alert.play();
                }
                draw(diff1, diff2, diff, count, size.width/2, size.height/2);
            }
        }
                .start();
        stage.show();
    }

    void draw(Image diff1, Image diff2, Image diff, int count, int width, int height) {
        gc.drawImage(images[0], 0, 0);
        gc.drawImage(diff, 0, height);
        gc.drawImage(diff1, width, 0);
        gc.drawImage(diff2, width, height);
        gc.strokeLine(width, 0, width, height*2);
        gc.strokeLine(0, height, width*2, height);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.strokeText("Difference between image 1 and image 2", width+5, height-5);
        gc.strokeText("Difference between image 2 and image 3", width+5, height*2-5);
        gc.strokeText("Logical product between difference 1 and difference 2", 5, height*2-5);
        gc.setTextAlign(TextAlignment.RIGHT);
        if (count>threshold) gc.setStroke(new Color(1, 0.2, 0.2, 1));
        gc.strokeText( +count+" pixels detected", width-5, height+20);
        gc.setStroke(new Color(1, 1, 1, 1));
    }

}

package app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import utils.ColorScaleUtils;
import utils.ImageUtils;
import utils.ShowImage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        int x = 0;
        System.out.println(Integer.toBinaryString(x));
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        /*String text = "lol";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        System.out.println(encoded);*/

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}

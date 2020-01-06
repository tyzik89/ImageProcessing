package app;

import algorithms.Algorithm;
import algorithms.BinaryAlgorithm;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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

public class Test extends Application {

    private static int SIZE_SEGMENT = 8;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Algorithm algorithm = new BinaryAlgorithm("src/main/resources/img/order_new.jpg");
        algorithm.check();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}

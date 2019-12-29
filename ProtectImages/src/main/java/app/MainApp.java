package app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import utils.ImageUtils;
import utils.ShowImage;

import java.io.File;
import java.util.ArrayList;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        File file = new File("src/main/resources/img/order.jpg");
        String localUrl = file.toURI().toString();
        Image image = new Image(localUrl);

        Mat sourceMat = ImageUtils.imageFXToMat(image);
    }

    public static void main(String[] args) throws Exception {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}

package app;

import algorithms.Algorithm;
import algorithms.BinaryAlgorithm;
import algorithms.DCTAlgorithm;
import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.security.NoSuchAlgorithmException;

public class CheckApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Algorithm algorithm = new BinaryAlgorithm("src/main/resources/img/", "order.bmp");
        algorithm.check();
        algorithm = new DCTAlgorithm("src/main/resources/img/", "order.bmp");
        algorithm.check();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}

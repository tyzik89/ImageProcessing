package app;

import algorithms.Algorithm;
import algorithms.BinaryAlgorithm;
import algorithms.DCTAlgorithm;
import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Algorithm algorithm = new BinaryAlgorithm("src/main/resources/img/", "order.bmp");
        algorithm.run();
        algorithm = new DCTAlgorithm("src/main/resources/img/", "order.bmp");
        algorithm.run();
    }

    public static void main(String[] args) throws Exception {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}

package app;

import algorithms.Algorithm;
import algorithms.BinaryAlgorithm;
import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Algorithm algorithm = new BinaryAlgorithm("src/main/resources/img/example.jpg");
        algorithm.run();
    }

    public static void main(String[] args) throws Exception {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}

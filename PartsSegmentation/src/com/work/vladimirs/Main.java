package com.work.vladimirs;

import com.work.vladimirs.algorithm.PolygonalSegmentation;
import com.work.vladimirs.algorithm.PolygonalSegmentationImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class Main extends Application {

    public static void main(String[] args) {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PolygonalSegmentation polygonalSegmentation = new PolygonalSegmentationImpl("src/resources/", "test_image_4.bmp");
        polygonalSegmentation.run();
    }
}

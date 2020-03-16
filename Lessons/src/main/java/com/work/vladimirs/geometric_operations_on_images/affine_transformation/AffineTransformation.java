package com.work.vladimirs.geometric_operations_on_images.affine_transformation;

import com.work.vladimirs.utils.MatrixOperations;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Афинные пеобразования изображения
 */
public class AffineTransformation {

    private static final String DIR_NAME = "src/main/resources/geometric_operations_on_images/affine_transformation/AffineTransformation/";
    private static final int[][] AFFINE_MATRIX = {{2, 0, 0}, {0, 2, 0}, {0, 0, 1}};

    void process(String pathToImage) throws FileNotFoundException, MatrixOperations.MatrixMismatchException {
        File dir = new File(pathToImage);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("Files not found. Check the path.");
        }
        for (File file : files) {
            if (!file.isFile()) continue;
            Mat image = Imgcodecs.imread(pathToImage + file.getName());
            File affineImageDir = new File(pathToImage + file.getName().replaceAll("[.]\\D*", ""));
            if (affineImageDir.mkdir()) {
                Mat affineChangeImage = doTransformation(image);
                Imgcodecs.imwrite(affineImageDir.getPath() + "\\" + "affine_" + file.getName(), affineChangeImage);
            }
        }
    }

    private Mat doTransformation(Mat image) throws MatrixOperations.MatrixMismatchException {

        return new Mat();
    }


    /**
     * Запуск
     */
    public static class Run extends Application {

        @Override
        public void start(Stage stage) {
            Text text = new Text("Transformations!");
            text.setLayoutY(80);
            text.setLayoutX(100);

            Group group = new Group(text);

            Scene scene = new Scene(group);

            stage.setScene(scene);
            stage.setTitle("Affine Transformations");
            stage.setWidth(300);
            stage.setHeight(250);
            stage.centerOnScreen();
            stage.show();
        }

        public static void main(String[] args) {
            // load the native OpenCV library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            //Application.launch(args);

            System.out.println("Start application");

            AffineTransformation transformation = new AffineTransformation();
            try {
                transformation.process(DIR_NAME);
            } catch (FileNotFoundException | MatrixOperations.MatrixMismatchException e) {
                System.out.println(e);
            }

        }
    }
}

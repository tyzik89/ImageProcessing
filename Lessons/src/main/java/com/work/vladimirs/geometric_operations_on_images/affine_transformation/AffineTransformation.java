package com.work.vladimirs.geometric_operations_on_images.affine_transformation;

import com.work.vladimirs.utils.MatrixOperations;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Афинные пеобразования изображения
 */
public class AffineTransformation {

    private static final String DIR_NAME = "src/main/resources/geometric_operations_on_images/affine_transformation/AffineTransformation/";
    private static final double[][] AFFINE_MATRIX = {{2, 0, 0}, {0, 2, 0}, {0, 0, 1}};

    void process(String pathToImage) throws FileNotFoundException, MatrixOperations.MatrixMismatchException {
        File dir = new File(pathToImage);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("Files not found. Check the path.");
        }
        for (File file : files) {
            if (!file.isFile()) continue;
            Mat image = Imgcodecs.imread(pathToImage + file.getName());
            Mat affineChangeImage = doTransformation(image);
            Imgcodecs.imwrite(pathToImage + "\\" + "affine_" + file.getName(), affineChangeImage);
        }
    }

    private Mat doTransformation(Mat image) throws MatrixOperations.MatrixMismatchException {
        int rows = image.rows();
        int cols = image.cols();
        Mat result = new Mat();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //создаём вектор координат
                double[][] vector = new double[][]{{i, j, 1}};
                double[][] vectorResult = MatrixOperations.multiplication(vector, AFFINE_MATRIX);
                result.put((int)vectorResult[0][0], (int)vectorResult[0][1], image.get(i, j));          //todo Доделать алгоритм афинной трансформации
            }
        }
        return result;
    }

    /**
     * Запуск
     */
    public static class Run{

        //todo Доделать интерфейс, для ввода значений
       /* @Override
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
        }*/

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

package com.work.vladimirs.geometric_operations_on_images.affine_transformation;

import com.work.vladimirs.utils.MatrixOperations;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Афинные пеобразования изображения
 */
public class AffineTransformation {

    private static final String DIR_NAME = "src/main/resources/geometric_operations_on_images/affine_transformation/AffineTransformation/";
    private static final double[][] SCALE_TRANSFORM_AFFINE_MATRIX = {
            {0.4, 0,   0},
            {0,   0.4, 0},
            {0,   0,   1}
    };
    private static final double[][] SHIFT_TRANSFORM_AFFINE_MATRIX = {
            {1,    0, 0},
            {0,    1, 0},
            {90, -50, 1}
    };
    private static final double[][] ROTATION_TRANSFORM_AFFINE_MATRIX = {
            {Math.cos(0.5),  Math.sin(0.5), 0},
            {-Math.sin(0.5), Math.cos(0.5), 0},
            {0,              0,             1}
    };
    private static final double[][] VERTICAL_BEVEL_TRANSFORM_AFFINE_MATRIX = {
            {1, 0,   0},
            {1, 0.8, 0},
            {0, 0,   1}
    };
    private static final double[][] HORIZONTAL_BEVEL_TRANSFORM_AFFINE_MATRIX = {
            {1, 0.8, 0},
            {0, 1,   0},
            {0, 0,   1}
    };

    void process(String pathToImage) throws FileNotFoundException, MatrixOperations.MatrixMismatchException {
        File dir = new File(pathToImage);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("Files not found. Check the path.");
        }
        for (File file : files) {
            if (!file.isFile()) continue;
            Mat image = Imgcodecs.imread(pathToImage + file.getName());
            ArrayList<Mat>  affineChangesImages = doTransformation(image);
            Imgcodecs.imwrite(pathToImage + "\\" + "affine_scale_" + file.getName(), affineChangesImages.get(0));
            Imgcodecs.imwrite(pathToImage + "\\" + "affine_shift_" + file.getName(), affineChangesImages.get(1));
            Imgcodecs.imwrite(pathToImage + "\\" + "affine_rotation_" + file.getName(), affineChangesImages.get(2));
            Imgcodecs.imwrite(pathToImage + "\\" + "affine_vertical_bevel_" + file.getName(), affineChangesImages.get(3));
            Imgcodecs.imwrite(pathToImage + "\\" + "affine_horizontal_bevel_" + file.getName(), affineChangesImages.get(4));
        }
    }

    private ArrayList<Mat> doTransformation(Mat image) throws MatrixOperations.MatrixMismatchException {
        int rows = image.rows();
        int cols = image.cols();
        int type = image.type();
        ArrayList<Mat> results = new ArrayList<>(5);
        Mat resultScale = new Mat(rows, cols, type);
        Mat resultShift = new Mat(rows, cols, type);
        Mat resultRotation = new Mat(rows, cols, type);
        Mat resultVertical = new Mat(rows, cols, type);
        Mat resultHorizontal = new Mat(rows, cols, type);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //создаём вектор координат
                double[][] vector = new double[][]{{i, j, 1}};

                double[][] vectorScale = MatrixOperations.multiplication(vector, SCALE_TRANSFORM_AFFINE_MATRIX);
                resultScale.put((int)vectorScale[0][0], (int)vectorScale[0][1], image.get(i, j));

                double[][] vectorShift = MatrixOperations.multiplication(vector, SHIFT_TRANSFORM_AFFINE_MATRIX);
                resultShift.put((int)vectorShift[0][0], (int)vectorShift[0][1], image.get(i, j));

                double[][] vectorRotation = MatrixOperations.multiplication(vector, ROTATION_TRANSFORM_AFFINE_MATRIX);
                resultRotation.put((int)vectorRotation[0][0], (int)vectorRotation[0][1], image.get(i, j));

                double[][] vectorVertical = MatrixOperations.multiplication(vector, VERTICAL_BEVEL_TRANSFORM_AFFINE_MATRIX);
                resultVertical.put((int)vectorVertical[0][0], (int)vectorVertical[0][1], image.get(i, j));

                double[][] vectorHorizontal = MatrixOperations.multiplication(vector, HORIZONTAL_BEVEL_TRANSFORM_AFFINE_MATRIX);
                resultHorizontal.put((int)vectorHorizontal[0][0], (int)vectorHorizontal[0][1], image.get(i, j));
            }
        }
        results.add(resultScale);
        results.add(resultShift);
        results.add(resultRotation);
        results.add(resultVertical);
        results.add(resultHorizontal);
        return results;
    }


    /**
     * Запуск
     */
    public static class Run{

        public static void main(String[] args) {
            // load the native OpenCV library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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

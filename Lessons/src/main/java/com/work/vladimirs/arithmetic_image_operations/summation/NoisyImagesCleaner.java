package com.work.vladimirs.arithmetic_image_operations.summation;

import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Сложение (усреднение) серии зашумленных изображений, для снижения уровня шума
 */
public class NoisyImagesCleaner {

    private static final String DIR_NAME = "src/main/resources/NoisyImagesCleaner/";
    private static final int COUNT_NOISE_IMAGES = 10;

    void process(String pathToImage) {
        File dir = new File(pathToImage);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;
        for (File file : files) {
            if (!file.isFile()) continue;
            File noiseSetDir = new File(pathToImage + "\\" + file.getName().replaceAll("[.]\\D*", ""));
            File[] noisyFiles = noiseSetDir.listFiles();
            if (noisyFiles == null || noisyFiles.length == 0) continue;
            ArrayList<Mat> noisyImages = new ArrayList<Mat>();
            for (File noisyFile : noisyFiles) {
                if (!noisyFile.isFile()) continue;
                Mat noisyImage = Imgcodecs.imread(pathToImage + file.getName().replaceAll("[.]\\D*", "") + "/" + noisyFile.getName());
                noisyImages.add(noisyImage);
            }
            if (noisyImages.size() == 0) continue;
            //Делаем устреднение набора изображений
            Mat avgImg = imagesAveraging(noisyImages);
            Imgcodecs.imwrite(pathToImage + "result_" + file.getName(), avgImg);
        }
    }

    private Mat imagesAveraging(ArrayList<Mat> noisyImages) {
        Mat resultImg = new Mat();

        return resultImg;
    }

    /**
     * Создание серии изображений, искажённых аддитивным гауссовым шумом
     * Т.е. имитация серии фотографий объектов, например объектов глубокого космоса.
     */
    public static class GaussNoiseAdder {

        void createSetImages(String pathToImage, int count) {
            File dir = new File(pathToImage);
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) return;
            for (File file : files) {
                if (!file.isFile()) continue;
                Mat image = Imgcodecs.imread(pathToImage + file.getName());
                File noiseSetDir = new File(pathToImage + file.getName().replaceAll("[.]\\D*", ""));
                if (noiseSetDir.mkdir()) {
                    for (int i = 1; i <= count; i++) {
                        Mat gaussianNoise = addNoise(image);
                        Imgcodecs.imwrite(noiseSetDir.getPath() + "\\" + i + "_" + file.getName(), gaussianNoise);
                    }
                }
            }
        }

        private Mat addNoise(Mat image) {
            Mat noise = Mat.zeros(image.rows(), image.cols(), image.type());
            Core.randn(noise, new Random().nextInt(11), new Random().nextInt(90) + 90);   //mean - среднее значение, stddev - стандартное отклонение
            Mat gaussianNoise = new Mat();
            Core.add(image, noise, gaussianNoise);
            return gaussianNoise;
        }
    }

    /**
     * Запуск
     */
    public static class Run extends Application {

        public static void main(String[] args) {
            // load the native OpenCV library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) {
            GaussNoiseAdder noiseAdder = new GaussNoiseAdder();
            noiseAdder.createSetImages(DIR_NAME, COUNT_NOISE_IMAGES);
            System.out.println("Generation noises images are done!");

            NoisyImagesCleaner noisyCleaner = new NoisyImagesCleaner();
            noisyCleaner.process(DIR_NAME);
            System.out.println("Delete noise is done!");
            System.exit(0);
        }
    }
}

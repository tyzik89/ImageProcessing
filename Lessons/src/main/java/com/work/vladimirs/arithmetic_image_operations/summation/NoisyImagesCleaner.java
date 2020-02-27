package com.work.vladimirs.arithmetic_image_operations.summation;

import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.Random;

/**
 * Сложение (усреднение) серии зашумленных изображений, для снижения уровня шума
 */
public class NoisyImagesCleaner {

    private static final String DIR_NAME = "src/main/resources/NoisyImagesCleaner/";
    private static final int COUNT_NOISE_IMAGES = 10;

    public void process() {

    }


    /**
     * Создание серии изображений, искажённых аддитивным гауссовым шумом
     * Т.е. имитация серии фотографий объектов, например объектов глубокого космоса.
     */
    public static class GaussNoiseAdder {

        public void createSetImages(int count) {
            createSetImages(NoisyImagesCleaner.class.getSimpleName(), count);
        }

        public void createSetImages(String pathToImage, int count) {
            File dir = new File(DIR_NAME);
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) return;
            for (File file : files) {
                if (!file.isFile()) continue;
                Mat image = Imgcodecs.imread(DIR_NAME + "\\" + file.getName());
                File noiseSetDir = new File(DIR_NAME + "\\" + file.getName().replaceAll("[.]\\D*", ""));
                noiseSetDir.mkdir();
                for (int i = 1; i <= count; i++) {
                    Mat gaussianNoise = addNoise(image);
                    Imgcodecs.imwrite(noiseSetDir.getPath() + "\\" + i + "_" + file.getName(), gaussianNoise);
                }
            }
        }

        private Mat addNoise(Mat image) {
            Mat noise = Mat.zeros(image.rows(), image.cols(), image.type());
            Core.randn(noise, new Random().nextInt(11), new Random().nextInt(81) + 81);   //mean - среднее значение, stddev - стандартное отклонение
            Mat gaussianNoise = image.clone();
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
        public void start(Stage primaryStage) throws Exception {
            GaussNoiseAdder noiseAdder = new GaussNoiseAdder();
            noiseAdder.createSetImages(COUNT_NOISE_IMAGES);
            System.out.println("Generation noises images are done!");

            NoisyImagesCleaner noisyCleaner = new NoisyImagesCleaner();
            noisyCleaner.process();
            System.out.println("Delete noise is done!");
            System.exit(0);
        }
    }
}

package com.work.vladimirs.arithmetic_image_operations.summation;

import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Сложение (усреднение) серии зашумленных изображений, для снижения уровня шума
 */
public class NoisyImagesCleaner {

    private static final String DIR_NAME = "src/main/resources/NoisyImagesCleaner/";
    private static final int COUNT_NOISE_IMAGES = 50;

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
            Mat avgImg = imagesAveraging(noisyImages);          //Делаем устреднение набора изображений
            Imgcodecs.imwrite(pathToImage + "result_" + file.getName(), avgImg);
        }
    }

    private Mat imagesAveraging(ArrayList<Mat> noisyImages) {
        int count = noisyImages.size();
        int cols = noisyImages.get(0).cols();
        int rows = noisyImages.get(0).rows();
        int channels = noisyImages.get(0).channels();
        int type = noisyImages.get(0).type();

        int[] tempIntArr = new int[cols * rows * channels];

        for (Mat noisyImage : noisyImages) {
            byte[] noisyImageArr = new byte[cols * rows * channels];
            noisyImage.get(0, 0, noisyImageArr);
            int color;
            for (int i = 0, j = noisyImageArr.length; i < j; i++) {
                color = noisyImageArr[i] & 0xFF;        //Получение беззнакового int (расширение до 32 бит без знака)
                tempIntArr[i] = tempIntArr[i] + color;
            }
        }

        byte[] resultImgArr = new byte[cols * rows * channels];
        for (int i = 0, j = tempIntArr.length; i < j; i++) {
            int val = tempIntArr[i] / count;
            val = val > 255 ? 255 : (val < 0 ? 0 : val);        //Проверка выхода за границы
            resultImgArr[i] = (byte) (val);
        }

        Mat resultImg = new Mat(rows, cols, type);
        resultImg.put(0, 0, resultImgArr);
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

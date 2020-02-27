package com.work.vladimirs.arithmetic_image_operations.summation;

import com.work.vladimirs.utils.ShowImage;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;

/**
 * Сложение (усреднение) серии зашумленных изображений, для снижения уровня шума
 */
public class NoisyImagesCleaner {

    public void process() {

    }

    /**
     * Создание серии изображений, искажённых аддитивным гауссовым шумом
     * Т.е. имитация серии фотографий объектов, например объектов глубокого космоса.
     */
    public static class GaussNoiseAdder {

        public void createSetImages() {
            createSetImages(NoisyImagesCleaner.class.getSimpleName());
        }

        public void createSetImages(String pathToImage) {
            File dir = new File(getClass().getClassLoader().getResource(pathToImage).getFile());
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isFile()) continue;
                Image image = new Image(file.toURI().toString());
                image = addNoise(image);
            }
        }

        private Image addNoise(Image image) {

            return new Image("");
        }
    }

    /**
     * Запуск тестирования
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
            noiseAdder.createSetImages();
        }
    }
}

package com.work.vladimirs.transformation_area_of_images.clean_sinusoidal_interference;

import org.opencv.core.Core;

/**
 * Изображение искажено синусоидальными помехами,
 * необходимо сделать преобразование Фурье, выявить помехи (яркие точки), наложить маску подавления энергии ярких точек
 * и получить чистое изображение.
 */
public class SinusoidalInterferenceCleaner {

    private static final String DIR_NAME = "src/main/resources/transformation_area_of_images/clean_sinusoidal_interference/SinusoidalInterferenceCleaner/";

    void process(String pathToImage) {

    }

    /**
     * Наложение на изображение синусоидального шума.
     * Синусоидальные помехи (СП) - помехи, сосредоточенные по спектру.
     */
    public static class SinusoidalInterferenceAdder {

    }

    /**
     * Запуск
     */
    public static class Run {

        public static void main(String[] args) {
            // load the native OpenCV library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("Start application");

            //todo run process

            System.out.println("Delete sinusoidal interference is done.");
            System.exit(0);
        }
    }
}
